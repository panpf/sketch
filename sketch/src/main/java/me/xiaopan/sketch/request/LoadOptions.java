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

import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions {
    private Resize resize;
    private MaxSize maxSize;
    private boolean decodeGifImage;
    private boolean forceUseResize;
    private boolean lowQualityImage;
    private ImageProcessor imageProcessor;

    public LoadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public LoadOptions(LoadOptions from) {
        copy(from);
    }

    @Override
    public LoadOptions setCacheInDisk(boolean cacheInDisk) {
        super.setCacheInDisk(cacheInDisk);
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

    /**
     * 获取最大尺寸，在解码的时候会使用此最大尺寸来计算inSimpleSize
     */
    public MaxSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此最大尺寸来计算inSimpleSize
     */
    public LoadOptions setMaxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     */
    public LoadOptions setMaxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 获取新尺寸
     *
     * @return 新尺寸
     */
    public Resize getResize() {
        return resize;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public LoadOptions setResize(Resize resize) {
        this.resize = resize;
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public LoadOptions setResize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 获取图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     */
    public LoadOptions setImageProcessor(ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 是否解码GIF图片（默认是）
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置是否解码GIF图片（默认是）
     */
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    /**
     * 是否返回低质量的图片
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置是否返回低质量的图片
     */
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    /**
     * 是否强制使经过resize处理的图片同resize的尺寸一致
     */
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    /**
     * 设置是否强制使经过resize处理的图片同resize的尺寸一致
     */
    public LoadOptions setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    @Override
    public void reset() {
        super.reset();

        maxSize = null;
        resize = null;
        lowQualityImage = false;
        imageProcessor = null;
        decodeGifImage = true;
        forceUseResize = false;
    }

    /**
     * 拷贝属性，绝对的覆盖
     */
    public void copy(LoadOptions options) {
        if(options == null){
            return;
        }

        super.copy(options);

        maxSize = options.maxSize;
        resize = options.resize;
        lowQualityImage = options.lowQualityImage;
        imageProcessor = options.imageProcessor;
        decodeGifImage = options.decodeGifImage;
        forceUseResize = options.forceUseResize;
    }

    /**
     * 应用属性，应用的过程并不是绝对的覆盖
     */
    public void apply(LoadOptions options) {
        if(options == null){
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

        if (resize == null && options.getResize() != null) {
            resize = new Resize(options.getResize());
        }

        forceUseResize = options.isForceUseResize();
        lowQualityImage = options.isLowQualityImage();

        if (imageProcessor == null) {
            imageProcessor = options.getImageProcessor();
        }

        decodeGifImage = options.isDecodeGifImage();
    }
}
