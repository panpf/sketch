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

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.datasource.DataSource;
import me.xiaopan.sketch.decode.BitmapDecodeResult;
import me.xiaopan.sketch.decode.DecodeException;
import me.xiaopan.sketch.decode.DecodeResult;
import me.xiaopan.sketch.decode.GifDecodeResult;
import me.xiaopan.sketch.decode.ProcessedImageCache;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class LoadRequest extends FreeRideDownloadRequest {
    private LoadOptions loadOptions;
    private LoadListener loadListener;

    private LoadResult loadResult;

    public LoadRequest(Sketch sketch, String uri, UriModel uriModel, String key, LoadOptions loadOptions,
                       LoadListener loadListener, DownloadProgressListener downloadProgressListener) {
        super(sketch, uri, uriModel, key, loadOptions, null, downloadProgressListener);

        this.loadOptions = loadOptions;
        this.loadListener = loadListener;

        setLogName("LoadRequest");
    }

    /**
     * 获取已处理功能使用的磁盘缓存 key
     */
    public String getProcessedDiskCacheKey() {
        return getKey();
    }

    /**
     * 获取数据源，优先考虑已处理缓存
     */
    public DataSource getDataSourceWithPressedCache() {
        ProcessedImageCache processedImageCache = getConfiguration().getProcessedImageCache();
        if (processedImageCache.canUse(getOptions())) {
            DataSource dataSource = processedImageCache.getDiskCache(this);
            if (dataSource != null) {
                return dataSource;
            }
        }

        return getDataSource();
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runDispatch. load request just start. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        setStatus(Status.INTERCEPT_LOCAL_TASK);

        if (!getUriModel().isFromNet()) {
            // 本地请求直接执行加载
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "local thread. local image. runDispatch. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            submitRunLoad();
            return;
        } else {
            ProcessedImageCache processedImageCache = getConfiguration().getProcessedImageCache();
            // 是网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            if (processedImageCache.canUse(getOptions()) && processedImageCache.checkDiskCache(this)) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "local thread. disk cache image. runDispatch. %s. %s",
                            Thread.currentThread().getName(), getKey());
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
            SLog.e(getLogName(), "Not found data after download completed. %s. %s",
                    Thread.currentThread().getName(), getKey());
            error(ErrorCause.DATA_LOST_AFTER_DOWNLOAD_COMPLETED);
        }
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runLoad. load request just start. %s. %s",
                        Thread.currentThread().getName(), getKey());
            }
            return;
        }

        // 解码
        setStatus(Status.DECODING);
        DecodeResult decodeResult;
        try {
            decodeResult = getConfiguration().getDecoder().decode(this);
        } catch (DecodeException e) {
            e.printStackTrace();
            error(e.getErrorCause());
            return;
        }

        if (decodeResult != null && decodeResult instanceof BitmapDecodeResult) {
            Bitmap bitmap = ((BitmapDecodeResult) decodeResult).getBitmap();

            if (bitmap.isRecycled()) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.e(getLogName(), "decode failed. runLoad. bitmap recycled. bitmapInfo: %s. %s. %s",
                        imageInfo, Thread.currentThread().getName(), getKey());
                error(ErrorCause.BITMAP_RECYCLED);
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.d(getLogName(), "decode success. runLoad. bitmapInfo: %s. %s. %s", imageInfo, Thread.currentThread().getName(), getKey());
            }

            if (isCanceled()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                    String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                            imageAttrs.getHeight(), imageAttrs.getMimeType(),
                            imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                    SLog.d(getLogName(), "canceled. runLoad. decode after. bitmapInfo: %s. %s. %s",
                            imageInfo, Thread.currentThread().getName(), getKey());
                }
                BitmapPoolUtils.freeBitmapToPool(bitmap, getConfiguration().getBitmapPool());
                return;
            }

            loadResult = new LoadResult(bitmap, decodeResult);
            loadCompleted();
        } else if (decodeResult != null && decodeResult instanceof GifDecodeResult) {
            SketchGifDrawable gifDrawable = ((GifDecodeResult) decodeResult).getGifDrawable();

            if (gifDrawable.isRecycled()) {
                SLog.e(getLogName(), "decode failed. runLoad. gif drawable recycled. gifInfo: %s. %s. %s",
                        gifDrawable.getInfo(), Thread.currentThread().getName(), getKey());
                error(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "decode gif success. runLoad. gifInfo: %s. %s. %s",
                        gifDrawable.getInfo(), Thread.currentThread().getName(), getKey());
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG) && isCanceled()) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "canceled. runLoad. decode after. gifInfo: %s. %s. %s",
                            gifDrawable.getInfo(), Thread.currentThread().getName(), getKey());
                }
                gifDrawable.recycle();
                return;
            }

            loadResult = new LoadResult(gifDrawable, decodeResult);
            loadCompleted();
        } else {
            SLog.e(getLogName(), "Not found data after decode. %s. %s", Thread.currentThread().getName(), getKey());
            error(ErrorCause.DATA_LOST_AFTER_DECODE);
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runCompletedInMainThread. %s. %s",
                        Thread.currentThread().getName(), getKey());
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "canceled. runErrorInMainThread. %s. %s",
                        Thread.currentThread().getName(), getKey());
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
