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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SketchView;
import me.xiaopan.sketch.decode.ImageSizeCalculator;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.SketchBitmapDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.drawable.SketchRefBitmap;
import me.xiaopan.sketch.drawable.SketchRefDrawable;
import me.xiaopan.sketch.drawable.SketchShapeBitmapDrawable;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.state.StateImage;
import me.xiaopan.sketch.uri.UriModel;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.Stopwatch;

/**
 * 显示 Helper，负责组织、收集、初始化显示参数，最后执行 commit() 提交请求
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
            Stopwatch.with().record("onDisplay");
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
     * 禁用磁盘缓存
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInDisk() {
        displayOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 禁用 BitmapPool
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableBitmapPool() {
        displayOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * 设置请求 Level
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper requestLevel(@Nullable RequestLevel requestLevel) {
        if (requestLevel != null) {
            displayOptions.setRequestLevel(requestLevel);
            displayOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 解码 Gif 图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper decodeGifImage() {
        displayOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此 Size 来计算 inSimpleSize
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper maxSize(@Nullable MaxSize maxSize) {
        displayOptions.setMaxSize(maxSize);
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此 Size 来计算 inSimpleSize
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper maxSize(int width, int height) {
        displayOptions.setMaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public DisplayHelper resize(@Nullable Resize resize) {
        displayOptions.setResize(resize);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public DisplayHelper resize(int width, int height) {
        displayOptions.setResize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据 resize 进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟 resize 一样的，
     * 但尺寸不一定会等于 resize，也有可能小于 resize，如果需要必须同 resize 一致可以设置 forceUseResize
     */
    @NonNull
    public DisplayHelper resize(int width, int height, @NonNull ScaleType scaleType) {
        displayOptions.setResize(width, height, scaleType);
        return this;
    }

    /**
     * 强制使经过 resize 处理后的图片同 resize 的尺寸一致
     */
    @NonNull
    public DisplayHelper forceUseResize() {
        displayOptions.setForceUseResize(true);
        return this;
    }

    /**
     * 返回低质量的图片
     */
    @NonNull
    public DisplayHelper lowQualityImage() {
        displayOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据 resize 和 ScaleType 创建一张新的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper processor(@Nullable ImageProcessor processor) {
        displayOptions.setImageProcessor(processor);
        return this;
    }

    /**
     * 设置图片质量
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper bitmapConfig(@Nullable Bitmap.Config config) {
        displayOptions.setBitmapConfig(config);
        return this;
    }

    /**
     * 设置优先考虑质量还是速度
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        displayOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper thumbnailMode() {
        displayOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过 ImageProcessor、resize 或 thumbnailMode 处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper cacheProcessedImageInDisk() {
        displayOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 禁用纠正图片方向功能
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCorrectImageOrientation() {
        displayOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * 禁用内存缓存
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInMemory() {
        displayOptions.setCacheInMemoryDisabled(true);
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper displayer(@Nullable ImageDisplayer displayer) {
        displayOptions.setImageDisplayer(displayer);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(@Nullable StateImage loadingImage) {
        displayOptions.setLoadingImage(loadingImage);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(@DrawableRes int drawableResId) {
        displayOptions.setLoadingImage(drawableResId);
        return this;
    }

    /**
     * 设置错误时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(@Nullable StateImage errorImage) {
        displayOptions.setErrorImage(errorImage);
        return this;
    }

    /**
     * 设置错误时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(@DrawableRes int drawableResId) {
        displayOptions.setErrorImage(drawableResId);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(@Nullable StateImage pauseDownloadImage) {
        displayOptions.setPauseDownloadImage(pauseDownloadImage);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(@DrawableRes int drawableResId) {
        displayOptions.setPauseDownloadImage(drawableResId);
        return this;
    }

    /**
     * 设置图片整型器，用于绘制时修改图片的形状
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shaper(@Nullable ImageShaper imageShaper) {
        displayOptions.setImageShaper(imageShaper);
        return this;
    }

    /**
     * 设置图片尺寸，用于绘制时修改图片的尺寸
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(@Nullable ShapeSize shapeSize) {
        displayOptions.setShapeSize(shapeSize);
        return this;
    }

    /**
     * 设置图片尺寸，用于绘制时修改图片的尺寸
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(int width, int height) {
        displayOptions.setShapeSize(width, height);
        return this;
    }

    /**
     * 设置图片尺寸，用于绘制时修改图片的尺寸
     */
    @NonNull
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(int width, int height, ScaleType scaleType) {
        displayOptions.setShapeSize(width, height, scaleType);
        return this;
    }


    /**
     * 批量设置显示参数（完全覆盖）
     */
    @NonNull
    public DisplayHelper options(@Nullable DisplayOptions newOptions) {
        displayOptions.copy(newOptions);
        return this;
    }

    /**
     * 提交请求
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

        boolean checkResult = checkParam();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("checkParam");
        }
        if (!checkResult) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
                Stopwatch.with().print(uri);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        preProcess();
        if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_TIME)) {
            Stopwatch.with().record("preProcess");
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

    private boolean checkParam() {
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

        return true;
    }

    /**
     * 对相关参数进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();
        ImageSizeCalculator imageSizeCalculator = sketch.getConfiguration().getSizeCalculator();
        FixedSize fixedSize = viewInfo.getFixedSize();


        // 用 ImageVie 的固定宽高作为 ShapeSize
        ShapeSize shapeSize = displayOptions.getShapeSize();
        if (shapeSize != null && shapeSize instanceof ShapeSize.ByViewFixedSizeShapeSize) {
            if (fixedSize != null) {
                shapeSize = new ShapeSize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType());
                displayOptions.setShapeSize(shapeSize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the ShapeSize.byViewFixedSize() function");
            }
        }

        // 如果没有设置 ScaleType 的话就从 ImageView 身上取
        if (shapeSize != null && shapeSize.getScaleType() == null && sketchView != null) {
            shapeSize.setScaleType(viewInfo.getScaleType());
        }

        // 检查 Resize 的宽高都必须大于 0
        if (shapeSize != null && (shapeSize.getWidth() == 0 || shapeSize.getHeight() == 0)) {
            throw new IllegalArgumentException("ShapeSize width and height must be > 0");
        }


        // 用 ImageVie 的固定宽高作为 Resize
        Resize resize = displayOptions.getResize();
        if (resize != null && resize instanceof Resize.ByViewFixedSizeResize) {
            if (fixedSize != null) {
                resize = new Resize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType());
                displayOptions.setResize(resize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the Resize.byViewFixedSize() function");
            }
        }

        // 如果没有设置 ScaleType 的话就从 ImageView 身上取
        if (resize != null && resize.getScaleType() == null && sketchView != null) {
            resize.setScaleType(viewInfo.getScaleType());
        }

        // 检查 Resize 的宽高都必须大于 0
        if (resize != null && (resize.getWidth() == 0 || resize.getHeight() == 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // 没有设置 maxSize 的话，如果 ImageView 的宽高是的固定的就根据 ImageView 的宽高来作为 maxSize，否则就用默认的 maxSize
        if (displayOptions.getMaxSize() == null) {
            MaxSize maxSize = imageSizeCalculator.calculateImageMaxSize(sketchView);
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.getContext());
            }
            displayOptions.setMaxSize(maxSize);
        }

        // 检查 MaxSize 的宽或高大于 0 即可
        MaxSize maxSize = displayOptions.getMaxSize();
        if (maxSize != null && maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // 没有 ImageProcessor 但有 resize 的话就需要设置一个默认的图片裁剪处理器
        if (displayOptions.getImageProcessor() == null && resize != null) {
            displayOptions.setImageProcessor(configuration.getResizeProcessor());
        }


        // 如果设置了全局使用低质量图片的话就强制使用低质量的图片
        if (configuration.isGlobalLowQualityImage()) {
            displayOptions.setLowQualityImage(true);
        }

        // 如果设置了全局解码质量优先
        if (configuration.isGlobalInPreferQualityOverSpeed()) {
            displayOptions.setInPreferQualityOverSpeed(true);
        }

        // 如果没有设置请求 Level 的话就跟据暂停下载和暂停加载功能来设置请求 Level
        if (displayOptions.getRequestLevel() == null) {
            if (configuration.isGlobalPauseDownload()) {
                displayOptions.setRequestLevel(RequestLevel.LOCAL);
                displayOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_DOWNLOAD);
            }

            if (configuration.isGlobalPauseLoad()) {
                displayOptions.setRequestLevel(RequestLevel.MEMORY);
                displayOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_LOAD);
            }
        }

        // ImageDisplayer 必须得有
        if (displayOptions.getImageDisplayer() == null) {
            displayOptions.setImageDisplayer(configuration.getDefaultDisplayer());
        }

        // 使用过渡图片显示器的时候，如果使用了 loadingImage 的话就必须配合 ShapeSize 才行，如果没有 ShapeSize 就取 ImageView 的宽高作为 ShapeSize
        if (displayOptions.getImageDisplayer() instanceof TransitionImageDisplayer
                && displayOptions.getLoadingImage() != null && displayOptions.getShapeSize() == null) {
            if (fixedSize != null) {
                displayOptions.setShapeSize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                ViewGroup.LayoutParams layoutParams = sketchView.getLayoutParams();
                String errorInfo = SketchUtils.concat(
                        "If you use TransitionImageDisplayer and loadingImage, " +
                                "You must be setup ShapeSize or imageView width and height must be fixed",
                        ". width=", SketchUtils.viewLayoutFormatted(layoutParams.width),
                        ", height=", SketchUtils.viewLayoutFormatted(layoutParams.height));
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                    SLog.d(NAME, "%s. view(%s). %s",
                            errorInfo, Integer.toHexString(sketchView.hashCode()), uri);
                }
                throw new IllegalArgumentException(errorInfo);
            }
        }

        // 根据 URI 和显示选项生成请求 key
        key = SketchUtils.makeRequestKey(uri, uriModel, displayOptions);
    }

    /**
     * 将相关信息保存在SketchImageView中，以便在RecyclerView中恢复显示使用
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
        if (displayOptions.getShapeSize() != null || displayOptions.getImageShaper() != null) {
            finalDrawable = new SketchShapeBitmapDrawable(sketch.getConfiguration().getContext(), refBitmapDrawable,
                    displayOptions.getShapeSize(), displayOptions.getImageShaper());
        } else {
            finalDrawable = refBitmapDrawable;
        }

        ImageDisplayer imageDisplayer = displayOptions.getImageDisplayer();
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
            boolean isPauseLoad = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD;

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                CancelCause cause = isPauseLoad ? CancelCause.PAUSE_LOAD : CancelCause.REQUEST_LEVEL_IS_MEMORY;
                SLog.d(NAME, "Request cancel. %s. view(%s). %s", cause, Integer.toHexString(sketchView.hashCode()), key);
            }

            Drawable loadingDrawable = null;
            if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                loadingDrawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.clearAnimation();
            sketchView.setImageDrawable(loadingDrawable);

            CancelCause cancelCause = isPauseLoad ? CancelCause.PAUSE_LOAD : CancelCause.REQUEST_LEVEL_IS_MEMORY;
            CallbackHandler.postCallbackCanceled(displayListener, cancelCause, false);
            return false;
        }

        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL && uriModel.isFromNet()
                && !sketch.getConfiguration().getDiskCache().exist(uriModel.getDiskCacheKey(uri))) {
            boolean isPauseDownload = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                CancelCause cause = isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL;
                SLog.d(NAME, "Request cancel. %s. view(%s). %s", cause, Integer.toHexString(sketchView.hashCode()), key);
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

            CancelCause cancelCause = isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.REQUEST_LEVEL_IS_LOCAL;
            CallbackHandler.postCallbackCanceled(displayListener, cancelCause, false);
            return false;
        }

        return true;
    }

    /**
     * 试图取消已经存在的请求
     *
     * @return DisplayRequest null：已经取消或没有已存在的请求
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