/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.display.ImageDisplayer;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.state.DrawableStateImage;
import me.panpf.sketch.state.StateImage;

/**
 * 显示选项，适用于 {@link Sketch#display(String, SketchView)} 方法 和 {@link SketchImageView}
 */
public class DisplayOptions extends LoadOptions {
    /**
     * 禁用内存缓存
     */
    private boolean cacheInMemoryDisabled;

    /**
     * 图片显示器，用来在加载完成后显示图片
     */
    private ImageDisplayer displayer;

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
     * 图片整形器，用于绘制时修改图片的形状
     */
    private ImageShaper shaper;

    /**
     * 绘制时修改图片的尺寸
     */
    private ShapeSize shapeSize;

    public DisplayOptions() {
        reset();
    }

    public DisplayOptions(DisplayOptions from) {
        copy(from);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        return (DisplayOptions) super.setCacheInDiskDisabled(cacheInDiskDisabled);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        return (DisplayOptions) super.setRequestLevel(requestLevel);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setMaxSize(@Nullable MaxSize maxSize) {
        return (DisplayOptions) super.setMaxSize(maxSize);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setMaxSize(int maxWidth, int maxHeight) {
        return (DisplayOptions) super.setMaxSize(maxWidth, maxHeight);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setResize(@Nullable Resize resize) {
        return (DisplayOptions) super.setResize(resize);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setResize(int reWidth, int reHeight) {
        return (DisplayOptions) super.setResize(reWidth, reHeight);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setResize(int reWidth, int reHeight, @Nullable ImageView.ScaleType scaleType) {
        return (DisplayOptions) super.setResize(reWidth, reHeight, scaleType);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setDecodeGifImage(boolean decodeGifImage) {
        return (DisplayOptions) super.setDecodeGifImage(decodeGifImage);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setLowQualityImage(boolean lowQualityImage) {
        return (DisplayOptions) super.setLowQualityImage(lowQualityImage);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setProcessor(@Nullable ImageProcessor processor) {
        return (DisplayOptions) super.setProcessor(processor);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setBitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        return (DisplayOptions) super.setBitmapConfig(bitmapConfig);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setInPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        return (DisplayOptions) super.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setThumbnailMode(boolean thumbnailMode) {
        return (DisplayOptions) super.setThumbnailMode(thumbnailMode);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setCacheProcessedImageInDisk(boolean cacheProcessedImageInDisk) {
        return (DisplayOptions) super.setCacheProcessedImageInDisk(cacheProcessedImageInDisk);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    @Override
    public DisplayOptions setBitmapPoolDisabled(boolean bitmapPoolDisabled) {
        return (DisplayOptions) super.setBitmapPoolDisabled(bitmapPoolDisabled);
    }

    /**
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
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
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public DisplayOptions setCacheInMemoryDisabled(@SuppressWarnings("SameParameterValue") boolean cacheInMemoryDisabled) {
        this.cacheInMemoryDisabled = cacheInMemoryDisabled;
        return this;
    }

    /**
     * 获取图片显示器
     *
     * @return {@link ImageDisplayer}
     */
    @Nullable
    public ImageDisplayer getDisplayer() {
        return displayer;
    }

    /**
     * 设置图片显示器
     *
     * @param displayer 图片显示器
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setDisplayer(@Nullable ImageDisplayer displayer) {
        this.displayer = displayer;
        return this;
    }

    /**
     * 获取正在加载时显示的图片
     *
     * @return {@link StateImage}
     */
    @Nullable
    public StateImage getLoadingImage() {
        return loadingImage;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param loadingImage 正在加载时显示的图片
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setLoadingImage(@Nullable StateImage loadingImage) {
        this.loadingImage = loadingImage;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setLoadingImage(@DrawableRes int drawableResId) {
        setLoadingImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 获取加载失败时显示的图片
     *
     * @return {@link StateImage}
     */
    @Nullable
    public StateImage getErrorImage() {
        return errorImage;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param errorImage 加载失败时显示的图片
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public DisplayOptions setErrorImage(@Nullable StateImage errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setErrorImage(@DrawableRes int drawableResId) {
        setErrorImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 获取暂停下载时显示的图片
     *
     * @return {@link StateImage}
     */
    @Nullable
    public StateImage getPauseDownloadImage() {
        return pauseDownloadImage;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param pauseDownloadImage 暂停下载时显示的图片
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public DisplayOptions setPauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        this.pauseDownloadImage = pauseDownloadImage;
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setPauseDownloadImage(@DrawableRes int drawableResId) {
        setPauseDownloadImage(new DrawableStateImage(drawableResId));
        return this;
    }

    /**
     * 设置图片整形器，用于绘制时修改图片的形状
     *
     * @return {@link ImageShaper}
     */
    @Nullable
    public ImageShaper getShaper() {
        return shaper;
    }

    /**
     * 设置图片整形器，用于绘制时修改图片的形状
     *
     * @param shaper 图片整形器
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setShaper(@Nullable ImageShaper shaper) {
        this.shaper = shaper;
        return this;
    }

    /**
     * 获取绘制时图片应该显示的尺寸
     *
     * @return {@link ShapeSize}
     */
    @Nullable
    public ShapeSize getShapeSize() {
        return shapeSize;
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeSize 绘制时修改图片的尺寸
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @NonNull
    public DisplayOptions setShapeSize(@Nullable ShapeSize shapeSize) {
        this.shapeSize = shapeSize;
        return this;
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeWidth  绘制时应该显示的宽
     * @param shapeHeight 绘制时应该显示的高
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public DisplayOptions setShapeSize(int shapeWidth, int shapeHeight) {
        return setShapeSize(new ShapeSize(shapeWidth, shapeHeight));
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeWidth  绘制时修改图片的尺寸的宽
     * @param shapeHeight 绘制时修改图片的尺寸的高
     * @param scaleType   指定在绘制时如果显示原图片
     * @return {@link DisplayOptions}. 为了支持链式调用
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public DisplayOptions setShapeSize(int shapeWidth, int shapeHeight, @Nullable ImageView.ScaleType scaleType) {
        return setShapeSize(new ShapeSize(shapeWidth, shapeHeight, scaleType));
    }

    @Override
    public void reset() {
        super.reset();
        cacheInMemoryDisabled = false;
        displayer = null;
        loadingImage = null;
        errorImage = null;
        pauseDownloadImage = null;
        shaper = null;
        shapeSize = null;
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
        displayer = options.displayer;
        loadingImage = options.loadingImage;
        errorImage = options.errorImage;
        pauseDownloadImage = options.pauseDownloadImage;
        shaper = options.shaper;
        shapeSize = options.shapeSize;
    }
}
