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
 * 显示选项，适用于 {@link me.xiaopan.sketch.Sketch#display(String, ImageViewInterface)} 方法 和 {@link me.xiaopan.sketch.SketchImageView}
 */
public class DisplayOptions extends LoadOptions {
    /**
     * 禁用内存缓存
     */
    private boolean cacheInMemoryDisabled;

    /**
     * 图片显示器，用来在加载完成后显示图片
     */
    private ImageDisplayer imageDisplayer;

    /**
     * 正在加载时显示的图片
     */
    private StateImage loadingImage;

    /**
     * 加载失败时显示的图片
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
        return (DisplayOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @Override
    public DisplayOptions setRequestLevel(RequestLevel requestLevel) {
        return (DisplayOptions) super.setRequestLevel(requestLevel);
    }

    @Override
    DisplayOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        return (DisplayOptions) super.setRequestLevelFrom(requestLevelFrom);
    }

    @Override
    public DisplayOptions setMaxSize(MaxSize maxSize) {
        return (DisplayOptions) super.setMaxSize(maxSize);
    }

    @Override
    public DisplayOptions setMaxSize(int width, int height) {
        return (DisplayOptions) super.setMaxSize(width, height);
    }

    @Override
    public DisplayOptions setResize(Resize resize) {
        return (DisplayOptions) super.setResize(resize);
    }

    @Override
    public DisplayOptions setResize(int width, int height) {
        return (DisplayOptions) super.setResize(width, height);
    }

    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        return (DisplayOptions) super.setDecodeGifImage(decodeGifImage);
    }

    @Override
    public DisplayOptions setLowQualityImage(boolean lowQualityImage) {
        return (DisplayOptions) super.setLowQualityImage(lowQualityImage);
    }

    @Override
    public DisplayOptions setForceUseResize(boolean forceUseResize) {
        return (DisplayOptions) super.setForceUseResize(forceUseResize);
    }

    @Override
    public DisplayOptions setImageProcessor(ImageProcessor processor) {
        return (DisplayOptions) super.setImageProcessor(processor);
    }

    @Override
    public DisplayOptions setBitmapConfig(Bitmap.Config bitmapConfig) {
        return (DisplayOptions) super.setBitmapConfig(bitmapConfig);
    }

    @Override
    public DisplayOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        return (DisplayOptions) super.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
    }

    @Override
    public DisplayOptions setThumbnailMode(boolean thumbnailMode) {
        return (DisplayOptions) super.setThumbnailMode(thumbnailMode);
    }

    @Override
    public DisplayOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        return (DisplayOptions) super.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
    }

    @Override
    public DisplayOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        return (DisplayOptions) super.setBitmapPoolDisabled(bitmapPoolDisabled);
    }

    @Override
    public DisplayOptions setCorrectImageOrientation(boolean correctImageOrientation) {
        return (DisplayOptions) super.setCorrectImageOrientation(correctImageOrientation);
    }

    /**
     * 禁止使用内存内缓存
     */
    public boolean isCacheInMemoryDisabled() {
        return cacheInMemoryDisabled;
    }

    /**
     * 设置禁止使用内存缓存
     *
     * @param cacheInMemoryDisabled 禁止使用内存缓存
     * @return DisplayOptions
     */
    public DisplayOptions setCacheInMemoryDisabled(boolean cacheInMemoryDisabled) {
        this.cacheInMemoryDisabled = cacheInMemoryDisabled;
        return this;
    }

    /**
     * 获取图片显示器
     *
     * @return ImageDisplayer
     */
    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    /**
     * 设置图片显示器
     *
     * @param displayer ImageDisplayer
     * @return DisplayOptions
     */
    public DisplayOptions setImageDisplayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    /**
     * 获取加载中时显示的占位图片
     *
     * @return StateImage
     */
    public StateImage getLoadingImage() {
        return loadingImage;
    }

    /**
     * 设置加载中时显示的占位图片
     *
     * @param loadingImage 加载中时显示的占位图片
     * @return DisplayOptions
     */
    public DisplayOptions setLoadingImage(StateImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    /**
     * 设置加载中时显示的占位图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setLoadingImage(int drawableResId) {
        setLoadingImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 获取加载失败时显示的图片
     *
     * @return StateImage
     */
    public StateImage getErrorImage() {
        return errorImage;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param errorImage 加载失败时显示的图片
     * @return DisplayOptions
     */
    public DisplayOptions setErrorImage(StateImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setErrorImage(int drawableResId) {
        setErrorImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 获取暂停下载时显示的图片
     *
     * @return StateImage
     */
    public StateImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param pauseDownloadImage 暂停下载时显示的图片
     * @return DisplayOptions
     */
    public DisplayOptions setPauseDownloadImage(StateImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    public DisplayOptions setPauseDownloadImage(int drawableResId) {
        setPauseDownloadImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 没有设置resize时使用fixed size作为resize
     *
     * @see FixedSize
     */
    public boolean isResizeByFixedSize() {
        return resizeByFixedSize;
    }

    /**
     * 设置没有设置resize时使用fixed size作为resize
     *
     * @param isResizeByFixedSize 没有设置resize时，使用fixed size作为resize
     * @return DisplayOptions
     * @see FixedSize
     */
    public DisplayOptions setResizeByFixedSize(boolean isResizeByFixedSize) {
        this.resizeByFixedSize = isResizeByFixedSize;
        return this;
    }

    /**
     * 获取绘制时图片形状修改器
     *
     * @return ImageShaper
     */
    public ImageShaper getImageShaper() {
        return imageShaper;
    }

    /**
     * 设置绘制时图片形状修改器
     *
     * @param imageShaper 绘制时图片形状修改器
     * @return DisplayOptions
     */
    public DisplayOptions setImageShaper(ImageShaper imageShaper) {
        this.imageShaper = imageShaper;
        return this;
    }

    /**
     * 获取绘制时图片应该显示的尺寸
     *
     * @return ShapeSize
     */
    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    /**
     * 设置绘制时图片应该显示的尺寸
     *
     * @param shapeSize 绘制时图片应该显示的尺寸
     * @return DisplayOptions
     */
    public DisplayOptions setShapeSize(ShapeSize shapeSize) {
        this.shapeSize = shapeSize;
        this.shapeSizeByFixedSize = false;
        return this;
    }

    /**
     * 设置绘制时图片应该显示的尺寸
     *
     * @param width  绘制时应该显示的宽
     * @param height 绘制时应该显示的高
     * @return DisplayOptions
     */
    public DisplayOptions setShapeSize(int width, int height) {
        return setShapeSize(new ShapeSize(width, height));
    }

    /**
     * 没有设置shape size时使用fixed size作为shape size
     */
    public boolean isShapeSizeByFixedSize() {
        return shapeSizeByFixedSize;
    }

    /**
     * 设置没有设置shape size时使用fixed size作为shape size
     *
     * @param shapeSizeByFixedSize 没有设置shape size时使用fixed size作为shape size
     * @return DisplayOptions
     */
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
     *
     * @param options 来源
     */
    public void copy(DisplayOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.copy((LoadOptions) options);

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
     * 合并指定的DisplayOptions，合并的过程并不是绝对的覆盖，专门为{@link DisplayHelper#options(DisplayOptions)}方法提供
     * <br>简单来说自己已经设置了的属性不会被覆盖，对于都设置了但可以比较大小的，较小的优先
     */
    public void merge(DisplayOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.merge((LoadOptions) options);

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

        if (!shapeSizeByFixedSize) {
            shapeSizeByFixedSize = options.shapeSizeByFixedSize;
        }
    }
}
