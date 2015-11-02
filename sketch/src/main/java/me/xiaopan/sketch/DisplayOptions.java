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

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    private boolean cacheInMemory = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private ImageDisplayer imageDisplayer;	// 图片显示器
    private ImageHolder loadingImageHolder;	//当正在加载时显示的图片
    private ImageHolder failureImageHolder;	//当失败时显示的图片
    private ImageHolder pauseDownloadImageHolder;	//暂停下载时显示的图片

    private boolean resizeByFixedSize;

    public DisplayOptions() {
    }

    public DisplayOptions(DisplayOptions from){
        super(from);
        copyOf(from);
    }

    /**
     * 是否将图片缓存在内存中
     * @return 是否将图片缓存在内存中（默认是）
     */
    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    /**
     * 设置是否将图片缓存在内存中
     * @param cacheInMemory 是否将图片缓存在内存中（默认是）
     * @return DisplayOptions
     */
    public DisplayOptions setCacheInMemory(boolean cacheInMemory) {
        this.cacheInMemory = cacheInMemory;
        return this;
    }

    /**
     * 获取图片显示器
     * @return 图片显示器
     */
    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     * @param displayer 图片显示器
     * @return DisplayOptions
     */
    public DisplayOptions setImageDisplayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    /**
     * 获取正在加载时显示的图片
     * @return 正在加载时显示的图片
     */
    public ImageHolder getLoadingImageHolder() {
        return loadingImageHolder;
    }

    /**
     * 设置正在加载时显示的图片
     * @param loadingImageHolder 正在加载时显示的图片
     */
    public DisplayOptions setLoadingImage(ImageHolder loadingImageHolder) {
        this.loadingImageHolder = loadingImageHolder;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setLoadingImage(int drawableResId) {
        setLoadingImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 获取失败时显示的图片
     * @return 失败时显示的图片
     */
    public ImageHolder getFailureImage() {
        return failureImageHolder;
    }

    /**
     * 设置失败时显示的图片
     * @param failureImageHolder 失败时显示的图片
     */
    public DisplayOptions setFailureImage(ImageHolder failureImageHolder) {
        this.failureImageHolder = failureImageHolder;
        return this;
    }

    /**
     * 设置失败时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setFailureImage(int drawableResId) {
        setFailureImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 获取暂停下载时显示的图片
     * @return 暂停下载时显示的图片
     */
    public ImageHolder getPauseDownloadImage() {
        return pauseDownloadImageHolder;
    }

    /**
     * 设置暂停下载时显示的图片
     * @param pauseDownloadImageHolder 暂停下载时显示的图片
     */
    public DisplayOptions setPauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        this.pauseDownloadImageHolder = pauseDownloadImageHolder;
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setPauseDownloadImage(int drawableResId) {
        setPauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 是否使用ImageView的LayoutSize作为resize
     * @return true：是
     */
    public boolean isResizeByFixedSize() {
        return resizeByFixedSize;
    }

    /**
     * 设置是否使用ImageView的LayoutSize作为resize
     * @param isResizeByFixedSize true：是
     */
    public DisplayOptions setResizeByFixedSize(boolean isResizeByFixedSize) {
        this.resizeByFixedSize = isResizeByFixedSize;
        if(this.resizeByFixedSize && getResize() != null){
            super.setResize(null);
        }
        return this;
    }

    @Override
    public DisplayOptions setImageProcessor(ImageProcessor processor) {
        super.setImageProcessor(processor);
        return this;
    }

    @Override
    public DisplayOptions setCacheInDisk(boolean cacheInDisk) {
        super.setCacheInDisk(cacheInDisk);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(MaxSize maxSize){
        super.setMaxSize(maxSize);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(int width, int height) {
        super.setMaxSize(width, height);
        return this;
    }

    @Override
    public DisplayOptions setResize(Resize resize){
        super.setResize(resize);
        this.resizeByFixedSize = false;
        return this;
    }

    @Override
    public DisplayOptions setResize(int width, int height) {
        super.setResize(width, height);
        this.resizeByFixedSize = false;
        return this;
    }

    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        super.setDecodeGifImage(decodeGifImage);
        return this;
    }

    @Override
    public DisplayOptions setLowQualityImage(boolean lowQualityImage) {
        super.setLowQualityImage(lowQualityImage);
        return this;
    }

    @Override
    public DisplayOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    @Override
    public DisplayOptions setForceUseResize(boolean forceUseResize) {
        super.setForceUseResize(forceUseResize);
        return this;
    }

    public void copyOf(DisplayOptions displayOptions){
        this.cacheInMemory = displayOptions.cacheInMemory;
        this.imageDisplayer = displayOptions.imageDisplayer;
        this.resizeByFixedSize = displayOptions.resizeByFixedSize;
        this.loadingImageHolder = displayOptions.loadingImageHolder;
        this.failureImageHolder = displayOptions.failureImageHolder;
        this.pauseDownloadImageHolder = displayOptions.pauseDownloadImageHolder;

        super.setMaxSize(displayOptions.getMaxSize());
        super.setResize(displayOptions.getResize());
        super.setLowQualityImage(displayOptions.isLowQualityImage());
        super.setImageProcessor(displayOptions.getImageProcessor());
        super.setDecodeGifImage(displayOptions.isDecodeGifImage());

        super.setCacheInDisk(displayOptions.isCacheInDisk());
        super.setRequestLevel(displayOptions.getRequestLevel());
    }
}
