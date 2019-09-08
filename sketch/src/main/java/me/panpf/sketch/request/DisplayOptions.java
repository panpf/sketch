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
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.display.ImageDisplayer;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.state.DrawableStateImage;
import me.panpf.sketch.state.StateImage;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class DisplayOptions extends LoadOptions {

    /**
     * Disabled memory caching
     */
    private boolean cacheInMemoryDisabled;

    /**
     * Placeholder image displayed while loading
     */
    @Nullable
    private StateImage loadingImage;

    /**
     * Show this image when loading fails
     */
    @Nullable
    private StateImage errorImage;

    /**
     * Show this image when pausing a download
     */
    @Nullable
    private StateImage pauseDownloadImage;

    /**
     * Modify the shape of the image when drawing
     */
    @Nullable
    private ImageShaper shaper;

    /**
     * Modify the size of the image when drawing
     */
    @Nullable
    private ShapeSize shapeSize;

    /**
     * Display image after image loading is completeThe, default value is {@link me.panpf.sketch.display.DefaultImageDisplayer}
     */
    @Nullable
    private ImageDisplayer displayer;

    public DisplayOptions() {
    }

    public DisplayOptions(@NonNull DisplayOptions from) {
        copy(from);
    }

    @NonNull
    @Override
    public DisplayOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (DisplayOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @NonNull
    @Override
    public DisplayOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (DisplayOptions) super.setRequestLevel(requestLevel);
    }

    @NonNull
    @Override
    public DisplayOptions setMaxSize(@Nullable MaxSize maxSize) {
        return (DisplayOptions) super.setMaxSize(maxSize);
    }

    @NonNull
    @Override
    public DisplayOptions setMaxSize(int maxWidth, int maxHeight) {
        return (DisplayOptions) super.setMaxSize(maxWidth, maxHeight);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(@Nullable Resize resize) {
        return (DisplayOptions) super.setResize(resize);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(int reWidth, int reHeight) {
        return (DisplayOptions) super.setResize(reWidth, reHeight);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(int reWidth, int reHeight, @Nullable ImageView.ScaleType scaleType) {
        return (DisplayOptions) super.setResize(reWidth, reHeight, scaleType);
    }

    @NonNull
    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        return (DisplayOptions) super.setDecodeGifImage(decodeGifImage);
    }

    @NonNull
    @Override
    public DisplayOptions setLowQualityImage(boolean lowQualityImage) {
        return (DisplayOptions) super.setLowQualityImage(lowQualityImage);
    }

    @NonNull
    @Override
    public DisplayOptions setProcessor(@Nullable ImageProcessor processor) {
        return (DisplayOptions) super.setProcessor(processor);
    }

    @NonNull
    @Override
    public DisplayOptions setBitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        return (DisplayOptions) super.setBitmapConfig(bitmapConfig);
    }

    @NonNull
    @Override
    public DisplayOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        return (DisplayOptions) super.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
    }

    @NonNull
    @Override
    public DisplayOptions setThumbnailMode(boolean thumbnailMode) {
        return (DisplayOptions) super.setThumbnailMode(thumbnailMode);
    }

    @NonNull
    @Override
    public DisplayOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        return (DisplayOptions) super.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
    }

    @NonNull
    @Override
    public DisplayOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        return (DisplayOptions) super.setBitmapPoolDisabled(bitmapPoolDisabled);
    }

    @NonNull
    @Override
    public DisplayOptions setCorrectImageOrientationDisabled(boolean correctImageOrientationDisabled) {
        return (DisplayOptions) super.setCorrectImageOrientationDisabled(correctImageOrientationDisabled);
    }

    public boolean isCacheInMemoryDisabled() {
        return cacheInMemoryDisabled;
    }

    @NonNull
    public DisplayOptions setCacheInMemoryDisabled(boolean cacheInMemoryDisabled) {
        this.cacheInMemoryDisabled = cacheInMemoryDisabled;
        return this;
    }

    @Nullable
    public ImageDisplayer getDisplayer() {
        return displayer;
    }

    @NonNull
    public DisplayOptions setDisplayer(@Nullable ImageDisplayer displayer) {
        this.displayer = displayer;
        return this;
    }

    @Nullable
    public StateImage getLoadingImage() {
        return loadingImage;
    }

    @NonNull
    public DisplayOptions setLoadingImage(@Nullable StateImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    @NonNull
    public DisplayOptions setLoadingImage(@DrawableRes int drawableResId) {
        this.loadingImage = new DrawableStateImage(drawableResId);
        return this;
    }

    @Nullable
    public StateImage getErrorImage() {
        return errorImage;
    }

    @NonNull
    public DisplayOptions setErrorImage(@Nullable StateImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    @NonNull
    public DisplayOptions setErrorImage(@DrawableRes int drawableResId) {
        this.errorImage = new DrawableStateImage(drawableResId);
        return this;
    }

    @Nullable
    public StateImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    @NonNull
    public DisplayOptions setPauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    @NonNull
    public DisplayOptions setPauseDownloadImage(@DrawableRes int drawableResId) {
        this.pauseDownloadImage = new DrawableStateImage(drawableResId);
        return this;
    }

    @Nullable
    public ImageShaper getShaper() {
        return shaper;
    }

    @NonNull
    public DisplayOptions setShaper(@Nullable ImageShaper shaper) {
        this.shaper = shaper;
        return this;
    }

    @Nullable
    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    @NonNull
    public DisplayOptions setShapeSize(@Nullable ShapeSize shapeSize) {
        this.shapeSize = shapeSize;
        return this;
    }

    @NonNull
    public DisplayOptions setShapeSize(int shapeWidth, int shapeHeight) {
        this.shapeSize = new ShapeSize(shapeWidth, shapeHeight);
        return this;
    }

    @NonNull
    public DisplayOptions setShapeSize(int shapeWidth, int shapeHeight, @Nullable ImageView.ScaleType scaleType) {
        this.shapeSize = new ShapeSize(shapeWidth, shapeHeight, scaleType);
        return this;
    }

    @Override
    public void reset() {
        super.reset();
        cacheInMemoryDisabled = false;
        displayer = null;
        loadingImage = null;
        errorImage = null;
        pauseDownloadImage = null;
        shaper = null;
        shapeSize = null;
    }

    public void copy(@Nullable DisplayOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.copy((LoadOptions) options);

        cacheInMemoryDisabled = options.cacheInMemoryDisabled;
        displayer = options.displayer;
        loadingImage = options.loadingImage;
        errorImage = options.errorImage;
        pauseDownloadImage = options.pauseDownloadImage;
        shaper = options.shaper;
        shapeSize = options.shapeSize;
    }
}
