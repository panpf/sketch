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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.cache.BitmapPool;
import me.panpf.sketch.decode.ImageSizeCalculator;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.decode.ProcessedResultCacheProcessor;
import me.panpf.sketch.decode.ThumbnailModeDecodeHelper;
import me.panpf.sketch.display.ImageDisplayer;
import me.panpf.sketch.display.TransitionImageDisplayer;
import me.panpf.sketch.drawable.SketchBitmapDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.drawable.SketchRefBitmap;
import me.panpf.sketch.drawable.SketchRefDrawable;
import me.panpf.sketch.drawable.SketchShapeBitmapDrawable;
import me.panpf.sketch.process.ImageProcessor;
import me.panpf.sketch.shaper.ImageShaper;
import me.panpf.sketch.state.StateImage;
import me.panpf.sketch.uri.UriModel;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.util.Stopwatch;

/**
 * 组织、收集、初始化显示参数，最后执行 {@link #commit()} 提交请求
 */
public class DisplayHelper {
    private static final String NAME = "DisplayHelper";

    private Sketch sketch;

    private String uri;
    private UriModel uriModel;
    private String key;
    private DisplayOptions displayOptions = new DisplayOptions();
    private DisplayListener displayListener;
    private DownloadProgressListener downloadProgressListener;

    private ViewInfo viewInfo = new ViewInfo();
    private SketchView sketchView;

    public DisplayHelper init(@NonNull Sketch sketch, @NonNull String uri, @NonNull SketchView sketchView) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriModel = UriModel.match(sketch, uri);
        this.sketchView = sketchView;

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().start(NAME + ". display use time");
        }

        // onDisplay 一定要在最前面执行，因为 在onDisplay 中会设置一些属性，这些属性会影响到后续一些 get 方法返回的结果
        this.sketchView.onReadyDisplay(uriModel);
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("onReadyDisplay");
        }

        viewInfo.reset(sketchView, sketch);
        displayOptions.copy(sketchView.getOptions());
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("init");
        }

        displayListener = sketchView.getDisplayListener();
        downloadProgressListener = sketchView.getDownloadProgressListener();

        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        sketch = null;

        uri = null;
        uriModel = null;
        key = null;
        displayOptions.reset();
        displayListener = null;
        downloadProgressListener = null;
        viewInfo.reset(null, null);
        sketchView = null;
    }

    /**
     * 设置请求 level，限制请求处理深度，参考 {@link RequestLevel}
     *
     * @param requestLevel {@link RequestLevel}
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            displayOptions.setRequestLevel(requestLevel);
        }
        return this;
    }

    /**
     * 禁用磁盘缓存
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInDisk() {
        displayOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 禁止从 {@link BitmapPool} 中寻找可复用的 {@link Bitmap}
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableBitmapPool() {
        displayOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * 解码 gif 图片并自动循环播放
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper decodeGifImage() {
        displayOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSimpleSize 缩小图片
     *
     * @param maxSize 最大尺寸
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper maxSize(@Nullable MaxSize maxSize) {
        displayOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * 设置最大尺寸，用于计算 inSimpleSize 缩小图片
     *
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper maxSize(int maxWidth, int maxHeight) {
        displayOptions.setMaxSize(maxWidth, maxHeight);
        return this;
    }

    /**
     * 调整图片的尺寸
     *
     * @param resize 新的尺寸
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    public DisplayHelper resize(@Nullable Resize resize) {
        displayOptions.setResize(resize);
        return this;
    }

    /**
     * 调整图片的尺寸
     *
     * @param reWidth  新的宽
     * @param reHeight 新的高
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    public DisplayHelper resize(int reWidth, int reHeight) {
        displayOptions.setResize(reWidth, reHeight);
        return this;
    }

    /**
     * 调整图片的尺寸
     *
     * @param reWidth   新的宽
     * @param reHeight  新的高
     * @param scaleType 指定如何生成新图片
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    public DisplayHelper resize(int reWidth, int reHeight, @NonNull ScaleType scaleType) {
        displayOptions.setResize(reWidth, reHeight, scaleType);
        return this;
    }

    /**
     * 在解码或创建 {@link Bitmap} 的时候尽量使用低质量的 {@link Bitmap.Config} ，优先级低于 {@link #bitmapConfig(Bitmap.Config)}，参考 {@link ImageType#getConfig(boolean)}
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    public DisplayHelper lowQualityImage() {
        displayOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，在图片读取到内存后对图片进行修改
     *
     * @param processor {@link ImageProcessor}
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper processor(@Nullable ImageProcessor processor) {
        displayOptions.setProcessor(processor);
        return this;
    }

    /**
     * 设置解码时使用的 {@link Bitmap.Config}，KITKAT 以上 {@link Bitmap.Config#ARGB_4444} 会被强制替换为 {@link Bitmap.Config#ARGB_8888}，优先级高于 {@link #lowQualityImage()}，对应 {@link android.graphics.BitmapFactory.Options#inPreferredConfig} 属性
     *
     * @param bitmapConfig {@link Bitmap.Config}
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper bitmapConfig(@Nullable Bitmap.Config bitmapConfig) {
        displayOptions.setBitmapConfig(bitmapConfig);
        return this;
    }

    /**
     * 设置解码时优先考虑速度还是质量，对应 {@link android.graphics.BitmapFactory.Options#inPreferQualityOverSpeed} 属性
     *
     * @param inPreferQualityOverSpeed true：质量优先；false：速度优先
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        displayOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式，配合 resize 可以得到更清晰的缩略图，参考 {@link ThumbnailModeDecodeHelper}
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper thumbnailMode() {
        displayOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过 {@link #processor(ImageProcessor)}、{@link #resize(Resize)} 或 {@link #thumbnailMode()}，下次就直接读取，参考 {@link ProcessedResultCacheProcessor}
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper cacheProcessedImageInDisk() {
        displayOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 禁止纠正图片方向
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCorrectImageOrientation() {
        displayOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * 禁用内存缓存
     *
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInMemory() {
        displayOptions.setCacheInMemoryDisabled(true);
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     *
     * @param displayer 图片显示器
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper displayer(@Nullable ImageDisplayer displayer) {
        displayOptions.setDisplayer(displayer);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param loadingImage 正在加载时显示的图片
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(@Nullable StateImage loadingImage) {
        displayOptions.setLoadingImage(loadingImage);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(@DrawableRes int drawableResId) {
        displayOptions.setLoadingImage(drawableResId);
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param errorImage 加载失败时显示的图片
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(@Nullable StateImage errorImage) {
        displayOptions.setErrorImage(errorImage);
        return this;
    }

    /**
     * 设置加载失败时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(@DrawableRes int drawableResId) {
        displayOptions.setErrorImage(drawableResId);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param pauseDownloadImage 暂停下载时显示的图片
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        displayOptions.setPauseDownloadImage(pauseDownloadImage);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param drawableResId drawable 资源 id
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(@DrawableRes int drawableResId) {
        displayOptions.setPauseDownloadImage(drawableResId);
        return this;
    }

    /**
     * 设置图片整形器，用于绘制时修改图片的形状
     *
     * @param imageShaper 图片整形器
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shaper(@Nullable ImageShaper imageShaper) {
        displayOptions.setShaper(imageShaper);
        return this;
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeSize 绘制时修改图片的尺寸
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(@Nullable ShapeSize shapeSize) {
        displayOptions.setShapeSize(shapeSize);
        return this;
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeWidth  绘制时修改图片的尺寸的宽
     * @param shapeHeight 绘制时修改图片的尺寸的高
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(int shapeWidth, int shapeHeight) {
        displayOptions.setShapeSize(shapeWidth, shapeHeight);
        return this;
    }

    /**
     * 设置在绘制时修改图片的尺寸
     *
     * @param shapeWidth  绘制时修改图片的尺寸的宽
     * @param shapeHeight 绘制时修改图片的尺寸的高
     * @param scaleType   指定在绘制时如果显示原图片
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(int shapeWidth, int shapeHeight, ScaleType scaleType) {
        displayOptions.setShapeSize(shapeWidth, shapeHeight, scaleType);
        return this;
    }


    /**
     * 批量设置显示参数（完全覆盖）
     *
     * @param newOptions {@link DisplayOptions}
     * @return {@link DisplayHelper}. 为了支持链式调用
     */
    @NonNull
    public DisplayHelper options(@Nullable DisplayOptions newOptions) {
        displayOptions.copy(newOptions);
        return this;
    }

    /**
     * 提交请求
     *
     * @return {@link DisplayRequest}
     */
    @Nullable
    public DisplayRequest commit() {
        if (!SketchUtils.isMainThread()) {
            SLog.w(NAME, "Please perform a commit in the UI thread. view(%s). %s",
                    Integer.toHexString(sketchView.hashCode()), uri);
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(uri);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        boolean checkResult = checkParams();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("checkParams");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(uri);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        saveParams();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("saveParams");
        }

        checkResult = checkMemoryCache();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("checkMemoryCache");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        checkResult = checkRequestLevel();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("checkRequestLevel");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        DisplayRequest potentialRequest = checkRepeatRequest();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("checkRepeatRequest");
        }
        if (potentialRequest != null) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return potentialRequest;
        }

        DisplayRequest request = submitRequest();

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().print(key);
        }
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    private boolean checkParams() {
        Configuration configuration = sketch.getConfiguration();
        ImageSizeCalculator imageSizeCalculator = sketch.getConfiguration().getSizeCalculator();
        FixedSize fixedSize = viewInfo.getFixedSize();


        // 用 ImageVie 的固定宽高作为 ShapeSize
        ShapeSize shapeSize = displayOptions.getShapeSize();
        if (shapeSize instanceof ShapeSize.ByViewFixedSizeShapeSize) {
            if (fixedSize != null) {
                shapeSize = new ShapeSize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType());
                displayOptions.setShapeSize(shapeSize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the ShapeSize.byViewFixedSize() function");
            }
        }

        // 检查 ShapeSize 的 ScaleType
        if (shapeSize != null && shapeSize.getScaleType() == null && sketchView != null) {
            shapeSize.setScaleType(viewInfo.getScaleType());
        }

        // 检查 ShapeSize 的宽高都必须大于 0
        if (shapeSize != null && (shapeSize.getWidth() == 0 || shapeSize.getHeight() == 0)) {
            throw new IllegalArgumentException("ShapeSize width and height must be > 0");
        }


        // 用 ImageVie 的固定宽高作为 Resize
        Resize resize = displayOptions.getResize();
        if (resize instanceof Resize.ByViewFixedSizeResize) {
            if (fixedSize != null) {
                resize = new Resize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType(), resize.getMode());
                displayOptions.setResize(resize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the Resize.byViewFixedSize() function");
            }
        }

        // 检查 Resize 的 ScaleType
        if (resize != null && resize.getScaleType() == null && sketchView != null) {
            resize.setScaleType(viewInfo.getScaleType());
        }

        // 检查 Resize 的宽高都必须大于 0
        if (resize != null && (resize.getWidth() <= 0 || resize.getHeight() <= 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // 没有设置 MaxSize 的话，如果 ImageView 的宽高是的固定的就根据 ImageView 的宽高来作为 MaxSize，否则就用默认的 MaxSize
        MaxSize maxSize = displayOptions.getMaxSize();
        if (maxSize == null) {
            maxSize = imageSizeCalculator.calculateImageMaxSize(sketchView);
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.getContext());
            }
            displayOptions.setMaxSize(maxSize);
        }

        // MaxSize 的宽或高大于 0 即可
        if (maxSize != null && maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // 没有 ImageProcessor 但有 Resize 的话就需要设置一个默认的图片裁剪处理器
        if (displayOptions.getProcessor() == null && resize != null) {
            displayOptions.setProcessor(configuration.getResizeProcessor());
        }


        // ImageDisplayer 必须得有
        if (displayOptions.getDisplayer() == null) {
            displayOptions.setDisplayer(configuration.getDefaultDisplayer());
        }

        // 使用过渡图片显示器的时候，如果使用了 loadingImage 的话就必须配合 ShapeSize 才行，如果没有 ShapeSize 就取 ImageView 的宽高作为 ShapeSize
        if (displayOptions.getDisplayer() instanceof TransitionImageDisplayer
                && displayOptions.getLoadingImage() != null && displayOptions.getShapeSize() == null) {
            if (fixedSize != null) {
                displayOptions.setShapeSize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                ViewGroup.LayoutParams layoutParams = sketchView.getLayoutParams();
                String widthName = SketchUtils.viewLayoutFormatted(layoutParams != null ? layoutParams.width : -1);
                String heightName = SketchUtils.viewLayoutFormatted(layoutParams != null ? layoutParams.height : -1);
                String errorInfo = String.format("If you use TransitionImageDisplayer and loadingImage, " +
                        "You must be setup ShapeSize or imageView width and height must be fixed. width=%s, height=%s", widthName, heightName);
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "%s. view(%s). %s", errorInfo, Integer.toHexString(sketchView.hashCode()), uri);
                }
                throw new IllegalArgumentException(errorInfo);
            }
        }

        configuration.getOptionsFilterManager().filter(displayOptions);

        if (TextUtils.isEmpty(uri)) {
            SLog.e(NAME, "Uri is empty. view(%s)", Integer.toHexString(sketchView.hashCode()));

            Drawable drawable = null;
            if (displayOptions.getErrorImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getErrorImage().getDrawable(context, sketchView, displayOptions);
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.setImageDrawable(drawable);

            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_INVALID, false);
            return false;
        }

        if (uriModel == null) {
            SLog.e(NAME, "Not support uri. %s. view(%s)", uri, Integer.toHexString(sketchView.hashCode()));

            Drawable drawable = null;
            if (displayOptions.getErrorImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getErrorImage().getDrawable(context, sketchView, displayOptions);
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.setImageDrawable(drawable);

            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NO_SUPPORT, false);
            return false;
        }


        // 根据 URI 和显示选项生成请求 key
        key = SketchUtils.makeRequestKey(uri, uriModel, displayOptions.makeKey());

        return true;
    }

    /**
     * 将 最终的 {@link DisplayOptions} 保存在 {@link SketchView} 中，以便在 RecyclerView 中恢复显示时使用
     */
    private void saveParams() {
        DisplayCache displayCache = sketchView.getDisplayCache();
        if (displayCache == null) {
            displayCache = new DisplayCache();
            sketchView.setDisplayCache(displayCache);
        }

        displayCache.uri = uri;
        displayCache.options.copy(displayOptions);
    }

    private boolean checkMemoryCache() {
        if (displayOptions.isCacheInMemoryDisabled()) {
            return true;
        }

        String memoryCacheKey = key;
        SketchRefBitmap cachedRefBitmap = sketch.getConfiguration().getMemoryCache().get(memoryCacheKey);
        if (cachedRefBitmap == null) {
            return true;
        }

        if (cachedRefBitmap.isRecycled()) {
            sketch.getConfiguration().getMemoryCache().remove(memoryCacheKey);
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.w(NAME, "Memory cache drawable recycled. %s. view(%s)", cachedRefBitmap.getInfo(), viewCode);
            return true;
        }

        // 立马标记等待使用，防止被回收
        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", NAME), true);

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.d(NAME, "Display image completed. %s. %s. view(%s)", ImageFrom.MEMORY_CACHE.name(), cachedRefBitmap.getInfo(), viewCode);
        }

        SketchBitmapDrawable refBitmapDrawable = new SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE);

        Drawable finalDrawable;
        if (displayOptions.getShapeSize() != null || displayOptions.getShaper() != null) {
            finalDrawable = new SketchShapeBitmapDrawable(sketch.getConfiguration().getContext(), refBitmapDrawable,
                    displayOptions.getShapeSize(), displayOptions.getShaper());
        } else {
            finalDrawable = refBitmapDrawable;
        }

        ImageDisplayer imageDisplayer = displayOptions.getDisplayer();
        if (imageDisplayer != null && imageDisplayer.isAlwaysUse()) {
            imageDisplayer.display(sketchView, finalDrawable);
        } else {
            sketchView.setImageDrawable(finalDrawable);
        }
        if (displayListener != null) {
            displayListener.onCompleted(finalDrawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.getAttrs());
        }

        ((SketchRefDrawable) finalDrawable).setIsWaitingUse(String.format("%s:waitingUse:finish", NAME), false);
        return false;
    }

    private boolean checkRequestLevel() {
        // 如果已经暂停加载的话就不再从本地或网络加载了
        if (displayOptions.getRequestLevel() == RequestLevel.MEMORY) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Request cancel. %s. view(%s). %s", CancelCause.PAUSE_LOAD, Integer.toHexString(sketchView.hashCode()), key);
            }

            Drawable loadingDrawable = null;
            if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                loadingDrawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.clearAnimation();
            sketchView.setImageDrawable(loadingDrawable);

            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_LOAD, false);
            return false;
        }

        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(NAME, "Request cancel. %s. view(%s). %s", CancelCause.PAUSE_DOWNLOAD, Integer.toHexString(sketchView.hashCode()), key);
            }

            // 显示暂停下载图片
            Drawable drawable = null;
            if (displayOptions.getPauseDownloadImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getPauseDownloadImage().getDrawable(context, sketchView, displayOptions);
                sketchView.clearAnimation();
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.setImageDrawable(drawable);

            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_DOWNLOAD, false);
            return false;
        }

        return true;
    }

    /**
     * 试图取消已经存在的请求
     *
     * @return {@link DisplayRequest} null：已经取消或没有已存在的请求
     */
    private DisplayRequest checkRepeatRequest() {
        DisplayRequest potentialRequest = SketchUtils.findDisplayRequest(sketchView);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (key.equals(potentialRequest.getKey())) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "Repeat request. key=%s. view(%s)", key, Integer.toHexString(sketchView.hashCode()));
                }
                return potentialRequest;
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "Cancel old request. newKey=%s. oldKey=%s. view(%s)",
                            key, potentialRequest.getKey(), Integer.toHexString(sketchView.hashCode()));
                }
                potentialRequest.cancel(CancelCause.BE_REPLACED_ON_HELPER);
            }
        }

        return null;
    }

    private DisplayRequest submitRequest() {
        CallbackHandler.postCallbackStarted(displayListener, false);
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("callbackStarted");
        }

        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        RequestAndViewBinder requestAndViewBinder = new RequestAndViewBinder(sketchView);
        DisplayRequest request = requestFactory.newDisplayRequest(sketch, uri, uriModel, key, displayOptions, viewInfo,
                requestAndViewBinder, displayListener, downloadProgressListener);
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("createRequest");
        }

        SketchLoadingDrawable loadingDrawable;
        StateImage loadingImage = displayOptions.getLoadingImage();
        if (loadingImage != null) {
            Context context = sketch.getConfiguration().getContext();
            Drawable drawable = loadingImage.getDrawable(context, sketchView, displayOptions);
            loadingDrawable = new SketchLoadingDrawable(drawable, request);
        } else {
            loadingDrawable = new SketchLoadingDrawable(null, request);
        }
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("createLoadingImage");
        }

        sketchView.setImageDrawable(loadingDrawable);
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("setLoadingImage");
        }

        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
            SLog.d(NAME, "Run dispatch submitted. view(%s). %s", Integer.toHexString(sketchView.hashCode()), key);
        }

        request.submit();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("submitRequest");
        }

        return request;
    }
}