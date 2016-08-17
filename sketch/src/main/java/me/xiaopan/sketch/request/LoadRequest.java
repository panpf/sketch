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

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.DecodeResult;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.feature.ExceptionMonitor;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.PreProcessResult;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载请求
 */
public class LoadRequest extends DownloadRequest {
    private LoadOptions loadOptions;
    private LoadListener loadListener;

    private DataSource dataSource;
    private LoadResult loadResult;

    public LoadRequest(
            Sketch sketch, RequestAttrs requestAttrs,
            LoadOptions loadOptions, LoadListener loadListener,
            DownloadProgressListener downloadProgressListener) {
        super(sketch, requestAttrs, loadOptions, null, downloadProgressListener);

        this.loadOptions = loadOptions;
        this.loadListener = loadListener;

        setLogName("LoadRequest");
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

    /**
     * 设置加载结果
     */
    @SuppressWarnings("unused")
    protected void setLoadResult(LoadResult loadResult) {
        this.loadResult = loadResult;
    }

    /**
     * 获取数据源
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void failed(FailedCause failedCause) {
        super.failed(failedCause);

        if (loadListener != null) {
            postRunFailed();
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
            if (Sketch.isDebugMode()) {
                printLogW("runDispatch", "canceled", "intercept local task before");
            }
            return;
        }

        // 本地请求直接执行加载
        setStatus(Status.INTERCEPT_LOCAL_TASK);
        if (getAttrs().getUriScheme() != UriScheme.NET) {
            if (Sketch.isDebugMode()) {
                printLogD("runDispatch", "local");
            }
            submitRunLoad();
            return;
        }

        super.runDispatch();
    }

    @Override
    protected void downloadCompleted() {
        DownloadResult downloadResult = getDownloadResult();
        if (downloadResult != null && downloadResult.getDiskCacheEntry() != null) {
            dataSource = new DataSource(downloadResult.getDiskCacheEntry(), downloadResult.getImageFrom());
            submitRunLoad();
        } else if (downloadResult != null && downloadResult.getImageData() != null && downloadResult.getImageData().length > 0) {
            dataSource = new DataSource(downloadResult.getImageData(), downloadResult.getImageFrom());
            submitRunLoad();
        } else {
            if (Sketch.isDebugMode()) {
                printLogE("downloadCompleted", "are all null");
            }
            failed(FailedCause.DOWNLOAD_FAIL);
        }
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runLoad", "canceled", "start load");
            }
            return;
        }

        // 预处理
        ImagePreprocessor imagePreprocessor = getSketch().getConfiguration().getImagePreprocessor();
        if (imagePreprocessor.isSpecific(this)) {
            setStatus(Status.PRE_PROCESS);
            PreProcessResult prePrecessResult = imagePreprocessor.process(this);
            if (prePrecessResult != null && prePrecessResult.diskCacheEntry != null) {
                dataSource = new DataSource(prePrecessResult.diskCacheEntry, prePrecessResult.imageFrom);
            } else if (prePrecessResult != null && prePrecessResult.imageData != null) {
                dataSource = new DataSource(prePrecessResult.imageData, prePrecessResult.imageFrom);
            } else {
                failed(FailedCause.PRE_PROCESS_RESULT_IS_NULL);
                return;
            }
        }

        // 解码
        setStatus(Status.DECODING);
        DecodeResult decodeResult = getSketch().getConfiguration().getImageDecoder().decode(this);

        if (decodeResult != null && decodeResult.getBitmap() != null) {
            Bitmap bitmap = decodeResult.getBitmap();

            if (bitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("runLoad", "decode failed", "bitmap recycled", "bitmapInfo: " + SketchUtils.getInfo(null, bitmap, decodeResult.getMimeType()));
                }
                failed(FailedCause.BITMAP_RECYCLED);
                return;
            }

            if (Sketch.isDebugMode()) {
                printLogI("runLoad", "decode success", "bitmapInfo: " + SketchUtils.getInfo(null, bitmap, decodeResult.getMimeType()));
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    printLogW("runLoad", "canceled", "decode after", "bitmapInfo: " + SketchUtils.getInfo(null, bitmap, decodeResult.getMimeType()));
                }
                bitmap.recycle();
                return;
            }

            // 处理一下
            ImageProcessor imageProcessor = loadOptions.getImageProcessor();
            if (imageProcessor != null) {
                setStatus(Status.PROCESSING);

                Bitmap newBitmap = null;
                try {
                    newBitmap = imageProcessor.process(
                            getSketch(), bitmap,
                            loadOptions.getResize(), loadOptions.isForceUseResize(),
                            loadOptions.isLowQualityImage());
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    ExceptionMonitor exceptionMonitor = getSketch().getConfiguration().getExceptionMonitor();
                    exceptionMonitor.onProcessImageFailed(e, getAttrs().getId(), imageProcessor);
                }

                // 确实是一张新图片，就替换掉旧图片
                if (newBitmap != null && !newBitmap.isRecycled() && newBitmap != bitmap) {
                    if (Sketch.isDebugMode()) {
                        printLogW("runLoad", "process new bitmap", "bitmapInfo: " + SketchUtils.getInfo(null, newBitmap, decodeResult.getMimeType()));
                    }
                    bitmap.recycle();
                    bitmap = newBitmap;
                } else {
                    // 有可能处理后没得到新图片就图片也没了，这叫赔了夫人又折兵
                    if (bitmap.isRecycled()) {
                        failed(FailedCause.SOURCE_BITMAP_RECYCLED);
                        return;
                    }
                }

                if (isCanceled()) {
                    if (Sketch.isDebugMode()) {
                        printLogW("runLoad", "canceled", "process after", "bitmapInfo: " + SketchUtils.getInfo(null, bitmap, decodeResult.getMimeType()));
                    }
                    bitmap.recycle();
                    return;
                }
            }

            loadResult = new LoadResult(bitmap, decodeResult);
            loadCompleted();
        } else if (decodeResult != null && decodeResult.getGifDrawable() != null) {
            SketchGifDrawable gifDrawable = decodeResult.getGifDrawable();

            if (gifDrawable.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    printLogE("runLoad", "decode failed", "gif drawable recycled", "gifInfo: " + SketchUtils.getInfo(gifDrawable));
                }
                failed(FailedCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            if (Sketch.isDebugMode()) {
                printLogI("runLoad", "decode gif success", "gifInfo: " + SketchUtils.getInfo(gifDrawable));
            }

            if (isCanceled()) {
                if (Sketch.isDebugMode()) {
                    printLogW("runLoad", "canceled", "decode after", "gifInfo: " + SketchUtils.getInfo(gifDrawable));
                }
                gifDrawable.recycle();
                return;
            }

            loadResult = new LoadResult(gifDrawable, decodeResult);
            loadCompleted();
        } else {
            if (Sketch.isDebugMode()) {
                printLogE("runLoad", "are all null");
            }
            failed(FailedCause.DECODE_FAIL);
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
                    loadResult.getBitmap().recycle();
                }
                if (loadResult.getGifDrawable() != null) {
                    loadResult.getGifDrawable().recycle();
                }
            }
            if (Sketch.isDebugMode()) {
                printLogW("runCompletedInMainThread", "canceled");
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult);
        }
    }

    @Override
    protected void runFailedInMainThread() {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                printLogW("runFailedInMainThread", "canceled");
            }
            return;
        }

        if (loadListener != null) {
            loadListener.onFailed(getFailedCause());
        }
    }

    @Override
    protected void runCanceledInMainThread() {
        if (loadListener != null) {
            loadListener.onCanceled(getCancelCause());
        }
    }
}
