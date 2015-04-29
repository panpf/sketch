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

package me.xiaopan.spear;

import android.content.Context;
import android.widget.ImageView.ScaleType;

import me.xiaopan.spear.display.ImageDisplayer;
import me.xiaopan.spear.process.ImageProcessor;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    private boolean enableMemoryCache = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private ImageDisplayer imageDisplayer;	// 图片显示器
    private DrawableHolder loadingDrawableHolder;	//当正在加载时显示的图片
    private DrawableHolder loadFailDrawableHolder;	//当加载失败时显示的图片
    private DrawableHolder pauseDownloadDrawableHolder;	//暂停下载时显示的图片

    private boolean resizeByImageViewLayoutSize;

    public DisplayOptions(Context context) {
        super(context);
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
     * 获取加载中时显示的图片
     * @return 加载中时显示的图片
     */
    public DrawableHolder getLoadingDrawableHolder() {
        return loadingDrawableHolder;
    }

    /**
     * 设置正在加载时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setLoadingDrawable(int drawableResId) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        loadingDrawableHolder.setProcess(false);
        return this;
    }

    /**
     * 设置正在加载时候显示的图片
     * @param drawableResId 资源图片ID
     * @param isProcess 是否使用ImageProcessor处理
     * @return DisplayOptions
     */
    public DisplayOptions setLoadingDrawable(int drawableResId, boolean isProcess) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        loadingDrawableHolder.setProcess(isProcess);
        return this;
    }

    /**
     * 获取加载失败时显示的图片
     * @return 加载失败时显示的图片
     */
    public DrawableHolder getLoadFailDrawableHolder() {
        return loadFailDrawableHolder;
    }

    /**
     * 设置加载失败时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setLoadFailDrawable(int drawableResId) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        loadFailDrawableHolder.setProcess(false);
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     * @param drawableResId 资源图片ID
     * @param isProcess 是否使用ImageProcessor处理
     * @return DisplayOptions
     */
    public DisplayOptions setLoadFailDrawable(int drawableResId, boolean isProcess) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        loadFailDrawableHolder.setProcess(isProcess);
        return this;
    }

    /**
     * 设置暂停下载时的占位图
     * @return 暂停下载时的占位图
     */
    public DrawableHolder getPauseDownloadDrawableHolder() {
        return pauseDownloadDrawableHolder;
    }

    /**
     * 设置暂停下载时显示的图片
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setPauseDownloadDrawable(int drawableResId) {
        if(pauseDownloadDrawableHolder == null){
            pauseDownloadDrawableHolder = new DrawableHolder();
        }
        pauseDownloadDrawableHolder.setResId(drawableResId);
        pauseDownloadDrawableHolder.setProcess(false);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     * @param drawableResId 资源图片ID
     * @param isProcess 是否使用ImageProcessor处理
     * @return DisplayOptions
     */
    public DisplayOptions setPauseDownloadDrawable(int drawableResId, boolean isProcess) {
        if(pauseDownloadDrawableHolder == null){
            pauseDownloadDrawableHolder = new DrawableHolder();
        }
        pauseDownloadDrawableHolder.setResId(drawableResId);
        pauseDownloadDrawableHolder.setProcess(isProcess);
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
    }

    @Override
    public DisplayOptions setImageProcessor(ImageProcessor processor) {
        super.setImageProcessor(processor);
        if(loadingDrawableHolder != null && loadingDrawableHolder.isProcess()){
            loadingDrawableHolder.reset();
        }
        if(loadFailDrawableHolder != null && loadFailDrawableHolder.isProcess()){
            loadFailDrawableHolder.reset();
        }
        if(pauseDownloadDrawableHolder != null && pauseDownloadDrawableHolder.isProcess()){
            pauseDownloadDrawableHolder.reset();
        }
        return this;
    }

    @Override
    public DisplayOptions setEnableDiskCache(boolean isEnableDiskCache) {
        super.setEnableDiskCache(isEnableDiskCache);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(ImageSize maxSize){
        super.setMaxSize(maxSize);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(int width, int height) {
        super.setMaxSize(width, height);
        return this;
    }

    @Override
    public DisplayOptions setResize(ImageSize resize){
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
    public DisplayOptions setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        return this;
    }

    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        super.setDecodeGifImage(decodeGifImage);
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
        this.loadingDrawableHolder = displayOptions.loadingDrawableHolder;
        this.loadFailDrawableHolder = displayOptions.loadFailDrawableHolder;
        this.pauseDownloadDrawableHolder = displayOptions.pauseDownloadDrawableHolder;
        this.resizeByImageViewLayoutSize = displayOptions.resizeByImageViewLayoutSize;

        setScaleType(displayOptions.getScaleType());
        setMaxSize(displayOptions.getMaxSize());
        setResize(displayOptions.getResize());
        setImageProcessor(displayOptions.getImageProcessor());
        setDecodeGifImage(displayOptions.isDecodeGifImage());

        setEnableDiskCache(displayOptions.isEnableDiskCache());
        setRequestLevel(displayOptions.getRequestLevel());
    }
}
