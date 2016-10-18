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

import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions {
    /**
     * 修正尺寸，将原始图片加载到内存中之后根据resize进行修正。修正的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    private Resize resize;

    /**
     * 强制使经过resize处理的图片同resize的尺寸一致
     */
    private boolean forceUseResize;

    /**
     * 最大尺寸，在解码的时候会使用此最大尺寸来计算inSimpleSize
     */
    private MaxSize maxSize;

    /**
     * 解码GIF图片（默认否）
     */
    private boolean decodeGifImage;

    /**
     * 返回低质量的图片（默认否）
     */
    private boolean lowQualityImage;

    /**
     * 解码时优先考虑速度还是质量 (true：质量；false：速度，默认false)
     */
    private boolean inPreferQualityOverSpeed;

    /**
     * 缩略图模式，当resize的宽高比同原始图片的宽高比相差非常大的时候会用BitmapRegionDecoder从原始图片中截取合适的部分
     */
    private boolean thumbnailMode;

    /**
     * 图片处理器，根据resize和ScaleType创建一张新的图片
     */
    private ImageProcessor imageProcessor;

    /**
     * 图片质量配置
     */
    private Bitmap.Config bitmapConfig;

    public LoadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public LoadOptions(LoadOptions from) {
        copy(from);
    }

    @Override
    public LoadOptions setDisableCacheInDisk(boolean disableCacheInDisk) {
        super.setDisableCacheInDisk(disableCacheInDisk);
        return this;
    }

    @Override
    public LoadOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    @Override
    LoadOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        super.setRequestLevelFrom(requestLevelFrom);
        return this;
    }

    public MaxSize getMaxSize() {
        return maxSize;
    }

    public LoadOptions setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public LoadOptions setMaxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    public Resize getResize() {
        return resize;
    }

    public LoadOptions setResize(Resize resize) {
        this.resize = resize;
        return this;
    }

    public boolean isForceUseResize() {
        return forceUseResize;
    }

    public LoadOptions setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    public LoadOptions setResize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    public LoadOptions setImageProcessor(ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    public LoadOptions setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
        return this;
    }

    public boolean isInPreferQualityOverSpeed() {
        return inPreferQualityOverSpeed;
    }

    public LoadOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        this.inPreferQualityOverSpeed = inPreferQualityOverSpeed;
        return this;
    }

    public boolean isThumbnailMode() {
        return thumbnailMode;
    }

    public LoadOptions setThumbnailMode(boolean thumbnailMode) {
        this.thumbnailMode = thumbnailMode;
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
    }

    /**
     * 拷贝属性，绝对的覆盖
     */
    public void copy(LoadOptions options) {
        if (options == null) {
            return;
        }

        super.copy(options);

        maxSize = options.maxSize;
        resize = options.resize;
        lowQualityImage = options.lowQualityImage;
        imageProcessor = options.imageProcessor;
        decodeGifImage = options.decodeGifImage;
        forceUseResize = options.forceUseResize;
        bitmapConfig = options.bitmapConfig;
        inPreferQualityOverSpeed = options.inPreferQualityOverSpeed;
        thumbnailMode = options.thumbnailMode;
    }

    /**
     * 应用属性，应用的过程并不是绝对的覆盖
     */
    public void apply(LoadOptions options) {
        if (options == null) {
            return;
        }

        super.apply(options);

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
            resize = options.getResize();
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
    }

    @Override
    public StringBuilder appendOptionsToId(StringBuilder builder) {
        super.appendOptionsToId(builder);

        if (maxSize != null) {
            maxSize.appendIdentifier("_", builder);
        }
        if (resize != null) {
            resize.appendIdentifier("_", builder);
        }
        if (forceUseResize) {
            builder.append("_").append("forceUseResize");
        }
        if (lowQualityImage) {
            builder.append("_").append("lowQualityImage");
        }
        if (inPreferQualityOverSpeed) {
            builder.append("_").append("preferQuality");
        }
        if (thumbnailMode) {
            builder.append("_").append("thumbnailMode");
        }
        if (bitmapConfig != null) {
            builder.append("_").append(bitmapConfig.name());
        }
        if (imageProcessor != null) {
            imageProcessor.appendIdentifier("_", builder);
        }
        return builder;
    }
}
