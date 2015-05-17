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
    private boolean enableMemoryCache = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private ImageDisplayer imageDisplayer;	// 图片显示器
    private ImageHolder loadingImage;	//当正在加载时显示的图片
    private ImageHolder failureImage;	//当失败时显示的图片
    private ImageHolder pauseDownloadImage;	//暂停下载时显示的图片

    private boolean resizeByImageViewLayoutSize;

    public DisplayOptions() {
    }

    public DisplayOptions(DisplayOptions from){
        super(from);
        copyOf(from);
    }

    /**
     * 是否开启了内存缓存
     * @return 是否开启了内存缓存
     */
    public boolean isEnableMemoryCache() {
        return enableMemoryCache;
    }

    /**
     * 设置是否开启内存缓存
     * @param isEnableMemoryCache 是否开启内存缓存
     * @return DisplayOptions
     */
    public DisplayOptions setEnableMemoryCache(boolean isEnableMemoryCache) {
        this.enableMemoryCache = isEnableMemoryCache;
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
    public ImageHolder getLoadingImage() {
        return loadingImage;
    }

    /**
     * 设置正在加载时显示的图片
     * @param loadingImage 正在加载时显示的图片
     */
    public DisplayOptions setLoadingImage(ImageHolder loadingImage) {
        if(this.loadingImage != null){
            this.loadingImage.recycle();
        }
        this.loadingImage = loadingImage;
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
        return failureImage;
    }

    /**
     * 设置失败时显示的图片
     * @param failureImage 失败时显示的图片
     */
    public DisplayOptions setFailureImage(ImageHolder failureImage) {
        if(this.failureImage != null){
            this.failureImage.recycle();
        }
        this.failureImage = failureImage;
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
        return pauseDownloadImage;
    }

    /**
     * 设置暂停下载时显示的图片
     * @param pauseDownloadImage 暂停下载时显示的图片
     */
    public DisplayOptions setPauseDownloadImage(ImageHolder pauseDownloadImage) {
        if(this.pauseDownloadImage != null){
            this.pauseDownloadImage.recycle();
        }
        this.pauseDownloadImage = pauseDownloadImage;
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
     * 是否需要根据ImageView的LayoutSize来调整resize
     * @return true：是
     */
    public boolean isResizeByImageViewLayoutSize() {
        return resizeByImageViewLayoutSize;
    }

    /**
     * 设置是否需要根据ImageView的LayoutSize来调整resize
     * @param isResizeByImageViewLayoutSize true：是
     */
    public void setResizeByImageViewLayoutSize(boolean isResizeByImageViewLayoutSize) {
        this.resizeByImageViewLayoutSize = isResizeByImageViewLayoutSize;
        if(this.resizeByImageViewLayoutSize && getResize() != null){
            super.setResize(null);
        }
    }

    @Override
    public DisplayOptions setImageProcessor(ImageProcessor processor) {
        super.setImageProcessor(processor);
        return this;
    }

    @Override
    public DisplayOptions setEnableDiskCache(boolean isEnableDiskCache) {
        super.setEnableDiskCache(isEnableDiskCache);
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
        this.resizeByImageViewLayoutSize = false;
        return this;
    }

    @Override
    public DisplayOptions setResize(int width, int height) {
        super.setResize(width, height);
        this.resizeByImageViewLayoutSize = false;
        return this;
    }

    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        super.setDecodeGifImage(decodeGifImage);
        return this;
    }

    @Override
    public DisplayOptions setImagesOfLowQuality(boolean imagesOfLowQuality) {
        super.setImagesOfLowQuality(imagesOfLowQuality);
        return this;
    }

    @Override
    public DisplayOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    public void copyOf(DisplayOptions displayOptions){
        this.enableMemoryCache = displayOptions.enableMemoryCache;
        this.imageDisplayer = displayOptions.imageDisplayer;
        this.loadingImage = displayOptions.loadingImage;
        this.failureImage = displayOptions.failureImage;
        this.pauseDownloadImage = displayOptions.pauseDownloadImage;
        this.resizeByImageViewLayoutSize = displayOptions.resizeByImageViewLayoutSize;

        setMaxSize(displayOptions.getMaxSize());
        setResize(displayOptions.getResize());
        setImagesOfLowQuality(displayOptions.isImagesOfLowQuality());
        setImageProcessor(displayOptions.getImageProcessor());
        setDecodeGifImage(displayOptions.isDecodeGifImage());

        setEnableDiskCache(displayOptions.isEnableDiskCache());
        setRequestLevel(displayOptions.getRequestLevel());
    }
}
