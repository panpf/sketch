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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 加载选项，适用于 {@link me.xiaopan.sketch.Sketch#load(String, LoadListener)} 方法
 */
public class LoadOptions extends DownloadOptions {
    /**
     * 修正尺寸
     */
    private Resize resize;

    /**
     * 最大尺寸，用于计算 inSampleSize，缩小图片
     */
    private MaxSize maxSize;

    /**
     * 解码 GIF 图片
     */
    private boolean decodeGifImage;

    /**
     * 在解码或创建 Bitmap 的时候尽量使用低质量的 Bitmap.Config
     *
     * @see ImageType
     */
    private boolean lowQualityImage;

    /**
     * 解码时优先考虑速度还是质量 (true：质量；false：速度，默认false)，优先级比 bitmapConfig 低
     *
     * @see #bitmapConfig
     */
    private boolean inPreferQualityOverSpeed;

    /**
     * 开启缩略图模式，当 resize 的宽高比同原始图片的宽高比相差非常大的时候会用 BitmapRegionDecoder 从原始图片中截取合适的部分，这样对于显示较大图片的缩略图会有很大的帮助
     */
    private boolean thumbnailMode;

    /**
     * 图片处理器
     */
    private ImageProcessor imageProcessor;

    /**
     * 图片质量配置，优先级比 lowQualityImage 高，KITKAT 以上ARGB_4444 会被强制替换为 ARGB_8888
     *
     * @see #lowQualityImage
     */
    private Bitmap.Config bitmapConfig;

    /**
     * 为了加快显示速度，将经过 ImageProcessor、resize 或 thumbnailMode 处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    private boolean cacheProcessedImageInDisk;

    /**
     * 禁止从 BitmapPool 中寻找可复用的 Bitmap
     */
    private boolean bitmapPoolDisabled;

    /**
     * 禁用纠正图片方向
     */
    private boolean correctImageOrientationDisabled;


    public LoadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public LoadOptions(@NonNull LoadOptions from) {
        copy(from);
    }

    @NonNull
    @Override
    public LoadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (LoadOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @NonNull
    @Override
    public LoadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (LoadOptions) super.setRequestLevel(requestLevel);
    }

    /**
     * 获取最大尺寸，用于计算 inSampleSize，缩小图片
     *
     * @return MaxSize
     */
    @Nullable
    public MaxSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，用于计算 inSampleSize，缩小图片
     *
     * @param maxSize MaxSize
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setMaxSize(@Nullable MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSampleSize，缩小图片
     *
     * @param width  最大宽
     * @param height 最大高
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setMaxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 获取修正尺寸，用于修改图片尺寸
     *
     * @return Resize
     */
    @Nullable
    public Resize getResize() {
        return resize;
    }

    /**
     * 设置修正尺寸，用于修改图片尺寸
     *
     * @param resize Resize
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setResize(@Nullable Resize resize) {
        this.resize = resize;
        return this;
    }

    /**
     * 设置修正尺寸，用与修改图片尺寸
     *
     * @param width  修正宽
     * @param height 修正高
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setResize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 设置修正尺寸，用与修改图片尺寸
     *
     * @param width     修正宽
     * @param height    修正高
     * @param scaleType 缩放类型
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setResize(int width, int height, @Nullable ImageView.ScaleType scaleType) {
        this.resize = new Resize(width, height, scaleType);
        return this;
    }

    /**
     * 获取图片处理器
     *
     * @return ImageProcessor
     */
    @Nullable
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器
     *
     * @param processor ImageProcessor
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setImageProcessor(@Nullable ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 是否解码 GIF 图片，默认只解码 GIF 图片的第一帧，当需要播放 GIF 的时候，设置解码 GIF 图片即可
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置解码 GIF 图片，默认只解码 GIF 图片的第一帧，当需要播放 GIF 的时候，设置解码 GIF 图片即可
     *
     * @param decodeGifImage 解码 GIF 图片
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    /**
     * 尽量返回低质量的图片，优先级低于 bitmapConfig
     *
     * @see ImageType
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置尽量返回低质量的图片，优先级低于 bitmapConfig
     *
     * @param lowQualityImage 尽量返回低质量的图片
     * @return LoadOptions
     * @see ImageType
     */
    @NonNull
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    /**
     * 获取你想使用的 Bitmap.Config，优先级高于 lowQualityImage
     *
     * @return Bitmap.Config，KITKAT 以上ARGB_4444 会被强制替换为 ARGB_8888
     */
    @Nullable
    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    /**
     * 设置你想使用的 Bitmap.Config，优先级高于 lowQualityImage
     *
     * @param bitmapConfig Bitmap.Config，KITKAT 以上ARGB_4444 会被强制替换为 ARGB_8888
     * @return LoadOptions
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
     * 解码时优先考虑质量还是速度
     *
     * @return true：质量；false：速度
     */
    public boolean isInPreferQualityOverSpeed() {
        return inPreferQualityOverSpeed;
    }

    /**
     * 设置解码时优先考虑质量还是速度
     *
     * @param inPreferQualityOverSpeed true：质量；false：速度
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        this.inPreferQualityOverSpeed = inPreferQualityOverSpeed;
        return this;
    }

    /**
     * 使用缩略图模式，可以帮助生成较清晰的缩略图
     */
    public boolean isThumbnailMode() {
        return thumbnailMode;
    }

    /**
     * 设置使用缩略图模式，可以帮助生成较清晰的缩略图
     *
     * @param thumbnailMode 缩略图模式
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
        return this;
    }

    /**
     * 将经过 ImageProcessor、resize 或 thumbnailMode 处理过的图片保存到磁盘缓存中，下次就直接读取，加快显示速度
     */
    public boolean isCacheProcessedImageInDisk() {
        return cacheProcessedImageInDisk;
    }

    /**
     * 设置将经过 ImageProcessor、resize 或 thumbnailMode 处理过的图片保存到磁盘缓存中，下次就直接读取，加快显示速度
     *
     * @param cacheProcessedImageInDisk true：缓存
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        this.cacheProcessedImageInDisk = cacheProcessedImageInDisk;
        return this;
    }

    /**
     * 禁止从 bitmap pool 从寻找可复用的 bitmap
     */
    public boolean isBitmapPoolDisabled() {
        return bitmapPoolDisabled;
    }

    /**
     * 设置禁止从 bitmap pool 从寻找可复用的 bitmap
     *
     * @param bitmapPoolDisabled 禁止从 bitmap pool 从寻找可复用的 bitmap
     * @return LoadOptions
     */
    @NonNull
    public LoadOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        this.bitmapPoolDisabled = bitmapPoolDisabled;
        return this;
    }

    /**
     * 是否禁用纠正图片方向功能
     */
    public boolean isCorrectImageOrientationDisabled() {
        return correctImageOrientationDisabled;
    }

    /**
     * 设置禁用纠正图片方向功能
     *
     * @param correctImageOrientationDisabled true：禁用纠正图片方向
     * @return LoadOptions
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
        imageProcessor = null;
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
        imageProcessor = options.imageProcessor;
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
            builder.append("_").append(maxSize.getKey());
        }
        if (resize != null) {
            builder.append("_").append(resize.getKey());
            if (thumbnailMode) {
                builder.append("_").append("thumbnailMode");
            }
        }
        if (correctImageOrientationDisabled) {
            builder.append("_").append("correctImageOrientationDisabled");
        }
        if (lowQualityImage) {
            builder.append("_").append("lowQualityImage");
        }
        if (inPreferQualityOverSpeed) {
            builder.append("_").append("preferQuality");
        }
        if (decodeGifImage) {
            builder.append("_").append("decodeGifImage");
        }
        if (bitmapConfig != null) {
            builder.append("_").append(bitmapConfig.name());
        }
        if (imageProcessor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            String processorKey = imageProcessor.getKey();
            if (!TextUtils.isEmpty(processorKey)) {
                builder.append("_").append(processorKey);
            }
        }
        return builder.toString();
    }

    @NonNull
    @Override
    public String makeStateImageKey() {
        StringBuilder builder = new StringBuilder();
        if (resize != null) {
            builder.append("_").append(resize.getKey());
        }
        if (lowQualityImage) {
            builder.append("_").append("lowQualityImage");
        }
        if (imageProcessor != null) {
            // 旋转图片处理器在旋转0度或360度时不用旋转处理，因此也不会返回key，因此这里过滤一下
            String processorKey = imageProcessor.getKey();
            if (!TextUtils.isEmpty(processorKey)) {
                builder.append("_").append(processorKey);
            }
        }
        return builder.toString();
    }
}
