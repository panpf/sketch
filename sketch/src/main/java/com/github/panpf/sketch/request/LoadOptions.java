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
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.panpf.sketch.cache.BitmapPool;
import com.github.panpf.sketch.decode.ProcessedResultCacheProcessor;
import com.github.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import com.github.panpf.sketch.process.ImageProcessor;
import com.github.panpf.sketch.util.SketchUtils;

public class LoadOptions extends DownloadOptions {

    /**
     * The size of the desired bitmap
     */
    @Nullable
    private Resize resize;

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    @Nullable
    private MaxSize maxSize;

    /**
     * Support gif images
     */
    private boolean decodeGifImage;

    /**
     * Prioritize low quality {@link Bitmap.Config} when creating bitmaps, the priority is lower than the {@link #bitmapConfig} attribute
     */
    private boolean lowQualityImage;

    /**
     * Priority is given to speed or quality when decoding. Applied to the {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed}
     */
    private boolean inPreferQualityOverSpeed;

    /**
     * Thumbnail mode, together with the {@link #resize} property, gives a sharper thumbnail, see {@link ThumbnailModeDecodeHelper}
     */
    private boolean thumbnailMode;

    /**
     * Modify Bitmap after decoding the image, If the {@link #resize} attribute is not null, the default is {@link com.github.panpf.sketch.process.ResizeImageProcessor}
     */
    @Nullable
    private ImageProcessor processor;

    /**
     * Specify {@link Bitmap.Config} to use when creating the bitmap.
     * KITKAT and above {@link Bitmap.Config#ARGB_4444} will be forced to be replaced with {@link Bitmap.Config#ARGB_8888}.
     * With priority higher than {@link #lowQualityImage} Property.
     * Applied to {@link android.graphics.BitmapFactory.Options#inPreferredConfig}
     */
    @Nullable
    private Bitmap.Config bitmapConfig;

    /**
     * In order to speed up, save the image processed by {@link #processor}, {@link #resize} or {@link #thumbnailMode} to the disk cache,
     * read it directly next time, refer to {@link ProcessedResultCacheProcessor}
     */
    private boolean cacheProcessedImageInDisk;

    /**
     * Disabled get reusable bitmap from {@link BitmapPool}
     */
    private boolean bitmapPoolDisabled;

    /**
     * Disabled correcting picture orientation
     */
    private boolean correctImageOrientationDisabled;


    public LoadOptions() {
    }

    public LoadOptions(@NonNull LoadOptions from) {
        copy(from);
    }

    @NonNull
    @Override
    public LoadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (LoadOptions) super.setRequestLevel(requestLevel);
    }

    @NonNull
    @Override
    public LoadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (LoadOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @Nullable
    public MaxSize getMaxSize() {
        return maxSize;
    }

    @NonNull
    public LoadOptions setMaxSize(@Nullable MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    @NonNull
    public LoadOptions setMaxSize(int maxWidth, int maxHeight) {
        this.maxSize = new MaxSize(maxWidth, maxHeight);
        return this;
    }

    @Nullable
    public Resize getResize() {
        return resize;
    }

    @NonNull
    public LoadOptions setResize(@Nullable Resize resize) {
        this.resize = resize;
        return this;
    }

    @NonNull
    public LoadOptions setResize(int reWidth, int reHeight) {
        this.resize = new Resize(reWidth, reHeight);
        return this;
    }

    @NonNull
    public LoadOptions setResize(int reWidth, int reHeight, @Nullable ImageView.ScaleType scaleType) {
        this.resize = new Resize(reWidth, reHeight, scaleType);
        return this;
    }

    @Nullable
    public ImageProcessor getProcessor() {
        return processor;
    }

    @NonNull
    public LoadOptions setProcessor(@Nullable ImageProcessor processor) {
        this.processor = processor;
        return this;
    }

    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    @NonNull
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    @NonNull
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    @Nullable
    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    @NonNull
    public LoadOptions setBitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        if (bitmapConfig == Bitmap.Config.ARGB_4444 && SketchUtils.isDisabledARGB4444()) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        this.bitmapConfig = bitmapConfig;
        return this;
    }

    public boolean isInPreferQualityOverSpeed() {
        return inPreferQualityOverSpeed;
    }

    @NonNull
    public LoadOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        this.inPreferQualityOverSpeed = inPreferQualityOverSpeed;
        return this;
    }

    public boolean isThumbnailMode() {
        return thumbnailMode;
    }

    @NonNull
    public LoadOptions setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
        return this;
    }

    public boolean isCacheProcessedImageInDisk() {
        return cacheProcessedImageInDisk;
    }

    @NonNull
    public LoadOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        this.cacheProcessedImageInDisk = cacheProcessedImageInDisk;
        return this;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isBitmapPoolDisabled() {
        return bitmapPoolDisabled;
    }

    @NonNull
    public LoadOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        this.bitmapPoolDisabled = bitmapPoolDisabled;
        return this;
    }

    public boolean isCorrectImageOrientationDisabled() {
        return correctImageOrientationDisabled;
    }

    @NonNull
    public LoadOptions setCorrectImageOrientationDisabled(boolean correctImageOrientationDisabled) {
        this.correctImageOrientationDisabled = correctImageOrientationDisabled;
        return this;
    }

    @Override
    public void reset() {
        super.reset();

        maxSize = null;
        resize = null;
        lowQualityImage = false;
        processor = null;
        decodeGifImage = false;
        bitmapConfig = null;
        inPreferQualityOverSpeed = false;
        thumbnailMode = false;
        cacheProcessedImageInDisk = false;
        bitmapPoolDisabled = false;
        correctImageOrientationDisabled = false;
    }

    public void copy(@Nullable LoadOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.copy((DownloadOptions) options);

        maxSize = options.maxSize;
        resize = options.resize;
        lowQualityImage = options.lowQualityImage;
        processor = options.processor;
        decodeGifImage = options.decodeGifImage;
        bitmapConfig = options.bitmapConfig;
        inPreferQualityOverSpeed = options.inPreferQualityOverSpeed;
        thumbnailMode = options.thumbnailMode;
        cacheProcessedImageInDisk = options.cacheProcessedImageInDisk;
        bitmapPoolDisabled = options.bitmapPoolDisabled;
        correctImageOrientationDisabled = options.correctImageOrientationDisabled;
    }

    @NonNull
    @Override
    public String makeKey() {
        StringBuilder builder = new StringBuilder();
        if (maxSize != null) {
            if (builder.length() > 0) builder.append('-');
            builder.append(maxSize.getKey());
        }
        if (resize != null) {
            // TODO: 2019/1/23 这里计算的时候 resize 有可能是 ByViewFixedSizeResize
            if (builder.length() > 0) builder.append('-');
            builder.append(resize.getKey());
            if (thumbnailMode) {
                if (builder.length() > 0) builder.append('-');
                builder.append("thumbnailMode");
            }
        }
        if (correctImageOrientationDisabled) {
            if (builder.length() > 0) builder.append('-');
            builder.append("ignoreOrientation");
        }
        if (lowQualityImage) {
            if (builder.length() > 0) builder.append('-');
            builder.append("lowQuality");
        }
        if (inPreferQualityOverSpeed) {
            if (builder.length() > 0) builder.append('-');
            builder.append("preferQuality");
        }
        if (bitmapConfig != null) {
            if (builder.length() > 0) builder.append('-');
            builder.append(bitmapConfig.name());
        }
        if (processor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            String processorKey = processor.getKey();
            if (!TextUtils.isEmpty(processorKey)) {
                if (builder.length() > 0) builder.append('-');
                builder.append(processorKey);
            }
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public String makeStateImageKey() {
        StringBuilder builder = new StringBuilder();
        if (resize != null) {
            if (builder.length() > 0) builder.append('-');
            builder.append(resize.getKey());
        }
        if (lowQualityImage) {
            if (builder.length() > 0) builder.append('-');
            builder.append("lowQuality");
        }
        if (processor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            String processorKey = processor.getKey();
            if (!TextUtils.isEmpty(processorKey)) {
                if (builder.length() > 0) builder.append('-');
                builder.append(processorKey);
            }
        }
        return builder.toString();
    }
}
