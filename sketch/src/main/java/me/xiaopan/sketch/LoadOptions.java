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

package me.xiaopan.sketch;

import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions{
    private Resize resize;
    private MaxSize maxSize;
    private boolean decodeGifImage = true;
    private boolean forceUseResize;
    private boolean lowQualityImage;
    private ImageProcessor imageProcessor;

    public LoadOptions() {
    }

    public LoadOptions(LoadOptions from){
        copyOf(from);
    }

    @Override
    public LoadOptions setCacheInDisk(boolean cacheInDisk) {
        super.setCacheInDisk(cacheInDisk);
        return this;
    }

    /**
     * 获取最大尺寸
     * @return 最大尺寸
     */
    public MaxSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(MaxSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(int width, int height){
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 获取新尺寸
     * @return 新尺寸
     */
    public Resize getResize() {
        return resize;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param resize 新的尺寸
     * @return LoadOptions
     */
    public LoadOptions setResize(Resize resize){
        this.resize = resize;
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions setResize(int width, int height){
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 获取图片处理器
     * @return 图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor 图片处理器
     * @return LoadOptions
     */
    public LoadOptions setImageProcessor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 是否解码GIF图片
     * @return true：是
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置是否解码GIF图片
     * @param decodeGifImage true：是
     */
    public LoadOptions setDecodeGifImage(boolean decodeGifImage) {
        this.decodeGifImage = decodeGifImage;
        return this;
    }

    @Override
    public LoadOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    /**
     * 是否返回低质量的图片
     * @return true: 是
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置是否返回低质量的图片
     * @param lowQualityImage true：是
     */
    public LoadOptions setLowQualityImage(boolean lowQualityImage) {
        this.lowQualityImage = lowQualityImage;
        return this;
    }

    /**
     * 是否强制使经过resize返回的图片同resize的尺寸一致
     * @return true：强制使经过resize返回的图片同resize的尺寸一致
     */
    public boolean isForceUseResize() {
        return forceUseResize;
    }

    /**
     * 设置是否强制使经过resize返回的图片同resize的尺寸一致
     * @param forceUseResize true：强制使经过resize返回的图片同resize的尺寸一致
     */
    public LoadOptions setForceUseResize(boolean forceUseResize) {
        this.forceUseResize = forceUseResize;
        return this;
    }

    public void copyOf(LoadOptions loadOptions){
        this.maxSize = loadOptions.getMaxSize();
        this.resize = loadOptions.getResize();
        this.lowQualityImage = loadOptions.lowQualityImage;
        this.imageProcessor = loadOptions.getImageProcessor();
        this.decodeGifImage = loadOptions.isDecodeGifImage();
        this.forceUseResize = loadOptions.isForceUseResize();

        super.setCacheInDisk(loadOptions.isCacheInDisk());
        super.setRequestLevel(loadOptions.getRequestLevel());
    }
}
