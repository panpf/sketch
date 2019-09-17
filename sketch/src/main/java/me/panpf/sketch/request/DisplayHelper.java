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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageSizeCalculator;
import me.panpf.sketch.decode.ProcessedResultCacheProcessor;
import me.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import me.panpf.sketch.display.ImageDisplayer;
import me.panpf.sketch.display.TransitionImageDisplayer;
import me.panpf.sketch.drawable.SketchBitmapDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.drawable.SketchRefDrawable;
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.state.StateImage;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.util.Stopwatch;

@SuppressWarnings("WeakerAccess")
public class DisplayHelper {
    private static final String NAME = "DisplayHelper";

    @NonNull
    private final Sketch sketch;
    @NonNull
    private final String uri;
    @NonNull
    private final SketchView sketchView;

    @NonNull
    private final DisplayOptions displayOptions;
    @Nullable
    private final DisplayListener displayListener;
    @Nullable
    private final DownloadProgressListener downloadProgressListener;

    public DisplayHelper(@NonNull Sketch sketch, @NonNull String uri, @NonNull SketchView sketchView) {
        this.sketch = sketch;
        this.uri = uri;
        this.sketchView = sketchView;
        this.displayListener = sketchView.getDisplayListener();
        this.downloadProgressListener = sketchView.getDownloadProgressListener();

        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().start(NAME + ". display use time");
        }

        // onDisplay 一定要在最前面执行，因为 在onDisplay 中会设置一些属性，这些属性会影响到后续一些 get 方法返回的结果
        this.sketchView.onReadyDisplay(uri);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("onReadyDisplay");
        }

        this.displayOptions = new DisplayOptions(sketchView.getOptions());
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("init");
        }
    }

    /**
     * Limit request processing depth
     */
    @NonNull
    public DisplayHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            displayOptions.setRequestLevel(requestLevel);
        }
        return this;
    }

    @NonNull
    public DisplayHelper disableCacheInDisk() {
        displayOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * Disabled get reusable {@link Bitmap} from {@link BitmapPool}
     */
    @NonNull
    public DisplayHelper disableBitmapPool() {
        displayOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * Support gif images
     */
    @NonNull
    public DisplayHelper decodeGifImage() {
        displayOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    @NonNull
    public DisplayHelper maxSize(@Nullable MaxSize maxSize) {
        displayOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    @NonNull
    public DisplayHelper maxSize(int maxWidth, int maxHeight) {
        displayOptions.setMaxSize(maxWidth, maxHeight);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public DisplayHelper resize(@Nullable Resize resize) {
        displayOptions.setResize(resize);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public DisplayHelper resize(int reWidth, int reHeight) {
        displayOptions.setResize(reWidth, reHeight);
        return this;
    }

    /**
     * The size of the desired bitmap
     */
    @NonNull
    public DisplayHelper resize(int reWidth, int reHeight, @NonNull ScaleType scaleType) {
        displayOptions.setResize(reWidth, reHeight, scaleType);
        return this;
    }

    /**
     * Prioritize low quality {@link Bitmap.Config} when creating bitmaps, the priority is lower than the {@link #bitmapConfig(Bitmap.Config)} method
     */
    @NonNull
    public DisplayHelper lowQualityImage() {
        displayOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * Modify Bitmap after decoding the image
     */
    @NonNull
    public DisplayHelper processor(@Nullable ImageProcessor processor) {
        displayOptions.setProcessor(processor);
        return this;
    }

    /**
     * Specify {@link Bitmap.Config} to use when creating the bitmap.
     * KITKAT and above {@link Bitmap.Config#ARGB_4444} will be forced to be replaced with {@link Bitmap.Config#ARGB_8888}.
     * With priority higher than {@link #lowQualityImage()} method.
     * Applied to {@link android.graphics.BitmapFactory.Options#inPreferredConfig}
     */
    @NonNull
    public DisplayHelper bitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        displayOptions.setBitmapConfig(bitmapConfig);
        return this;
    }

    /**
     * Priority is given to speed or quality when decoding. Applied to the {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed}
     */
    @NonNull
    public DisplayHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        displayOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * Thumbnail mode, together with the {@link #resize(Resize)} method, gives a sharper thumbnail, see {@link ThumbnailModeDecodeHelper}
     */
    @NonNull
    public DisplayHelper thumbnailMode() {
        displayOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * In order to speed up, save the image processed by {@link #processor(ImageProcessor)}, {@link #resize(Resize)} or {@link #thumbnailMode()} to the disk cache,
     * read it directly next time, refer to {@link ProcessedResultCacheProcessor}
     */
    @NonNull
    public DisplayHelper cacheProcessedImageInDisk() {
        displayOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * Disabled correcting picture orientation
     */
    @NonNull
    public DisplayHelper disableCorrectImageOrientation() {
        displayOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    @NonNull
    public DisplayHelper disableCacheInMemory() {
        displayOptions.setCacheInMemoryDisabled(true);
        return this;
    }

    /**
     * Placeholder image displayed while loading
     */
    @NonNull
    public DisplayHelper loadingImage(@Nullable StateImage loadingImage) {
        displayOptions.setLoadingImage(loadingImage);
        return this;
    }

    /**
     * Placeholder image displayed while loading
     */
    @NonNull
    public DisplayHelper loadingImage(@DrawableRes int drawableResId) {
        displayOptions.setLoadingImage(drawableResId);
        return this;
    }

    /**
     * Show this image when loading fails
     */
    @NonNull
    public DisplayHelper errorImage(@Nullable StateImage errorImage) {
        displayOptions.setErrorImage(errorImage);
        return this;
    }

    /**
     * Show this image when loading fails
     */
    @NonNull
    public DisplayHelper errorImage(@DrawableRes int drawableResId) {
        displayOptions.setErrorImage(drawableResId);
        return this;
    }

    /**
     * Show this image when pausing a download
     */
    @NonNull
    public DisplayHelper pauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        displayOptions.setPauseDownloadImage(pauseDownloadImage);
        return this;
    }

    /**
     * Show this image when pausing a download
     */
    @NonNull
    public DisplayHelper pauseDownloadImage(@DrawableRes int drawableResId) {
        displayOptions.setPauseDownloadImage(drawableResId);
        return this;
    }

    /**
     * Modify the shape of the image when drawing
     */
    @NonNull
    public DisplayHelper shaper(@Nullable ImageShaper imageShaper) {
        displayOptions.setShaper(imageShaper);
        return this;
    }

    /**
     * Modify the size of the image when drawing
     */
    @NonNull
    public DisplayHelper shapeSize(@Nullable ShapeSize shapeSize) {
        displayOptions.setShapeSize(shapeSize);
        return this;
    }

    /**
     * Modify the size of the image when drawing
     */
    @NonNull
    public DisplayHelper shapeSize(int shapeWidth, int shapeHeight) {
        displayOptions.setShapeSize(shapeWidth, shapeHeight);
        return this;
    }

    /**
     * Modify the size of the image when drawing
     */
    @NonNull
    public DisplayHelper shapeSize(int shapeWidth, int shapeHeight, ScaleType scaleType) {
        displayOptions.setShapeSize(shapeWidth, shapeHeight, scaleType);
        return this;
    }

    /**
     * Display image after image loading is completeThe, default value is {@link me.panpf.sketch.display.DefaultImageDisplayer}
     */
    @NonNull
    public DisplayHelper displayer(@Nullable ImageDisplayer displayer) {
        displayOptions.setDisplayer(displayer);
        return this;
    }


    /**
     * Batch setting display parameters, all reset
     */
    @NonNull
    public DisplayHelper options(@Nullable DisplayOptions newOptions) {
        displayOptions.copy(newOptions);
        return this;
    }

    @Nullable
    private Drawable getErrorDrawable() {
        Drawable drawable = null;
        if (displayOptions.getErrorImage() != null) {
            Context context = sketch.getConfiguration().getContext();
            drawable = displayOptions.getErrorImage().getDrawable(context, sketchView, displayOptions);
        } else if (displayOptions.getLoadingImage() != null) {
            Context context = sketch.getConfiguration().getContext();
            drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
        }
        return drawable;
    }

    @Nullable
    public DisplayRequest commit() {
        // Cannot run on non-UI threads
        if (!SketchUtils.isMainThread()) {
            throw new IllegalStateException("Cannot run on non-UI thread");
        }

        // Uri cannot is empty
        if (TextUtils.isEmpty(uri)) {
            SLog.emf(NAME, "Uri is empty. view(%s)", Integer.toHexString(sketchView.hashCode()));
            sketchView.setImageDrawable(getErrorDrawable());
            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_INVALID, false);
            return null;
        }

        // Uri type must be supported
        final UriModel uriModel = UriModel.match(sketch, uri);
        if (uriModel == null) {
            SLog.emf(NAME, "Unsupported uri type. %s. view(%s)", uri, Integer.toHexString(sketchView.hashCode()));
            sketchView.setImageDrawable(getErrorDrawable());
            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NO_SUPPORT, false);
            return null;
        }

        processOptions();
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("processOptions");
        }

        saveOptions();
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("saveOptions");
        }

        final String key = SketchUtils.makeRequestKey(uri, uriModel, displayOptions.makeKey());
        boolean checkResult = checkMemoryCache(key);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("checkMemoryCache");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.VERBOSE)) {
                Stopwatch.with().print(key);
            }
            return null;
        }

        checkResult = checkRequestLevel(key, uriModel);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("checkRequestLevel");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.VERBOSE)) {
                Stopwatch.with().print(key);
            }
            return null;
        }

        DisplayRequest potentialRequest = checkRepeatRequest(key);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("checkRepeatRequest");
        }
        if (potentialRequest != null) {
            if (SLog.isLoggable(SLog.VERBOSE)) {
                Stopwatch.with().print(key);
            }
            return potentialRequest;
        }

        DisplayRequest request = submitRequest(key, uriModel);

        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().print(key);
        }
        return request;
    }

    private void processOptions() {
        Configuration configuration = sketch.getConfiguration();
        final FixedSize fixedSize = sketch.getConfiguration().getSizeCalculator().calculateImageFixedSize(sketchView);
        final ScaleType scaleType = sketchView.getScaleType();

        // Replace ShapeSize.ByViewFixedSizeShapeSize
        ShapeSize shapeSize = displayOptions.getShapeSize();
        if (shapeSize instanceof ShapeSize.ByViewFixedSizeShapeSize) {
            if (fixedSize != null) {
                shapeSize = new ShapeSize(fixedSize.getWidth(), fixedSize.getHeight(), scaleType);
                displayOptions.setShapeSize(shapeSize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the ShapeSize.byViewFixedSize() function");
            }
        }

        // ShapeSize must set ScaleType
        if (shapeSize != null && shapeSize.getScaleType() == null) {
            shapeSize.setScaleType(scaleType);
        }

        // The width and height of the ShapeSize must be greater than 0
        if (shapeSize != null && (shapeSize.getWidth() == 0 || shapeSize.getHeight() == 0)) {
            throw new IllegalArgumentException("ShapeSize width and height must be > 0");
        }


        // Replace Resize.ByViewFixedSizeShapeSize
        Resize resize = displayOptions.getResize();
        if (resize instanceof Resize.ByViewFixedSizeResize) {
            if (fixedSize != null) {
                resize = new Resize(fixedSize.getWidth(), fixedSize.getHeight(), scaleType, resize.getMode());
                displayOptions.setResize(resize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the Resize.byViewFixedSize() function");
            }
        }

        // Resize must set ScaleType
        if (resize != null && resize.getScaleType() == null) {
            resize.setScaleType(scaleType);
        }

        // The width and height of the Resize must be greater than 0
        if (resize != null && (resize.getWidth() <= 0 || resize.getHeight() <= 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // If MaxSize is not set, the default MaxSize is used.
        MaxSize maxSize = displayOptions.getMaxSize();
        if (maxSize == null) {
            ImageSizeCalculator imageSizeCalculator = sketch.getConfiguration().getSizeCalculator();
            maxSize = imageSizeCalculator.calculateImageMaxSize(sketchView);
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.getContext());
            }
            displayOptions.setMaxSize(maxSize);
        }

        // The width or height of MaxSize is greater than 0.
        if (maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // There is no ImageProcessor but there is a Resize, you need to set a default image cropping processor
        if (displayOptions.getProcessor() == null && resize != null) {
            displayOptions.setProcessor(configuration.getResizeProcessor());
        }


        // When using TransitionImageDisplayer, if you use a loadingImage , you must have a ShapeSize
        if (displayOptions.getDisplayer() instanceof TransitionImageDisplayer
                && displayOptions.getLoadingImage() != null && displayOptions.getShapeSize() == null) {
            if (fixedSize != null) {
                displayOptions.setShapeSize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                ViewGroup.LayoutParams layoutParams = sketchView.getLayoutParams();
                String widthName = SketchUtils.viewLayoutFormatted(layoutParams != null ? layoutParams.width : -1);
                String heightName = SketchUtils.viewLayoutFormatted(layoutParams != null ? layoutParams.height : -1);
                String errorInfo = String.format("If you use TransitionImageDisplayer and loadingImage, " +
                        "You must be setup ShapeSize or imageView width and height must be fixed. width=%s, height=%s", widthName, heightName);
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(NAME, "%s. view(%s). %s", errorInfo, Integer.toHexString(sketchView.hashCode()), uri);
                }
                throw new IllegalArgumentException(errorInfo);
            }
        }

        configuration.getOptionsFilterManager().filter(displayOptions);
    }

    private void saveOptions() {
        DisplayCache displayCache = sketchView.getDisplayCache();
        if (displayCache == null) {
            displayCache = new DisplayCache();
            sketchView.setDisplayCache(displayCache);
        }

        displayCache.uri = uri;
        displayCache.options.copy(displayOptions);
    }

    private boolean checkMemoryCache(@NonNull String key) {
        if (displayOptions.isCacheInMemoryDisabled()) {
            return true;
        }

        SketchRefBitmap cachedRefBitmap = sketch.getConfiguration().getMemoryCache().get(key);
        if (cachedRefBitmap == null) {
            return true;
        }

        if (cachedRefBitmap.isRecycled()) {
            sketch.getConfiguration().getMemoryCache().remove(key);
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.wmf(NAME, "Memory cache drawable recycled. %s. view(%s)", cachedRefBitmap.getInfo(), viewCode);
            return true;
        }

        // Gif does not use memory cache
        if (displayOptions.isDecodeGifImage() && "image/gif".equalsIgnoreCase(cachedRefBitmap.getAttrs().getMimeType())) {
            SLog.dmf(NAME, "The picture in the memory cache is just the first frame of the gif. It cannot be used. %s", cachedRefBitmap.getInfo());
            return true;
        }

        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", NAME), true);

        if (SLog.isLoggable(SLog.DEBUG)) {
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.dmf(NAME, "Display image completed. %s. %s. view(%s)", ImageFrom.MEMORY_CACHE.name(), cachedRefBitmap.getInfo(), viewCode);
        }

        SketchBitmapDrawable refBitmapDrawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);

        Drawable finalDrawable;
        if (displayOptions.getShapeSize() != null || displayOptions.getShaper() != null) {
            finalDrawable = new SketchShapeBitmapDrawable(sketch.getConfiguration().getContext(), refBitmapDrawable,
                    displayOptions.getShapeSize(), displayOptions.getShaper());
        } else {
            finalDrawable = refBitmapDrawable;
        }

        ImageDisplayer imageDisplayer = displayOptions.getDisplayer();
        if (imageDisplayer != null && imageDisplayer.isAlwaysUse()) {
            imageDisplayer.display(sketchView, finalDrawable);
        } else {
            sketchView.setImageDrawable(finalDrawable);
        }
        if (displayListener != null) {
            displayListener.onCompleted(finalDrawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.getAttrs());
        }

        ((SketchRefDrawable) finalDrawable).setIsWaitingUse(String.format("%s:waitingUse:finish", NAME), false);
        return false;
    }

    private boolean checkRequestLevel(@NonNull String key, @NonNull UriModel uriModel) {
        if (displayOptions.getRequestLevel() == RequestLevel.MEMORY) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "Request cancel. %s. view(%s). %s", CancelCause.PAUSE_LOAD, Integer.toHexString(sketchView.hashCode()), key);
            }

            Drawable loadingDrawable = null;
            if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                loadingDrawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.clearAnimation();
            sketchView.setImageDrawable(loadingDrawable);

            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_LOAD, false);
            return false;
        }

        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {

            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "Request cancel. %s. view(%s). %s", CancelCause.PAUSE_DOWNLOAD, Integer.toHexString(sketchView.hashCode()), key);
            }

            Drawable drawable = null;
            if (displayOptions.getPauseDownloadImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getPauseDownloadImage().getDrawable(context, sketchView, displayOptions);
                sketchView.clearAnimation();
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.setImageDrawable(drawable);

            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_DOWNLOAD, false);
            return false;
        }

        return true;
    }

    /**
     * Attempting to cancel an existing request
     *
     * @return null: The request has been canceled or no existing; otherwise: the request is repeated
     */
    @Nullable
    private DisplayRequest checkRepeatRequest(@NonNull String key) {
        DisplayRequest potentialRequest = SketchUtils.findDisplayRequest(sketchView);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (key.equals(potentialRequest.getKey())) {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(NAME, "Repeat request. key=%s. view(%s)", key, Integer.toHexString(sketchView.hashCode()));
                }
                return potentialRequest;
            } else {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(NAME, "Cancel old request. newKey=%s. oldKey=%s. view(%s)",
                            key, potentialRequest.getKey(), Integer.toHexString(sketchView.hashCode()));
                }
                potentialRequest.cancel(CancelCause.BE_REPLACED_ON_HELPER);
            }
        }

        return null;
    }

    @NonNull
    private DisplayRequest submitRequest(@NonNull String key, @NonNull UriModel uriModel) {
        CallbackHandler.postCallbackStarted(displayListener, false);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("callbackStarted");
        }

        RequestAndViewBinder requestAndViewBinder = new RequestAndViewBinder(sketchView);
        DisplayRequest request = new DisplayRequest(sketch, uri, uriModel, key, displayOptions, sketchView.isUseSmallerThumbnails(),
                requestAndViewBinder, displayListener, downloadProgressListener);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("createRequest");
        }

        SketchLoadingDrawable loadingDrawable;
        StateImage loadingImage = displayOptions.getLoadingImage();
        if (loadingImage != null) {
            Context context = sketch.getConfiguration().getContext();
            Drawable drawable = loadingImage.getDrawable(context, sketchView, displayOptions);
            loadingDrawable = new SketchLoadingDrawable(drawable, request);
        } else {
            loadingDrawable = new SketchLoadingDrawable(null, request);
        }
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("createLoadingImage");
        }

        sketchView.setImageDrawable(loadingDrawable);
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("setLoadingImage");
        }

        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(NAME, "Run dispatch submitted. view(%s). %s", Integer.toHexString(sketchView.hashCode()), key);
        }

        request.submitDispatch();
        if (SLog.isLoggable(SLog.VERBOSE)) {
            Stopwatch.with().record("submitRequest");
        }

        return request;
    }
}