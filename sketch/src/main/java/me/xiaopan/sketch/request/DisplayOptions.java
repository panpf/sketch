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
    /**
     * 禁用内存缓存（默认否）
     */
    private boolean disableCacheInMemory;

    /**
     * 图片显示器，在加载完成后会调用此显示器来显示图片
     */
    private ImageDisplayer imageDisplayer;

    /**
     * 正在加载时显示的图片
     */
    private ModeImage loadingImage;

    /**
     * 加载错误时显示的图片
     */
    private ModeImage errorImage;

    /**
     * 暂停下载时显示的图片
     */
    private ModeImage pauseDownloadImage;

    /**
     * 使用ImageView的layout_width和layout_height作为resize（默认否）
     */
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

    @Override
    public DisplayOptions setThumbnailMode(boolean thumbnailMode) {
        super.setThumbnailMode(thumbnailMode);
        return this;
    }

    public boolean isDisableCacheInMemory() {
        return disableCacheInMemory;
    }

    public DisplayOptions setDisableCacheInMemory(boolean disableCacheInMemory) {
        this.disableCacheInMemory = disableCacheInMemory;
        return this;
    }

    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    public DisplayOptions setImageDisplayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    public ModeImage getLoadingImage() {
        return loadingImage;
    }

    public DisplayOptions setLoadingImage(ModeImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    public DisplayOptions setLoadingImage(int drawableResId) {
        setLoadingImage(new DrawableModeImage(drawableResId));
        return this;
    }

    public ModeImage getErrorImage() {
        return errorImage;
    }

    public DisplayOptions setErrorImage(ModeImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    public DisplayOptions setErrorImage(int drawableResId) {
        setErrorImage(new DrawableModeImage(drawableResId));
        return this;
    }

    public ModeImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    public DisplayOptions setPauseDownloadImage(ModeImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    public DisplayOptions setPauseDownloadImage(int drawableResId) {
        setPauseDownloadImage(new DrawableModeImage(drawableResId));
        return this;
    }

    public boolean isResizeByFixedSize() {
        return resizeByFixedSize;
    }

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
        loadingImage = null;
        errorImage = null;
        pauseDownloadImage = null;
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
        loadingImage = options.loadingImage;
        errorImage = options.errorImage;
        pauseDownloadImage = options.pauseDownloadImage;
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

        if (loadingImage == null) {
            loadingImage = options.getLoadingImage();
        }

        if (errorImage == null) {
            errorImage = options.getErrorImage();
        }

        if (pauseDownloadImage == null) {
            pauseDownloadImage = options.getPauseDownloadImage();
        }

        if (!resizeByFixedSize) {
            resizeByFixedSize = options.isResizeByFixedSize();
        }
    }
}
