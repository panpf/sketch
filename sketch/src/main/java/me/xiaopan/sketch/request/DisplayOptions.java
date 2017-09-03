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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import me.xiaopan.sketch.SketchView;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.StateImage;

/**
 * 显示选项，适用于 {@link me.xiaopan.sketch.Sketch#display(String, SketchView)} 方法 和 {@link me.xiaopan.sketch.SketchImageView}
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
     * 绘制时修改图片的形状
     */
    private ImageShaper imageShaper;

    /**
     * 绘制时修改图片的尺寸
     */
    private ShapeSize shapeSize;

    /**
     * 使用 ImageView 的 layout_width 和 layout_height 作为 shape size
     */
    private boolean shapeSizeByFixedSize;

    public DisplayOptions() {
        reset();
    }

    public DisplayOptions(DisplayOptions from) {
        copy(from);
    }

    @NonNull
    @Override
    public DisplayOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (DisplayOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    @NonNull
    @Override
    public DisplayOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (DisplayOptions) super.setRequestLevel(requestLevel);
    }

    @NonNull
    @Override
    DisplayOptions setRequestLevelFrom(@Nullable RequestLevelFrom requestLevelFrom) {
        return (DisplayOptions) super.setRequestLevelFrom(requestLevelFrom);
    }

    @NonNull
    @Override
    public DisplayOptions setMaxSize(@Nullable MaxSize maxSize) {
        return (DisplayOptions) super.setMaxSize(maxSize);
    }

    @NonNull
    @Override
    public DisplayOptions setMaxSize(int width, int height) {
        return (DisplayOptions) super.setMaxSize(width, height);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(@Nullable Resize resize) {
        return (DisplayOptions) super.setResize(resize);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(int width, int height) {
        return (DisplayOptions) super.setResize(width, height);
    }

    @NonNull
    @Override
    public DisplayOptions setResize(int width, int height, @Nullable ImageView.ScaleType scaleType) {
        return (DisplayOptions) super.setResize(width, height, scaleType);
    }

    @NonNull
    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        return (DisplayOptions) super.setDecodeGifImage(decodeGifImage);
    }

    @NonNull
    @Override
    public DisplayOptions setLowQualityImage(boolean lowQualityImage) {
        return (DisplayOptions) super.setLowQualityImage(lowQualityImage);
    }

    @NonNull
    @Override
    public DisplayOptions setForceUseResize(boolean forceUseResize) {
        return (DisplayOptions) super.setForceUseResize(forceUseResize);
    }

    @NonNull
    @Override
    public DisplayOptions setImageProcessor(@Nullable ImageProcessor processor) {
        return (DisplayOptions) super.setImageProcessor(processor);
    }

    @NonNull
    @Override
    public DisplayOptions setBitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        return (DisplayOptions) super.setBitmapConfig(bitmapConfig);
    }

    @NonNull
    @Override
    public DisplayOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        return (DisplayOptions) super.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
    }

    @NonNull
    @Override
    public DisplayOptions setThumbnailMode(boolean thumbnailMode) {
        return (DisplayOptions) super.setThumbnailMode(thumbnailMode);
    }

    @NonNull
    @Override
    public DisplayOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        return (DisplayOptions) super.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
    }

    @NonNull
    @Override
    public DisplayOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        return (DisplayOptions) super.setBitmapPoolDisabled(bitmapPoolDisabled);
    }

    @NonNull
    @Override
    public DisplayOptions setCorrectImageOrientationDisabled(boolean correctImageOrientationDisabled) {
        return (DisplayOptions) super.setCorrectImageOrientationDisabled(correctImageOrientationDisabled);
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
    @NonNull
    public DisplayOptions setCacheInMemoryDisabled(boolean cacheInMemoryDisabled) {
        this.cacheInMemoryDisabled = cacheInMemoryDisabled;
        return this;
    }

    /**
     * 获取图片显示器
     *
     * @return ImageDisplayer
     */
    @Nullable
    public ImageDisplayer getImageDisplayer() {
        return imageDisplayer;
    }

    /**
     * 设置图片显示器
     *
     * @param displayer ImageDisplayer
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setImageDisplayer(@Nullable ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    /**
     * 获取加载中时显示的占位图片
     *
     * @return StateImage
     */
    @Nullable
    public StateImage getLoadingImage() {
        return loadingImage;
    }

    /**
     * 设置加载中时显示的占位图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setLoadingImage(@DrawableRes int drawableResId) {
        setLoadingImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 设置加载中时显示的占位图片
     *
     * @param loadingImage 加载中时显示的占位图片
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setLoadingImage(@Nullable StateImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    /**
     * 获取加载失败时显示的图片
     *
     * @return StateImage
     */
    @Nullable
    public StateImage getErrorImage() {
        return errorImage;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setErrorImage(@DrawableRes int drawableResId) {
        setErrorImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param errorImage 加载失败时显示的图片
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setErrorImage(@Nullable StateImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    /**
     * 获取暂停下载时显示的图片
     *
     * @return StateImage
     */
    @Nullable
    public StateImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param drawableResId 资源图片ID
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setPauseDownloadImage(@DrawableRes int drawableResId) {
        setPauseDownloadImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param pauseDownloadImage 暂停下载时显示的图片
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setPauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    /**
     * 获取绘制时图片形状修改器
     *
     * @return ImageShaper
     */
    @Nullable
    public ImageShaper getImageShaper() {
        return imageShaper;
    }

    /**
     * 设置绘制时图片形状修改器
     *
     * @param imageShaper 绘制时图片形状修改器
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setImageShaper(@Nullable ImageShaper imageShaper) {
        this.imageShaper = imageShaper;
        return this;
    }

    /**
     * 获取绘制时图片应该显示的尺寸
     *
     * @return ShapeSize
     */
    @Nullable
    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    /**
     * 设置绘制时图片应该显示的尺寸
     *
     * @param shapeSize 绘制时图片应该显示的尺寸
     * @return DisplayOptions
     */
    @NonNull
    public DisplayOptions setShapeSize(@Nullable ShapeSize shapeSize) {
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
    @NonNull
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
    public void copy(@Nullable DisplayOptions options) {
        if (options == null) {
            return;
        }

        //noinspection RedundantCast
        super.copy((LoadOptions) options);

        cacheInMemoryDisabled = options.cacheInMemoryDisabled;
        imageDisplayer = options.imageDisplayer;
        loadingImage = options.loadingImage;
        errorImage = options.errorImage;
        pauseDownloadImage = options.pauseDownloadImage;
        imageShaper = options.imageShaper;
        shapeSize = options.shapeSize;
        shapeSizeByFixedSize = options.shapeSizeByFixedSize;
    }
}
