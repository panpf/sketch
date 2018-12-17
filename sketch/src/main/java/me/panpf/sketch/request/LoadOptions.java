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
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.decode.ProcessedResultCacheProcessor;
import me.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.util.SketchUtils;

/**
 * 加载选项，适用于 {@link Sketch#load(String, LoadListener)} 方法
 */
public class LoadOptions extends DownloadOptions {
    /**
     * 新的尺寸，用于调整图片尺寸
     */
    private Resize resize;

    /**
     * 最大尺寸，用于计算 inSampleSize 缩小图片
     */
    private MaxSize maxSize;

    /**
     * 解码 gif 图片并自动循环播放
     */
    private boolean decodeGifImage;

    /**
     * 在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config}，优先级低于 {@link #bitmapConfig}，参考 {@link ImageType#getConfig(boolean)}
     */
    private boolean lowQualityImage;

    /**
     * 解码时优先考虑速度还是质量，对应 {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     */
    private boolean inPreferQualityOverSpeed;

    /**
     * 开启缩略图模式，配合 resize 可以得到更清晰的缩略图，参考 {@link ThumbnailModeDecodeHelper}
     */
    private boolean thumbnailMode;

    /**
     * 图片处理器，在图片读取到内存后对图片进行修改
     */
    private ImageProcessor processor;

    /**
     * 解码时使用的 {@link Bitmap.Config}，KITKAT 以上 {@link Bitmap.Config#ARGB_4444} 会被强制替换为 {@link Bitmap.Config#ARGB_8888}，优先级高于 {@link #lowQualityImage}，对应 {@link android.graphics.BitmapFactory.Options#inPreferredConfig} 属性
     *
     * @see #lowQualityImage
     */
    private Bitmap.Config bitmapConfig;

    /**
     * 为了加快速度，将经过 {@link #processor}、{@link #resize} 或 {@link #thumbnailMode} 处理过的图片保存到磁盘缓存中，下次就直接读取，参考 {@link ProcessedResultCacheProcessor}
     */
    private boolean cacheProcessedImageInDisk;

    /**
     * 禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     */
    private boolean bitmapPoolDisabled;

    /**
     * 禁止纠正图片方向
     */
    private boolean correctImageOrientationDisabled;


    public LoadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public LoadOptions(@NonNull LoadOptions from) {
        copy(from);
    }

    /**
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public LoadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (LoadOptions) super.setRequestLevel(requestLevel);
    }

    /**
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public LoadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (LoadOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    /**
     * 获取最大尺寸，用于计算 inSampleSize 缩小图片
     *
     * @return {@link MaxSize}
     */
    @Nullable
    public MaxSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，用于计算 inSampleSize 缩小图片
     *
     * @param maxSize 最大尺寸
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setMaxSize(@Nullable MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSampleSize 缩小图片
     *
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setMaxSize(int maxWidth, int maxHeight) {
        this.maxSize = new MaxSize(maxWidth, maxHeight);
        return this;
    }

    /**
     * 获取新的尺寸，用于调整图片尺寸
     *
     * @return {@link Resize}
     */
    @Nullable
    public Resize getResize() {
        return resize;
    }

    /**
     * 设置新的尺寸，用于调整图片尺寸
     *
     * @param resize 新的尺寸
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setResize(@Nullable Resize resize) {
        this.resize = resize;
        return this;
    }

    /**
     * 设置新的尺寸，用于调整图片尺寸
     *
     * @param reWidth  新的宽
     * @param reHeight 新的高
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setResize(int reWidth, int reHeight) {
        this.resize = new Resize(reWidth, reHeight);
        return this;
    }

    /**
     * 设置新的尺寸，用于调整图片尺寸
     *
     * @param reWidth   新的宽
     * @param reHeight  新的高
     * @param scaleType 指定如何生成新图片
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setResize(int reWidth, int reHeight, @Nullable ImageView.ScaleType scaleType) {
        this.resize = new Resize(reWidth, reHeight, scaleType);
        return this;
    }

    /**
     * 获取图片处理器，在图片读取到内存后对图片进行修改
     *
     * @return {@link ImageProcessor}
     */
    @Nullable
    public ImageProcessor getProcessor() {
        return processor;
    }

    /**
     * 设置图片处理器，在图片读取到内存后对图片进行修改
     *
     * @param processor {@link ImageProcessor}
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setProcessor(@Nullable ImageProcessor processor) {
        this.processor = processor;
        return this;
    }

    /**
     * 是否解码 gif 图片并自动循环播放
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置是否解码 gif 图片并自动循环播放
     *
     * @param decodeGifImage 解码 gif 图片
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    /**
     * 是否在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config}，优先级低于 {@link #setBitmapConfig(Bitmap.Config)}，参考 {@link ImageType#getConfig(boolean)}
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置是否在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config}，优先级低于 {@link #setBitmapConfig(Bitmap.Config)}，参考 {@link ImageType#getConfig(boolean)}
     *
     * @param lowQualityImage 在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config}
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    /**
     * 获取解码时使用的 {@link Bitmap.Config}，KITKAT 以上 {@link Bitmap.Config#ARGB_4444} 会被强制替换为 {@link Bitmap.Config#ARGB_8888}，优先级高于 {@link #setLowQualityImage(boolean)}，对应 {@link android.graphics.BitmapFactory.Options#inPreferredConfig} 属性
     *
     * @return {@link Bitmap.Config}
     */
    @Nullable
    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    /**
     * 设置解码时使用的 {@link Bitmap.Config}，KITKAT 以上 {@link Bitmap.Config#ARGB_4444} 会被强制替换为 {@link Bitmap.Config#ARGB_8888}，优先级高于 {@link #setLowQualityImage(boolean)}，对应 {@link android.graphics.BitmapFactory.Options#inPreferredConfig} 属性
     *
     * @param bitmapConfig {@link Bitmap.Config}
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setBitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        if (bitmapConfig == Bitmap.Config.ARGB_4444 && SketchUtils.isDisabledARGB4444()) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        this.bitmapConfig = bitmapConfig;
        return this;
    }

    /**
     * 解码时优先考虑速度还是质量，对应 {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     *
     * @return true：质量优先；false：速度优先
     */
    public boolean isInPreferQualityOverSpeed() {
        return inPreferQualityOverSpeed;
    }

    /**
     * 设置解码时优先考虑速度还是质量，对应 {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     *
     * @param inPreferQualityOverSpeed true：质量优先；false：速度优先
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        this.inPreferQualityOverSpeed = inPreferQualityOverSpeed;
        return this;
    }

    /**
     * 是否开启缩略图模式，配合 resize 可以得到更清晰的缩略图，参考 {@link ThumbnailModeDecodeHelper}
     */
    public boolean isThumbnailMode() {
        return thumbnailMode;
    }

    /**
     * 设置是否开启缩略图模式，配合 resize 可以得到更清晰的缩略图，参考 {@link ThumbnailModeDecodeHelper}
     *
     * @param thumbnailMode 缩略图模式
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
        return this;
    }

    /**
     * 是否为了加快速度，将经过 {@link #setProcessor(ImageProcessor)}、{@link #setResize(Resize)} 或 {@link #setThumbnailMode(boolean)} 处理过的图片保存到磁盘缓存中，下次就直接读取，参考 {@link ProcessedResultCacheProcessor}
     */
    public boolean isCacheProcessedImageInDisk() {
        return cacheProcessedImageInDisk;
    }

    /**
     * 设置是否为了加快速度，将经过 {@link #setProcessor(ImageProcessor)}、{@link #setResize(Resize)} 或 {@link #setThumbnailMode(boolean)} 处理过的图片保存到磁盘缓存中，下次就直接读取，参考 {@link ProcessedResultCacheProcessor}
     *
     * @param cacheProcessedImageInDisk true：缓存
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        this.cacheProcessedImageInDisk = cacheProcessedImageInDisk;
        return this;
    }

    /**
     * 是否禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     */
    public boolean isBitmapPoolDisabled() {
        return bitmapPoolDisabled;
    }

    /**
     * 设置是否禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     *
     * @param bitmapPoolDisabled 禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     * @return {@link LoadOptions}. 为了支持链式调用
     */
    @NonNull
    public LoadOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        this.bitmapPoolDisabled = bitmapPoolDisabled;
        return this;
    }

    /**
     * 是否禁止纠正图片方向
     */
    public boolean isCorrectImageOrientationDisabled() {
        return correctImageOrientationDisabled;
    }

    /**
     * 设置是否禁止纠正图片方向
     *
     * @param correctImageOrientationDisabled 是否禁止纠正图片方向
     * @return {@link LoadOptions}. 为了支持链式调用
     */
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

    /**
     * 拷贝属性，绝对的覆盖
     *
     * @param options 来源
     */
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
        if (decodeGifImage) {
            if (builder.length() > 0) builder.append('-');
            builder.append("gif");
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
