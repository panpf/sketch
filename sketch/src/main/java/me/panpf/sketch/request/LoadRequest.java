/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.datasource.DataSource;
import me.panpf.sketch.decode.BitmapDecodeResult;
import me.panpf.sketch.decode.DecodeException;
import me.panpf.sketch.decode.DecodeResult;
import me.panpf.sketch.decode.GifDecodeResult;
import me.panpf.sketch.decode.ProcessedImageCache;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.uri.GetDataSourceException;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

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
     * 获取数据源
     */
    @NonNull
    public DataSource getDataSource() throws GetDataSourceException {
        DownloadResult downloadResult = getUriModel().isFromNet() ? getDownloadResult() : null;
        return getUriModel().getDataSource(getContext(), getUri(), downloadResult);
    }

    /**
     * 获取数据源，优先考虑已处理缓存
     */
    @NonNull
    public DataSource getDataSourceWithPressedCache() throws GetDataSourceException {
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
    protected void doError(@NonNull ErrorCause errorCause) {
        super.doError(errorCause);

        if (loadListener != null) {
            postRunError();
        }
    }

    @Override
    protected void doCancel(@NonNull CancelCause cancelCause) {
        super.doCancel(cancelCause);

        if (loadListener != null) {
            postRunCanceled();
        }
    }

    @Override
    protected void runDispatch() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Request end before dispatch. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        setStatus(Status.INTERCEPT_LOCAL_TASK);

        if (getUriModel().isFromNet()) {
            // 是网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            ProcessedImageCache processedImageCache = getConfiguration().getProcessedImageCache();
            if (processedImageCache.canUse(getOptions()) && processedImageCache.checkDiskCache(this)) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "Dispatch. Processed disk cache. %s. %s", getThreadName(), getKey());
                }
                submitRunLoad();
            } else {
                super.runDispatch();
            }
        } else {
            // 本地请求直接执行加载
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Dispatch. Local image. %s. %s", getThreadName(), getKey());
            }
            submitRunLoad();
        }
    }

    @Override
    protected void downloadCompleted() {
        DownloadResult downloadResult = getDownloadResult();
        if (downloadResult != null && downloadResult.hasData()) {
            submitRunLoad();
        } else {
            SLog.e(getLogName(), "Not found data after download completed. %s. %s", getThreadName(), getKey());
            doError(ErrorCause.DATA_LOST_AFTER_DOWNLOAD_COMPLETED);
        }
    }

    @Override
    protected void runLoad() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Request end before decode. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        setStatus(Status.DECODING);
        DecodeResult decodeResult;
        try {
            decodeResult = getConfiguration().getDecoder().decode(this);
        } catch (DecodeException e) {
            e.printStackTrace();
            doError(e.getErrorCause());
            return;
        }

        if (decodeResult instanceof BitmapDecodeResult) {
            Bitmap bitmap = ((BitmapDecodeResult) decodeResult).getBitmap();

            if (bitmap.isRecycled()) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.e(getLogName(), "Decode failed because bitmap recycled. bitmapInfo: %s. %s. %s", imageInfo, getThreadName(), getKey());
                doError(ErrorCause.BITMAP_RECYCLED);
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.d(getLogName(), "Decode success. bitmapInfo: %s. %s. %s", imageInfo, getThreadName(), getKey());
            }

            if (isCanceled()) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, getConfiguration().getBitmapPool());

                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "Request end after decode. %s. %s", getThreadName(), getKey());
                }
                return;
            }

            loadResult = new LoadResult(bitmap, decodeResult);
            loadCompleted();
        } else if (decodeResult instanceof GifDecodeResult) {
            SketchGifDrawable gifDrawable = ((GifDecodeResult) decodeResult).getGifDrawable();

            if (gifDrawable.isRecycled()) {
                SLog.e(getLogName(), "Decode failed because gif drawable recycled. gifInfo: %s. %s. %s",
                        gifDrawable.getInfo(), getThreadName(), getKey());
                doError(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return;
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Decode gif success. gifInfo: %s. %s. %s", gifDrawable.getInfo(), getThreadName(), getKey());
            }

            if (isCanceled()) {
                gifDrawable.recycle();

                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(getLogName(), "Request end after decode. %s. %s", getThreadName(), getKey());
                }
                return;
            }

            loadResult = new LoadResult(gifDrawable, decodeResult);
            loadCompleted();
        } else {
            SLog.e(getLogName(), "Unknown DecodeResult type. %S. %s. %s", decodeResult.getClass().getName(), getThreadName(), getKey());
            doError(ErrorCause.DECODE_UNKNOWN_RESULT_TYPE);
        }
    }

    protected void loadCompleted() {
        postRunCompleted();
    }

    @Override
    protected void runCompletedInMainThread() {
        if (isCanceled()) {
            if (loadResult != null && loadResult.getBitmap() != null) {
                BitmapPoolUtils.freeBitmapToPool(loadResult.getBitmap(), getConfiguration().getBitmapPool());
            } else if (loadResult != null && loadResult.getGifDrawable() != null) {
                loadResult.getGifDrawable().recycle();
            }

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Request end before call completed. %s. %s", getThreadName(), getKey());
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
                SLog.d(getLogName(), "Request end before call err. %s. %s", getThreadName(), getKey());
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
