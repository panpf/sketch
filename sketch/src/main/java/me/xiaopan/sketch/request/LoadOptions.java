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
import android.text.TextUtils;

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
     * 强制使经过resize处理的图片同resize的尺寸一致
     */
    private boolean forceUseResize;

    /**
     * 最大尺寸，用于计算inSampleSize，缩小图片
     */
    private MaxSize maxSize;

    /**
     * 解码GIF图片
     */
    private boolean decodeGifImage;

    /**
     * 在解码或创建Bitmap的时候尽量使用低质量的Bitmap.Config
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
     * 开启缩略图模式，当resize的宽高比同原始图片的宽高比相差非常大的时候会用BitmapRegionDecoder从原始图片中截取合适的部分，这样对于现实较大图片的缩略图会有很大的帮助
     */
    private boolean thumbnailMode;

    /**
     * 图片处理器
     */
    private ImageProcessor imageProcessor;

    /**
     * 图片质量配置，优先级比 lowQualityImage 高
     *
     * @see #lowQualityImage
     */
    private Bitmap.Config bitmapConfig;

    /**
     * 为了加快显示速度，将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    private boolean cacheProcessedImageInDisk;

    /**
     * 禁止从BitmapPool中寻找可复用的Bitmap
     */
    private boolean bitmapPoolDisabled;

    /**
     * 纠正图片方向，可让被旋转了的图片以正常方向显示
     */
    private boolean correctImageOrientation;


    public LoadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public LoadOptions(LoadOptions from) {
        copy(from);
    }

    @Override
    public LoadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (LoadOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @Override
    public LoadOptions setRequestLevel(RequestLevel requestLevel) {
        return (LoadOptions) super.setRequestLevel(requestLevel);
    }

    @Override
    LoadOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        return (LoadOptions) super.setRequestLevelFrom(requestLevelFrom);
    }

    /**
     * 获取最大尺寸，用于计算inSampleSize，缩小图片
     *
     * @return MaxSize
     */
    public MaxSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，用于计算inSampleSize，缩小图片
     *
     * @param maxSize MaxSize
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，用于计算inSampleSize，缩小图片
     *
     * @param width  最大宽
     * @param height 最大高
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 获取修正尺寸，用于修改图片尺寸
     *
     * @return Resize
     */
    public Resize getResize() {
        return resize;
    }

    /**
     * 设置修正尺寸，用于修改图片尺寸
     *
     * @param resize Resize
     * @return LoadOptions
     */
    public LoadOptions setResize(Resize resize) {
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
    public LoadOptions setResize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 强制使最终图片的尺寸跟resize一样
     */
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    /**
     * 设置强制使最终图片的尺寸跟resize一样
     *
     * @param forceUseResize 强制使最终图片的尺寸跟resize一样
     * @return LoadOptions
     */
    public LoadOptions setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    /**
     * 获取图片处理器
     *
     * @return ImageProcessor
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器
     *
     * @param processor ImageProcessor
     * @return LoadOptions
     */
    public LoadOptions setImageProcessor(ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 解码GIF图片，默认只解码GIF图片的第一帧，当需要播放GIF的时候，设置解码GIF图片即可
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置解码GIF图片，默认只解码GIF图片的第一帧，当需要播放GIF的时候，设置解码GIF图片即可
     *
     * @param decodeGifImage 解码GIF图片
     * @return LoadOptions
     */
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    /**
     * 尽量返回低质量的图片，优先级低于bitmapConfig
     *
     * @see ImageType
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置尽量返回低质量的图片，优先级低于bitmapConfig
     *
     * @param lowQualityImage 尽量返回低质量的图片
     * @return LoadOptions
     * @see ImageType
     */
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    /**
     * 获取bitmap config，这是你强制指定的config，优先级高于lowQualityImage
     *
     * @return Bitmap.Config
     */
    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    /**
     * 设置你想使用的bitmap config，优先级高于lowQualityImage
     *
     * @param bitmapConfig Bitmap.Config
     * @return LoadOptions
     */
    public LoadOptions setBitmapConfig(Bitmap.Config bitmapConfig) {
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
    public LoadOptions setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
        return this;
    }

    /**
     * 将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取，加快显示速度
     */
    public boolean isCacheProcessedImageInDisk() {
        return cacheProcessedImageInDisk;
    }

    /**
     * 设置将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取，加快显示速度
     *
     * @param cacheProcessedImageInDisk true：缓存
     * @return LoadOptions
     */
    public LoadOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        this.cacheProcessedImageInDisk = cacheProcessedImageInDisk;
        return this;
    }

    /**
     * 禁止从bitmap pool从寻找可复用的bitmap
     */
    public boolean isBitmapPoolDisabled() {
        return bitmapPoolDisabled;
    }

    /**
     * 设置禁止从bitmap pool从寻找可复用的bitmap
     *
     * @param bitmapPoolDisabled 禁止从bitmap pool从寻找可复用的bitmap
     * @return LoadOptions
     */
    public LoadOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        this.bitmapPoolDisabled = bitmapPoolDisabled;
        return this;
    }

    /**
     * 是否纠正图片的方向，让被旋转了的图片以正常方向显示
     */
    public boolean isCorrectImageOrientation() {
        return correctImageOrientation;
    }

    /**
     * 设置纠正图片的方向，让被旋转了的图片以正常方向显示
     *
     * @param correctImageOrientation true：纠正图片的方向，让被旋转了的图片以正常方向显示
     * @return LoadOptions
     */
    public LoadOptions setCorrectImageOrientation(boolean correctImageOrientation) {
        this.correctImageOrientation = correctImageOrientation;
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
        forceUseResize = false;
        bitmapConfig = null;
        inPreferQualityOverSpeed = false;
        thumbnailMode = false;
        cacheProcessedImageInDisk = false;
        bitmapPoolDisabled = false;
        correctImageOrientation = false;
    }

    /**
     * 拷贝属性，绝对的覆盖
     *
     * @param options 来源
     */
    public void copy(LoadOptions options) {
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
        forceUseResize = options.forceUseResize;
        bitmapConfig = options.bitmapConfig;
        inPreferQualityOverSpeed = options.inPreferQualityOverSpeed;
        thumbnailMode = options.thumbnailMode;
        cacheProcessedImageInDisk = options.cacheProcessedImageInDisk;
        bitmapPoolDisabled = options.bitmapPoolDisabled;
        correctImageOrientation = options.correctImageOrientation;
    }

    /**
     * 合并指定的LoadOptions，合并的过程并不是绝对的覆盖，专门为{@link LoadHelper#options(LoadOptions)}方法提供
     * <br>简单来说自己已经设置了的属性不会被覆盖，对于都设置了但可以比较大小的，较小的优先
     */
    public void merge(LoadOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.merge((DownloadOptions) options);

        if (maxSize == null) {
            maxSize = options.getMaxSize();
        } else if (options.getMaxSize() != null) {
            // 当两者都有值时谁的像素数少用谁
            int optionMaxSizePixelCount = options.getMaxSize().getWidth() * options.getMaxSize().getHeight();
            int oldMaxSizePixelCount = maxSize.getWidth() * maxSize.getHeight();
            if (optionMaxSizePixelCount < oldMaxSizePixelCount) {
                maxSize = options.getMaxSize();
            }
        }

        if (resize == null) {
            resize = options.resize;
        }

        if (!forceUseResize) {
            forceUseResize = options.forceUseResize;
        }

        if (!lowQualityImage) {
            lowQualityImage = options.lowQualityImage;
        }

        if (imageProcessor == null) {
            imageProcessor = options.imageProcessor;
        }

        if (!decodeGifImage) {
            decodeGifImage = options.decodeGifImage;
        }

        if (bitmapConfig == null) {
            bitmapConfig = options.bitmapConfig;
        }

        if (!inPreferQualityOverSpeed) {
            inPreferQualityOverSpeed = options.inPreferQualityOverSpeed;
        }

        if (!thumbnailMode) {
            thumbnailMode = options.thumbnailMode;
        }

        if (!cacheProcessedImageInDisk) {
            cacheProcessedImageInDisk = options.cacheProcessedImageInDisk;
        }

        if (!bitmapPoolDisabled) {
            bitmapPoolDisabled = options.bitmapPoolDisabled;
        }

        if (!correctImageOrientation) {
            correctImageOrientation = options.correctImageOrientation;
        }
    }

    @Override
    public StringBuilder makeKey(StringBuilder builder) {
        super.makeKey(builder);

        if (maxSize != null) {
            builder.append("_").append(maxSize.getKey());
        }
        if (resize != null) {
            builder.append("_").append(resize.getKey());
            if (forceUseResize) {
                builder.append("_").append("forceUseResize");
            }
            if (thumbnailMode) {
                builder.append("_").append("thumbnailMode");
            }
        }
        if (correctImageOrientation) {
            builder.append("_").append("correctImageOrientation");
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
        return builder;
    }

    @Override
    public StringBuilder makeStateImageKey(StringBuilder builder) {
        super.makeKey(builder);

        if (resize != null) {
            builder.append("_").append(resize.getKey());
            if (forceUseResize) {
                builder.append("_").append("forceUseResize");
            }
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
        return builder;
    }
}
