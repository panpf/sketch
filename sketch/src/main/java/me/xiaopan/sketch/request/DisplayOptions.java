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

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    private boolean disableCacheInMemory;
    private ImageDisplayer imageDisplayer;
    private ImageHolder loadingImageHolder;
    private ImageHolder failedImageHolder;
    private ImageHolder pauseDownloadImageHolder;
    private boolean resizeByFixedSize;

    public DisplayOptions() {
        reset();
    }

    public DisplayOptions(DisplayOptions from) {
        copy(from);
    }

    @Override
    public DisplayOptions setDisableCacheInDisk(boolean disableCacheInDisk) {
        super.setDisableCacheInDisk(disableCacheInDisk);
        return this;
    }

    @Override
    public DisplayOptions setRequestLevel(RequestLevel requestLevel) {
        super.setRequestLevel(requestLevel);
        return this;
    }

    @Override
    DisplayOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        super.setRequestLevelFrom(requestLevelFrom);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(MaxSize maxSize) {
        super.setMaxSize(maxSize);
        return this;
    }

    @Override
    public DisplayOptions setMaxSize(int width, int height) {
        super.setMaxSize(width, height);
        return this;
    }

    @Override
    public DisplayOptions setResize(Resize resize) {
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
    public DisplayOptions setForceUseResize(boolean forceUseResize) {
        super.setForceUseResize(forceUseResize);
        return this;
    }

    @Override
    public DisplayOptions setImageProcessor(ImageProcessor processor) {
        super.setImageProcessor(processor);
        return this;
    }

    @Override
    public DisplayOptions setBitmapConfig(Bitmap.Config bitmapConfig) {
        super.setBitmapConfig(bitmapConfig);
        return this;
    }

    @Override
    public DisplayOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        super.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 是否禁用内存缓存
     */
    public boolean isDisableCacheInMemory() {
        return disableCacheInMemory;
    }

    /**
     * 设置是否禁用内存缓存
     */
    public DisplayOptions setDisableCacheInMemory(boolean disableCacheInMemory) {
        this.disableCacheInMemory = disableCacheInMemory;
        return this;
    }

    /**
     * 获取图片显示器
     */
    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     */
    public DisplayOptions setImageDisplayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    /**
     * 获取正在加载时显示的图片
     */
    public ImageHolder getLoadingImageHolder() {
        return loadingImageHolder;
    }

    /**
     * 设置正在加载时显示的图片
     */
    public DisplayOptions setLoadingImage(ImageHolder loadingImageHolder) {
        this.loadingImageHolder = loadingImageHolder;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    public DisplayOptions setLoadingImage(int drawableResId) {
        setLoadingImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 获取失败时显示的图片
     */
    public ImageHolder getFailedImageHolder() {
        return failedImageHolder;
    }

    /**
     * 设置失败时显示的图片
     */
    public DisplayOptions setFailedImage(ImageHolder failedImageHolder) {
        this.failedImageHolder = failedImageHolder;
        return this;
    }

    /**
     * 设置失败时显示的图片
     */
    public DisplayOptions setFailedImage(int drawableResId) {
        setFailedImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 获取暂停下载时显示的图片
     */
    public ImageHolder getPauseDownloadImageHolder() {
        return pauseDownloadImageHolder;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    public DisplayOptions setPauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        this.pauseDownloadImageHolder = pauseDownloadImageHolder;
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    public DisplayOptions setPauseDownloadImage(int drawableResId) {
        setPauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 是否使用ImageView的layout_width和layout_height作为resize
     */
    public boolean isResizeByFixedSize() {
        return resizeByFixedSize;
    }

    /**
     * 设置是否使用ImageView的layout_width和layout_height作为resize
     */
    public DisplayOptions setResizeByFixedSize(boolean isResizeByFixedSize) {
        this.resizeByFixedSize = isResizeByFixedSize;
        if (this.resizeByFixedSize && getResize() != null) {
            super.setResize(null);
        }
        return this;
    }

    @Override
    public void reset() {
        super.reset();

        disableCacheInMemory = false;
        imageDisplayer = null;
        resizeByFixedSize = false;
        loadingImageHolder = null;
        failedImageHolder = null;
        pauseDownloadImageHolder = null;
    }

    /**
     * 拷贝属性，绝对的覆盖
     */
    public void copy(DisplayOptions options) {
        if (options == null) {
            return;
        }

        super.copy(options);

        disableCacheInMemory = options.disableCacheInMemory;
        imageDisplayer = options.imageDisplayer;
        resizeByFixedSize = options.resizeByFixedSize;
        loadingImageHolder = options.loadingImageHolder;
        failedImageHolder = options.failedImageHolder;
        pauseDownloadImageHolder = options.pauseDownloadImageHolder;
    }

    /**
     * 应用属性，应用的过程并不是绝对的覆盖
     */
    public void apply(DisplayOptions options) {
        if (options == null) {
            return;
        }

        super.apply(options);

        if (!disableCacheInMemory) {
            disableCacheInMemory = options.isDisableCacheInMemory();
        }

        if (imageDisplayer == null) {
            imageDisplayer = options.getImageDisplayer();
        }

        if (loadingImageHolder == null) {
            loadingImageHolder = options.getLoadingImageHolder();
        }

        if (failedImageHolder == null) {
            failedImageHolder = options.getFailedImageHolder();
        }

        if (pauseDownloadImageHolder == null) {
            pauseDownloadImageHolder = options.getPauseDownloadImageHolder();
        }

        if (!resizeByFixedSize) {
            resizeByFixedSize = options.isResizeByFixedSize();
        }
    }
}
