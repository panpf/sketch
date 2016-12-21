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
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.StateImage;

/**
 * 显示选项
 */
public class DisplayOptions extends LoadOptions {
    /**
     * 禁用内存缓存
     */
    private boolean cacheInMemoryDisabled;

    /**
     * 图片显示器，在加载完成后会调用此显示器来显示图片
     */
    private ImageDisplayer imageDisplayer;

    /**
     * 正在加载时显示的图片
     */
    private StateImage loadingImage;

    /**
     * 加载错误时显示的图片
     */
    private StateImage errorImage;

    /**
     * 暂停下载时显示的图片
     */
    private StateImage pauseDownloadImage;

    /**
     * 使用ImageView的layout_width和layout_height作为resize
     */
    private boolean resizeByFixedSize;

    /**
     * 绘制时修改图片的形状
     */
    private ImageShaper imageShaper;

    /**
     * 绘制时修改图片的尺寸
     */
    private ShapeSize shapeSize;

    /**
     * 使用ImageView的layout_width和layout_height作为shape size
     */
    private boolean shapeSizeByFixedSize;

    public DisplayOptions() {
        reset();
    }

    public DisplayOptions(DisplayOptions from) {
        copy(from);
    }

    @Override
    public DisplayOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        super.setCacheInDiskDisabled(cacheInDiskDisabled);
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
        return this;
    }

    @Override
    public DisplayOptions setResize(int width, int height) {
        super.setResize(width, height);
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

    @Override
    public DisplayOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        super.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
        return this;
    }

    @Override
    public DisplayOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        super.setBitmapPoolDisabled(bitmapPoolDisabled);
        return this;
    }

    public boolean isCacheInMemoryDisabled() {
        return cacheInMemoryDisabled;
    }

    public DisplayOptions setCacheInMemoryDisabled(boolean cacheInMemoryDisabled) {
        this.cacheInMemoryDisabled = cacheInMemoryDisabled;
        return this;
    }

    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    public DisplayOptions setImageDisplayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    public StateImage getLoadingImage() {
        return loadingImage;
    }

    public DisplayOptions setLoadingImage(int drawableResId) {
        setLoadingImage(new DrawableStateImage(drawableResId));
        return this;
    }

    public DisplayOptions setLoadingImage(StateImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    public StateImage getErrorImage() {
        return errorImage;
    }

    public DisplayOptions setErrorImage(int drawableResId) {
        setErrorImage(new DrawableStateImage(drawableResId));
        return this;
    }

    public DisplayOptions setErrorImage(StateImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    public StateImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    public DisplayOptions setPauseDownloadImage(int drawableResId) {
        setPauseDownloadImage(new DrawableStateImage(drawableResId));
        return this;
    }

    public DisplayOptions setPauseDownloadImage(StateImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    public boolean isResizeByFixedSize() {
        return resizeByFixedSize;
    }

    public DisplayOptions setResizeByFixedSize(boolean isResizeByFixedSize) {
        this.resizeByFixedSize = isResizeByFixedSize;
        return this;
    }

    public ImageShaper getImageShaper() {
        return imageShaper;
    }

    public DisplayOptions setImageShaper(ImageShaper imageShaper) {
        this.imageShaper = imageShaper;
        return this;
    }

    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    public DisplayOptions setShapeSize(ShapeSize shapeSize) {
        this.shapeSize = shapeSize;
        this.shapeSizeByFixedSize = false;
        return this;
    }

    public DisplayOptions setShapeSize(int width, int height) {
        return setShapeSize(new ShapeSize(width, height));
    }

    public boolean isShapeSizeByFixedSize() {
        return shapeSizeByFixedSize;
    }

    public DisplayOptions setShapeSizeByFixedSize(boolean shapeSizeByFixedSize) {
        this.shapeSizeByFixedSize = shapeSizeByFixedSize;
        return this;
    }

    @Override
    public void reset() {
        super.reset();

        cacheInMemoryDisabled = false;
        imageDisplayer = null;
        resizeByFixedSize = false;
        loadingImage = null;
        errorImage = null;
        pauseDownloadImage = null;
        imageShaper = null;
        shapeSize = null;
        shapeSizeByFixedSize = false;
    }

    /**
     * 拷贝属性，绝对的覆盖
     */
    public void copy(DisplayOptions options) {
        if (options == null) {
            return;
        }

        super.copy(options);

        cacheInMemoryDisabled = options.cacheInMemoryDisabled;
        imageDisplayer = options.imageDisplayer;
        resizeByFixedSize = options.resizeByFixedSize;
        loadingImage = options.loadingImage;
        errorImage = options.errorImage;
        pauseDownloadImage = options.pauseDownloadImage;
        imageShaper = options.imageShaper;
        shapeSize = options.shapeSize;
        shapeSizeByFixedSize = options.shapeSizeByFixedSize;
    }

    /**
     * 应用属性，应用的过程并不是绝对的覆盖
     */
    public void apply(DisplayOptions options) {
        if (options == null) {
            return;
        }

        super.apply(options);

        if (!cacheInMemoryDisabled) {
            cacheInMemoryDisabled = options.cacheInMemoryDisabled;
        }

        if (imageDisplayer == null) {
            imageDisplayer = options.imageDisplayer;
        }

        if (loadingImage == null) {
            loadingImage = options.loadingImage;
        }

        if (errorImage == null) {
            errorImage = options.errorImage;
        }

        if (pauseDownloadImage == null) {
            pauseDownloadImage = options.pauseDownloadImage;
        }

        if (!resizeByFixedSize) {
            resizeByFixedSize = options.resizeByFixedSize;
        }

        if (imageShaper == null) {
            imageShaper = options.imageShaper;
        }

        if (shapeSize == null) {
            shapeSize = options.shapeSize;
        }

        if(!shapeSizeByFixedSize){
            shapeSizeByFixedSize = options.shapeSizeByFixedSize;
        }
    }
}
