/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.request;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.cache.BitmapPoolUtils;
import com.github.panpf.sketch.datasource.DataSource;
import com.github.panpf.sketch.decode.BitmapDecodeResult;
import com.github.panpf.sketch.decode.DecodeException;
import com.github.panpf.sketch.decode.DecodeResult;
import com.github.panpf.sketch.decode.GifDecodeResult;
import com.github.panpf.sketch.decode.ImageAttrs;
import com.github.panpf.sketch.decode.TransformCacheManager;
import com.github.panpf.sketch.drawable.SketchGifDrawable;
import com.github.panpf.sketch.uri.GetDataSourceException;
import com.github.panpf.sketch.uri.UriModel;
import com.github.panpf.sketch.util.SketchUtils;

public class LoadRequest extends DownloadRequest {
    @Nullable
    private LoadListener loadListener;

    @Nullable
    private DownloadResult downloadResult;
    @Nullable
    private LoadResult loadResult;

    public LoadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull LoadOptions loadOptions,
                       @Nullable LoadListener loadListener, @Nullable DownloadProgressListener downloadProgressListener, @NonNull String logModule) {
        super(sketch, uri, uriModel, key, loadOptions, null, downloadProgressListener, logModule);
        this.loadListener = loadListener;
    }

    public LoadRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull LoadOptions loadOptions,
                       @Nullable LoadListener loadListener, @Nullable DownloadProgressListener downloadProgressListener) {
        this(sketch, uri, uriModel, key, loadOptions, loadListener, downloadProgressListener, "LoadRequest");
    }

    @NonNull
    @Override
    public LoadOptions getOptions() {
        return (LoadOptions) super.getOptions();
    }

    @NonNull
    public String getTransformCacheKey() {
        return getKey();
    }

    @NonNull
    public DataSource getDataSource(boolean disableTransformCache) throws GetDataSourceException {
        if (!disableTransformCache) {
            TransformCacheManager transformCacheManager = getConfiguration().getTransformCacheManager();
            if (transformCacheManager.canUse(getOptions())) {
                DataSource dataSource = transformCacheManager.getDiskCache(this);
                if (dataSource != null) {
                    return dataSource;
                }
            }
        }

        DownloadResult downloadResult = getUriModel().isFromNet() ? this.downloadResult : null;
        return getUriModel().getDataSource(getContext(), getUri(), downloadResult);
    }

    @Override
    protected void doError(@NonNull ErrorCause errorCause) {
        super.doError(errorCause);

        if (loadListener != null) {
            postToMainRunError();
        }
    }

    @Override
    protected void doCancel(@NonNull CancelCause cancelCause) {
        super.doCancel(cancelCause);

        if (loadListener != null) {
            postToMainRunCanceled();
        }
    }

    @Nullable
    @Override
    protected DispatchResult runDispatch() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG))
                SLog.dmf(getLogName(), "Request end before dispatch. %s. %s", getThreadName(), getKey());
            return null;
        }

        setStatus(Status.INTERCEPT_LOCAL_TASK);

        TransformCacheManager transformCacheManager = getConfiguration().getTransformCacheManager();
        if (!getUriModel().isFromNet()) {
            if (SLog.isLoggable(SLog.DEBUG))
                SLog.dmf(getLogName(), "Dispatch. Local image. %s. %s", getThreadName(), getKey());
            return new RunLoadResult();
        } else if (transformCacheManager.canUse(getOptions()) && transformCacheManager.checkDiskCache(this)) {
            // 网络图片但是本地已经有缓存好的且经过处理的缓存图片可以直接用
            if (SLog.isLoggable(SLog.DEBUG))
                SLog.dmf(getLogName(), "Dispatch. Processed disk cache. %s. %s", getThreadName(), getKey());
            return new RunLoadResult();
        } else {
            return super.runDispatch();
        }
    }

    @Override
    public void onRunDownloadFinished(@Nullable DownloadResult result) {
        this.downloadResult = result;
        if (result != null) {
            submitLoad();
        }
    }

    @Override
    LoadResult runLoad() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before decode. %s. %s", getThreadName(), getKey());
            }
            return null;
        }

        setStatus(Status.DECODING);
        DecodeResult decodeResult;
        try {
            decodeResult = getConfiguration().getDecoder().decode(this);
        } catch (DecodeException e) {
            e.printStackTrace();
            doError(e.getErrorCause());
            return null;
        }

        if (decodeResult instanceof BitmapDecodeResult) {
            Bitmap bitmap = ((BitmapDecodeResult) decodeResult).getBitmap();

            if (bitmap.isRecycled()) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.emf(getLogName(), "Decode failed because bitmap recycled. bitmapInfo: %s. %s. %s", imageInfo, getThreadName(), getKey());
                doError(ErrorCause.BITMAP_RECYCLED);
                return null;
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                ImageAttrs imageAttrs = decodeResult.getImageAttrs();
                String imageInfo = SketchUtils.makeImageInfo(null, imageAttrs.getWidth(),
                        imageAttrs.getHeight(), imageAttrs.getMimeType(),
                        imageAttrs.getExifOrientation(), bitmap, SketchUtils.getByteCount(bitmap), null);
                SLog.dmf(getLogName(), "Decode success. bitmapInfo: %s. %s. %s", imageInfo, getThreadName(), getKey());
            }

            if (isCanceled()) {
                BitmapPoolUtils.freeBitmapToPool(bitmap, getConfiguration().getBitmapPool());

                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(getLogName(), "Request end after decode. %s. %s", getThreadName(), getKey());
                }
                return null;
            }

            return new BitmapLoadResult(bitmap, decodeResult.getImageAttrs(), decodeResult.getImageFrom());
        } else if (decodeResult instanceof GifDecodeResult) {
            SketchGifDrawable gifDrawable = ((GifDecodeResult) decodeResult).getGifDrawable();

            if (gifDrawable.isRecycled()) {
                SLog.emf(getLogName(), "Decode failed because gif drawable recycled. gifInfo: %s. %s. %s",
                        gifDrawable.getInfo(), getThreadName(), getKey());
                doError(ErrorCause.GIF_DRAWABLE_RECYCLED);
                return null;
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Decode gif success. gifInfo: %s. %s. %s", gifDrawable.getInfo(), getThreadName(), getKey());
            }

            if (isCanceled()) {
                gifDrawable.recycle();

                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(getLogName(), "Request end after decode. %s. %s", getThreadName(), getKey());
                }
                return null;
            }

            return new GifLoadResult(gifDrawable, decodeResult.getImageAttrs(), decodeResult.getImageFrom());
        } else {
            SLog.emf(getLogName(), "Unknown DecodeResult type. %S. %s. %s", decodeResult.getClass().getName(), getThreadName(), getKey());
            doError(ErrorCause.DECODE_UNKNOWN_RESULT_TYPE);
            return null;
        }
    }

    @Override
    void onRunLoadFinished(@Nullable LoadResult result) {
        this.loadResult = result;
        if (result != null) {
            postRunCompleted();
        }
    }

    @Override
    protected void runCompletedInMain() {
        if (isCanceled()) {
            if (loadResult instanceof BitmapLoadResult) {
                BitmapPoolUtils.freeBitmapToPool(((BitmapLoadResult) loadResult).getBitmap(), getConfiguration().getBitmapPool());
            } else if (loadResult instanceof GifLoadResult) {
                ((GifLoadResult) loadResult).getGifDrawable().recycle();
            }

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call completed. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        setStatus(Status.COMPLETED);

        if (loadListener != null && loadResult != null) {
            loadListener.onCompleted(loadResult);
        }
    }

    @Override
    protected void runErrorInMain() {
        if (isCanceled()) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request end before call err. %s. %s", getThreadName(), getKey());
            }
            return;
        }

        if (loadListener != null && getErrorCause() != null) {
            loadListener.onError(getErrorCause());
        }
    }

    @Override
    protected void runCanceledInMain() {
        if (loadListener != null && getCancelCause() != null) {
            loadListener.onCanceled(getCancelCause());
        }
    }
}
