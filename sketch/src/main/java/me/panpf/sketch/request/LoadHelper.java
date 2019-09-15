/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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
import android.text.TextUtils;
import android.widget.ImageView.ScaleType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ProcessedResultCacheProcessor;
import me.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;

@SuppressWarnings("WeakerAccess")
public class LoadHelper {

    private static final String NAME = "LoadHelper";

    @NonNull
    private Sketch sketch;
    @NonNull
    private final String uri;
    @Nullable
    private LoadListener loadListener;

    private boolean sync;
    @NonNull
    private final LoadOptions loadOptions;
    @Nullable
    private DownloadProgressListener downloadProgressListener;

    public LoadHelper(@NonNull Sketch sketch, @NonNull String uri, @Nullable LoadListener loadListener) {
        this.sketch = sketch;
        this.uri = uri;
        this.loadListener = loadListener;
        this.loadOptions = new DisplayOptions();
    }

    /**
     * Limit request processing depth
     */
    @NonNull
    public LoadHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            loadOptions.setRequestLevel(requestLevel);
        }
        return this;
    }

    @NonNull
    public LoadHelper disableCacheInDisk() {
        loadOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * Disabled get reusable {@link Bitmap} from {@link BitmapPool}
     */
    @NonNull
    public LoadHelper disableBitmapPool() {
        loadOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * Support gif images
     */
    @NonNull
    public LoadHelper decodeGifImage() {
        loadOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    @NonNull
    public LoadHelper maxSize(@Nullable MaxSize maxSize) {
        loadOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    @NonNull
    public LoadHelper maxSize(int maxWidth, int maxHeight) {
        loadOptions.setMaxSize(maxWidth, maxHeight);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public LoadHelper resize(@Nullable Resize resize) {
        loadOptions.setResize(resize);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public LoadHelper resize(int reWidth, int reHeight) {
        loadOptions.setResize(reWidth, reHeight);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public LoadHelper resize(int reWidth, int reHeight, @NonNull ScaleType scaleType) {
        loadOptions.setResize(reWidth, reHeight, scaleType);
        return this;
    }

    /**
     * Prioritize low quality {@link Bitmap.Config} when creating bitmaps, the priority is lower than the {@link #bitmapConfig(Bitmap.Config)} method
     */
    @NonNull
    public LoadHelper lowQualityImage() {
        loadOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * Modify Bitmap after decoding the image
     */
    @NonNull
    public LoadHelper processor(@Nullable ImageProcessor processor) {
        loadOptions.setProcessor(processor);
        return this;
    }

    /**
     * Specify {@link Bitmap.Config} to use when creating the bitmap.
     * KITKAT and above {@link Bitmap.Config#ARGB_4444} will be forced to be replaced with {@link Bitmap.Config#ARGB_8888}.
     * With priority higher than {@link #lowQualityImage()} method.
     * Applied to {@link android.graphics.BitmapFactory.Options#inPreferredConfig}
     */
    @NonNull
    public LoadHelper bitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        loadOptions.setBitmapConfig(bitmapConfig);
        return this;
    }

    /**
     * Priority is given to speed or quality when decoding. Applied to the {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed}
     */
    @NonNull
    public LoadHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        loadOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * Thumbnail mode, together with the {@link #resize(Resize)} method, gives a sharper thumbnail, see {@link ThumbnailModeDecodeHelper}
     */
    @NonNull
    public LoadHelper thumbnailMode() {
        loadOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * In order to speed up, save the image processed by {@link #processor(ImageProcessor)}, {@link #resize(Resize)} or {@link #thumbnailMode()} to the disk cache,
     * read it directly next time, refer to {@link ProcessedResultCacheProcessor}
     */
    @NonNull
    public LoadHelper cacheProcessedImageInDisk() {
        loadOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * Disabled correcting picture orientation
     */
    @NonNull
    public LoadHelper disableCorrectImageOrientation() {
        loadOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * Batch setting load parameters, all reset
     */
    @NonNull
    public LoadHelper options(@Nullable LoadOptions newOptions) {
        loadOptions.copy(newOptions);
        return this;
    }

    @NonNull
    public LoadHelper downloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    /**
     * Synchronous execution
     */
    @NonNull
    public LoadHelper sync() {
        this.sync = true;
        return this;
    }

    @Nullable
    // todo 支持协程 spend
    public LoadRequest commit() {
        // Cannot run on UI threads
        if (sync && SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot sync perform the load in the UI thread ");
        }

        // Uri cannot is empty
        if (TextUtils.isEmpty(uri)) {
            SLog.em(NAME, "Uri is empty");
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_INVALID, sync);
            return null;
        }

        // Uri type must be supported
        final UriModel uriModel = UriModel.match(sketch, uri);
        if (uriModel == null) {
            SLog.emf(NAME, "Unsupported uri type. %s", uri);
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_NO_SUPPORT, sync);
            return null;
        }

        processOptions();

        final String key = SketchUtils.makeRequestKey(uri, uriModel, loadOptions.makeKey());
        if (!checkRequestLevel(key, uriModel)) {
            return null;
        }

        return submitRequest(key, uriModel);
    }

    private void processOptions() {
        Configuration configuration = sketch.getConfiguration();

        // LoadRequest can not be used Resize.ByViewFixedSizeResize
        Resize resize = loadOptions.getResize();
        if (resize instanceof Resize.ByViewFixedSizeResize) {
            resize = null;
            loadOptions.setResize(null);
        }

        // The width and height of the Resize must be greater than 0
        if (resize != null && (resize.getWidth() <= 0 || resize.getHeight() <= 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // If MaxSize is not set, the default MaxSize is used.
        MaxSize maxSize = loadOptions.getMaxSize();
        if (maxSize == null) {
            maxSize = configuration.getSizeCalculator().getDefaultImageMaxSize(configuration.getContext());
            loadOptions.setMaxSize(maxSize);
        }

        // The width or height of MaxSize is greater than 0.
        if (maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // There is no ImageProcessor but there is a Resize, you need to set a default image cropping processor
        if (loadOptions.getProcessor() == null && resize != null) {
            loadOptions.setProcessor(configuration.getResizeProcessor());
        }

        configuration.getOptionsFilterManager().filter(loadOptions);
    }

    private boolean checkRequestLevel(@NonNull String key, @NonNull UriModel uriModel) {
        if (loadOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "Request cancel. %s. %s", CancelCause.PAUSE_DOWNLOAD, key);
            }

            CallbackHandler.postCallbackCanceled(loadListener, CancelCause.PAUSE_DOWNLOAD, sync);
            return false;
        }

        return true;
    }

    @NonNull
    private LoadRequest submitRequest(@NonNull String key, @NonNull UriModel uriModel) {
        CallbackHandler.postCallbackStarted(loadListener, sync);

        LoadRequest request = new LoadRequest(sketch, uri, uriModel, key, loadOptions, loadListener, downloadProgressListener);
        request.setSync(sync);

        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(NAME, "Run dispatch submitted. %s", key);
        }
        request.submit();

        return request;
    }
}
