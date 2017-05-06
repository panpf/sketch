/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketch.request;

import android.graphics.Bitmap;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.decode.BitmapDecodeResult;
import me.xiaopan.sketch.decode.DecodeException;
import me.xiaopan.sketch.decode.DecodeResult;
import me.xiaopan.sketch.decode.GifDecodeResult;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.PreProcessResult;
import me.xiaopan.sketch.feature.ProcessedImageCache;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class LoadRequest extends FreeRideDownloadRequest {
    private LoadOptions loadOptions;
    private LoadListener loadListener;

    private LoadResult loadResult;

    public LoadRequest(
            Sketch sketch, LoadInfo info,
            LoadOptions loadOptions, LoadListener loadListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, info, loadOptions, null, downloadProgressListener);

        this.loadOptions = loadOptions;
        this.loadListener = loadListener;

        setLogName("LoadRequest");
    }

    /**
     * 获取磁盘缓存key
     */
    public String getProcessedImageDiskCacheKey() {
        return ((LoadInfo) getInfo()).getProcessedImageDiskCacheKey();
    }

    /**
     * 获取加载选项
     */
    @Override
    public LoadOptions getOptions() {
        return loadOptions;
    }

    /**
     * 获取加载结果
     */
    public LoadResult getLoadResult() {
        return loadResult;
    }

    @Override
    public void error(ErrorCause errorCause) {
        super.error(errorCause);

        if (loadListener != null) {
            postRunError();
        }
    }

    @Override
    public void canceled(CancelCause cancelCause) {
        super.canceled(cancelCause);

        if (loadListener != null) {
            postRunCanceled();
        }
    }

    @Override
    protected void runDispatch() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runDispatch", "load request just start");
            }
            return;
        }

        setStatus(Status.INTERCEPT_LOCAL_TASK);

        if (getUriScheme() != UriScheme.NET) {
            // 本地请求直接执行加载
            if (SLogType.REQUEST.isEnabled()) {
                printLogD("local thread", "local image", "runDispatch");
            }
            submitRunLoad();
            return;
        } else {
            ProcessedImageCache processedImageCache = getConfiguration().getProcessedImageCache();
            // 是网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            if (processedImageCache.canUse(getOptions()) && processedImageCache.existProcessedImageDiskCache(this)) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogD("local thread", "disk cache image", "runDispatch");
                }
                submitRunLoad();
                return;
            }
        }

        super.runDispatch();
    }

    @Override
    protected void downloadCompleted() {
        DownloadResult downloadResult = getDownloadResult();
        if (downloadResult != null && downloadResult.hasData()) {
            submitRunLoad();
        } else {
            if (SLogType.REQUEST.isEnabled()) {
                printLogE("are all null", "downloadCompleted");
            }
            error(ErrorCause.DOWNLOAD_FAIL);
        }
    }

    public PreProcessResult doPreProcess() {
        setStatus(Status.PRE_PROCESS);
        return getConfiguration().getImagePreprocessor().process(this);
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runLoad", "load request just start");
            }
            return;
        }

        // 解码
        setStatus(Status.DECODING);
        DecodeResult decodeResult;
        try {
            decodeResult = getConfiguration().getImageDecoder().decode(this);
        } catch (DecodeException e) {
            e.printStackTrace();
            error(e.getErrorCause());
            return;
        }

        if (decodeResult != null && decodeResult instanceof BitmapDecodeResult) {
            Bitmap bitmap = ((BitmapDecodeResult) decodeResult).getBitmap();

            if (bitmap.isRecycled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogE("decode failed", "runLoad", "bitmap recycled", "bitmapInfo: "
                            + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
                }
                error(ErrorCause.BITMAP_RECYCLED);
                return;
            }

            if (SLogType.REQUEST.isEnabled()) {
                printLogI("decode success", "runLoad", "bitmapInfo: "
                        + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
            }

            if (isCanceled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogW("canceled", "runLoad", "decode after", "bitmapInfo: "
                            + SketchUtils.makeImageInfo(null, bitmap, decodeResult.getImageAttrs().getMimeType()));
                }
                BitmapPoolUtils.freeBitmapToPool(bitmap, getConfiguration().getBitmapPool());
                return;
            }

            loadResult = new LoadResult(bitmap, decodeResult);
            loadCompleted();
        } else if (decodeResult != null && decodeResult instanceof GifDecodeResult) {
            SketchGifDrawable gifDrawable = ((GifDecodeResult) decodeResult).getGifDrawable();

            if (gifDrawable.isRecycled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogE("decode failed", "runLoad", "gif drawable recycled", "gifInfo: " + gifDrawable.getInfo());
                }
                error(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            if (SLogType.REQUEST.isEnabled()) {
                printLogI("decode gif success", "runLoad", "gifInfo: " + gifDrawable.getInfo());
            }

            if (isCanceled()) {
                if (SLogType.REQUEST.isEnabled()) {
                    printLogW("runLoad", "runLoad", "decode after", "gifInfo: " + gifDrawable.getInfo());
                }
                gifDrawable.recycle();
                return;
            }

            loadResult = new LoadResult(gifDrawable, decodeResult);
            loadCompleted();
        } else {
            if (SLogType.REQUEST.isEnabled()) {
                printLogE("are all null", "runLoad");
            }
            error(ErrorCause.DECODE_FAIL);
        }
    }

    protected void loadCompleted() {
        postRunCompleted();
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            // 已经取消了就直接把图片回收了
            if (loadResult != null) {
                if (loadResult.getBitmap() != null) {
                    BitmapPoolUtils.freeBitmapToPool(loadResult.getBitmap(), getConfiguration().getBitmapPool());
                }
                if (loadResult.getGifDrawable() != null) {
                    loadResult.getGifDrawable().recycle();
                }
            }
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runCompletedInMainThread");
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult);
        }
    }

    @Override
    protected void runErrorInMainThread() {
        if (isCanceled()) {
            if (SLogType.REQUEST.isEnabled()) {
                printLogW("canceled", "runErrorInMainThread");
            }
            return;
        }

        if (loadListener != null) {
            loadListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (loadListener != null) {
            loadListener.onCanceled(getCancelCause());
        }
    }
}
