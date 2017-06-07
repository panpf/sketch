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
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
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
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.Stopwatch;

/**
 * 显示Helper，负责组织、收集、初始化显示参数，最后执行commit()提交请求
 */
public class DisplayHelper {
    private static final String LOG_NAME = "DisplayHelper";

    private Sketch sketch;

    private UriInfo uriInfo;
    private String key;
    private DisplayOptions displayOptions = new DisplayOptions();
    private DisplayListener displayListener;
    private DownloadProgressListener downloadProgressListener;

    private ViewInfo viewInfo = new ViewInfo();
    private SketchView sketchView;

    public DisplayHelper init(Sketch sketch, String uri, SketchView sketchView) {
        this.sketch = sketch;
        this.uriInfo = UriInfo.make(uri);
        this.sketchView = sketchView;

        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().start(LOG_NAME + ". display use time");
        }

        // onDisplay一定要在最前面执行，因为在onDisplay中会设置一些属性，这些属性会影响到后续一些get方法返回的结果
        this.sketchView.onReadyDisplay(uriInfo != null ? uriInfo.getScheme() : null);
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("onDisplay");
        }

        viewInfo.reset(sketchView, sketch);
        displayOptions.copy(sketchView.getOptions());
        if (SLogType.TIME.isEnabled()) {
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

        uriInfo = null;
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
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInDisk() {
        displayOptions.setCacheInDiskDisabled(true);
        return this;
    }

    /**
     * 禁用BitmapPool
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableBitmapPool() {
        displayOptions.setBitmapPoolDisabled(true);
        return this;
    }

    /**
     * 设置请求Level
     */
    @SuppressWarnings("unused")
    public DisplayHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            displayOptions.setRequestLevel(requestLevel);
            displayOptions.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 解码Gif图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper decodeGifImage() {
        displayOptions.setDecodeGifImage(true);
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     */
    @SuppressWarnings("unused")
    public DisplayHelper maxSize(int width, int height) {
        displayOptions.setMaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public DisplayHelper resize(int width, int height) {
        displayOptions.setResize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，
     * 但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public DisplayHelper resize(int width, int height, ScaleType scaleType) {
        displayOptions.setResize(new Resize(width, height, scaleType));
        return this;
    }

    /**
     * 使用ImageView的layout_width和layout_height作为resize
     */
    @SuppressWarnings("unused")
    public DisplayHelper resizeByFixedSize() {
        displayOptions.setResizeByFixedSize(true);
        return this;
    }

    /**
     * 强制使经过resize处理后的图片同resize的尺寸一致
     */
    public DisplayHelper forceUseResize() {
        displayOptions.setForceUseResize(true);
        return this;
    }

    /**
     * 返回低质量的图片
     */
    public DisplayHelper lowQualityImage() {
        displayOptions.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper processor(ImageProcessor processor) {
        displayOptions.setImageProcessor(processor);
        return this;
    }

    /**
     * 设置图片质量
     */
    @SuppressWarnings("unused")
    public DisplayHelper bitmapConfig(Bitmap.Config config) {
        displayOptions.setBitmapConfig(config);
        return this;
    }

    /**
     * 设置优先考虑质量还是速度
     */
    @SuppressWarnings("unused")
    public DisplayHelper inPreferQualityOverSpeed(boolean inPreferQualityOverSpeed) {
        displayOptions.setInPreferQualityOverSpeed(inPreferQualityOverSpeed);
        return this;
    }

    /**
     * 开启缩略图模式
     */
    @SuppressWarnings("unused")
    public DisplayHelper thumbnailMode() {
        displayOptions.setThumbnailMode(true);
        return this;
    }

    /**
     * 为了加快速度，将经过ImageProcessor、resize或thumbnailMode处理过的图片保存到磁盘缓存中，下次就直接读取
     */
    @SuppressWarnings("unused")
    public DisplayHelper cacheProcessedImageInDisk() {
        displayOptions.setCacheProcessedImageInDisk(true);
        return this;
    }

    /**
     * 禁用纠正图片方向功能
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableCorrectImageOrientation() {
        displayOptions.setCorrectImageOrientationDisabled(true);
        return this;
    }

    /**
     * 禁用内存缓存
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInMemory() {
        displayOptions.setCacheInMemoryDisabled(true);
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper displayer(ImageDisplayer displayer) {
        displayOptions.setImageDisplayer(displayer);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(StateImage loadingImage) {
        displayOptions.setLoadingImage(loadingImage);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(int drawableResId) {
        displayOptions.setLoadingImage(drawableResId);
        return this;
    }

    /**
     * 设置错误时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(StateImage errorImage) {
        displayOptions.setErrorImage(errorImage);
        return this;
    }

    /**
     * 设置错误时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper errorImage(int drawableResId) {
        displayOptions.setErrorImage(drawableResId);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(StateImage pauseDownloadImage) {
        displayOptions.setPauseDownloadImage(pauseDownloadImage);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(int drawableResId) {
        displayOptions.setPauseDownloadImage(drawableResId);
        return this;
    }

    /**
     * 设置图片整型器，用于绘制时修改图片的形状
     */
    @SuppressWarnings("unused")
    public DisplayHelper shaper(ImageShaper imageShaper) {
        displayOptions.setImageShaper(imageShaper);
        return this;
    }

    /**
     * 设置图片尺寸，用于绘制时修改图片的尺寸
     */
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(ShapeSize shapeSize) {
        displayOptions.setShapeSize(shapeSize);
        return this;
    }

    /**
     * 设置图片尺寸，用于绘制时修改图片的尺寸
     */
    @SuppressWarnings("unused")
    public DisplayHelper shapeSize(int width, int height) {
        displayOptions.setShapeSize(width, height);
        return this;
    }

    /**
     * 设置根据ImageView的layout_width和layout_height作为shape size
     */
    @SuppressWarnings("unused")
    public DisplayHelper shapeSizeByFixedSize() {
        displayOptions.setShapeSizeByFixedSize(true);
        return this;
    }


    /**
     * 批量设置显示参数（完全覆盖）
     */
    public DisplayHelper options(DisplayOptions newOptions) {
        displayOptions.copy(newOptions);
        return this;
    }

    /**
     * 提交请求
     */
    public DisplayRequest commit() {
        if (!SketchUtils.isMainThread()) {
            SLog.fw(SLogType.REQUEST, LOG_NAME, "Please perform a commit in the UI thread. viewHashCode=%s. %s",
                    Integer.toHexString(sketchView.hashCode()), uriInfo != null ? uriInfo.getUri() : "");
            if (SLogType.TIME.isEnabled()) {
                Stopwatch.with().print(uriInfo != null ? uriInfo.getUri() : "");
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        CallbackHandler.postCallbackStarted(displayListener, false);
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("callbackStarted");
        }

        boolean checkResult = checkUri();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("checkUri");
        }
        if (!checkResult) {
            if (SLogType.TIME.isEnabled()) {
                Stopwatch.with().print(uriInfo != null ? uriInfo.getUri() : "");
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        preProcess();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("preProcess");
        }

        saveParams();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("saveParams");
        }

        checkResult = checkMemoryCache();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("checkMemoryCache");
        }
        if (!checkResult) {
            if (SLogType.TIME.isEnabled()) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        checkResult = checkRequestLevel();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("checkRequestLevel");
        }
        if (!checkResult) {
            if (SLogType.TIME.isEnabled()) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        DisplayRequest potentialRequest = checkRepeatRequest();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("checkRepeatRequest");
        }
        if (potentialRequest != null) {
            if (SLogType.TIME.isEnabled()) {
                Stopwatch.with().print(key);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return potentialRequest;
        }

        DisplayRequest request = submitRequest();

        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().print(key);
        }
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    private boolean checkUri() {
        if (uriInfo == null) {
            if (SLogType.REQUEST.isEnabled()) {
                SLog.fe(SLogType.REQUEST, LOG_NAME, "uri is null or empty. viewHashCode=%s",
                        Integer.toHexString(sketchView.hashCode()));
            }

            Drawable drawable = null;
            if (displayOptions.getErrorImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getErrorImage().getDrawable(context, sketchView, displayOptions);
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, sketchView, displayOptions);
            }
            sketchView.setImageDrawable(drawable);

            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NULL_OR_EMPTY, false);
            return false;
        }

        if (uriInfo.getScheme() == null) {
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.fe(SLogType.REQUEST, LOG_NAME, "unknown uri scheme: %s. viewHashCode=%s. %s",
                    uriInfo.getUri(), viewCode, uriInfo.getUri());

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
        ImageSizeCalculator imageSizeCalculator = sketch.getConfiguration().getImageSizeCalculator();
        FixedSize fixedSize = viewInfo.getFixedSize();


        // 用ImageVie的固定宽高作为shape size
        ShapeSize shapeSize = displayOptions.getShapeSize();
        if (shapeSize == null && displayOptions.isShapeSizeByFixedSize()) {
            if (fixedSize != null) {
                shapeSize = new ShapeSize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType());
                displayOptions.setShapeSize(shapeSize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the shapeSizeByFixedSize function");
            }
        }

        // 如果没有设置ScaleType的话就从ImageView身上取
        if (shapeSize != null && shapeSize.getScaleType() == null && sketchView != null) {
            shapeSize.setScaleType(viewInfo.getScaleType());
        }

        // 检查Resize的宽高都必须大于0
        if (shapeSize != null && (shapeSize.getWidth() == 0 || shapeSize.getHeight() == 0)) {
            throw new IllegalArgumentException("ShapeSize width and height must be > 0");
        }


        // 用ImageVie的固定宽高作为resize
        Resize resize = displayOptions.getResize();
        if (resize == null && displayOptions.isResizeByFixedSize()) {
            if (fixedSize != null) {
                resize = new Resize(fixedSize.getWidth(), fixedSize.getHeight(), viewInfo.getScaleType());
                displayOptions.setResize(resize);
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the resizeByFixedSize function");
            }
        }

        // 如果没有设置ScaleType的话就从ImageView身上取
        if (resize != null && resize.getScaleType() == null && sketchView != null) {
            resize.setScaleType(viewInfo.getScaleType());
        }

        // 检查Resize的宽高都必须大于0
        if (resize != null && (resize.getWidth() == 0 || resize.getHeight() == 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }


        // 没有设置maxSize的话，如果ImageView的宽高是的固定的就根据ImageView的宽高来作为maxSize，否则就用默认的maxSize
        if (displayOptions.getMaxSize() == null) {
            MaxSize maxSize = imageSizeCalculator.calculateImageMaxSize(sketchView);
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.getContext());
            }
            displayOptions.setMaxSize(maxSize);
        }

        // 检查MaxSize的宽或高大于0即可
        MaxSize maxSize = displayOptions.getMaxSize();
        if (maxSize != null && maxSize.getWidth() <= 0 && maxSize.getHeight() <= 0) {
            throw new IllegalArgumentException("MaxSize width or height must be > 0");
        }


        // 没有ImageProcessor但有resize的话就需要设置一个默认的图片裁剪处理器
        if (displayOptions.getImageProcessor() == null && resize != null) {
            displayOptions.setImageProcessor(configuration.getResizeImageProcessor());
        }


        // 如果设置了全局使用低质量图片的话就强制使用低质量的图片
        if (configuration.isGlobalLowQualityImage()) {
            displayOptions.setLowQualityImage(true);
        }

        // 如果设置了全局解码质量优先
        if (configuration.isGlobalInPreferQualityOverSpeed()) {
            displayOptions.setInPreferQualityOverSpeed(true);
        }

        // 如果没有设置请求Level的话就跟据暂停下载和暂停加载功能来设置请求Level
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

        // ImageDisplayer必须得有
        if (displayOptions.getImageDisplayer() == null) {
            displayOptions.setImageDisplayer(configuration.getDefaultImageDisplayer());
        }

        // 使用过渡图片显示器的时候，如果使用了loadingImage的话就必须配合ShapeSize才行，如果没有ShapeSize就取ImageView的宽高作为ShapeSize
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
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fd(SLogType.REQUEST, LOG_NAME, "%s. viewHashCode=%s. %s",
                            errorInfo, Integer.toHexString(sketchView.hashCode()), uriInfo.getUri());
                }
                throw new IllegalArgumentException(errorInfo);
            }
        }

        // 根据URI和显示选项生成请求key
        key = SketchUtils.makeRequestKey(uriInfo.getUri(), uriInfo.getScheme(), displayOptions);
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

        displayCache.uri = uriInfo.getUri();
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
            if (SLogType.REQUEST.isEnabled()) {
                String viewCode = Integer.toHexString(sketchView.hashCode());
                SLog.fe(SLogType.REQUEST, LOG_NAME, "memory cache drawable recycled. %s. viewHashCode=%s",
                        cachedRefBitmap.getInfo(), viewCode);
            }
            return true;
        }

        // 立马标记等待使用，防止被回收
        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", LOG_NAME), true);

        if (SLogType.REQUEST.isEnabled()) {
            String viewCode = Integer.toHexString(sketchView.hashCode());
            SLog.fi(SLogType.REQUEST, LOG_NAME, "image display completed. %s. %s. viewHashCode=%s",
                    ImageFrom.MEMORY_CACHE.name(), cachedRefBitmap.getInfo(), viewCode);
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

        ((SketchRefDrawable) finalDrawable).setIsWaitingUse(String.format("%s:waitingUse:finish", LOG_NAME), false);
        return false;
    }

    private boolean checkRequestLevel() {
        // 如果已经暂停加载的话就不再从本地或网络加载了
        if (displayOptions.getRequestLevel() == RequestLevel.MEMORY) {
            boolean isPauseLoad = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD;

            if (SLogType.REQUEST.isEnabled()) {
                SLog.fw(SLogType.REQUEST, LOG_NAME,
                        "canceled. %s. viewHashCode=%s. %s", isPauseLoad ? "pause load" : "requestLevel is memory",
                        Integer.toHexString(sketchView.hashCode()), key);
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
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL && uriInfo.getScheme() == UriScheme.NET
                && !sketch.getConfiguration().getDiskCache().exist(uriInfo.getDiskCacheKey())) {
            boolean isPauseDownload = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (SLogType.REQUEST.isEnabled()) {
                SLog.fd(SLogType.REQUEST, LOG_NAME,
                        "canceled. %s. viewHashCode=%s. %s", isPauseDownload ? "pause download" : "requestLevel is local",
                        Integer.toHexString(sketchView.hashCode()), key);
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
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fw(SLogType.REQUEST, LOG_NAME, "pauseDownloadDrawable is null. viewHashCode=%s. %s",
                            Integer.toHexString(sketchView.hashCode()), key);
                }
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
     * @return DisplayRequest 非null：请求一模一样，无需取消；null：已经取消或没有已存在的请求
     */
    private DisplayRequest checkRepeatRequest() {
        DisplayRequest potentialRequest = SketchUtils.findDisplayRequest(sketchView);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (key.equals(potentialRequest.getKey())) {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fd(SLogType.REQUEST, LOG_NAME, "repeat request. newId=%s. viewHashCode=%s",
                            key, Integer.toHexString(sketchView.hashCode()));
                }
                return potentialRequest;
            } else {
                if (SLogType.REQUEST.isEnabled()) {
                    SLog.fw(SLogType.REQUEST, LOG_NAME, "cancel old request. newId=%s. oldId=%s. viewHashCode=%s",
                            key, potentialRequest.getKey(), Integer.toHexString(sketchView.hashCode()));
                }
                potentialRequest.cancel(CancelCause.BE_REPLACED_ON_HELPER);
            }
        }

        return null;
    }

    private DisplayRequest submitRequest() {
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        RequestAndViewBinder requestAndViewBinder = new RequestAndViewBinder(sketchView);
        DisplayRequest request = requestFactory.newDisplayRequest(sketch, uriInfo, key, displayOptions, viewInfo,
                requestAndViewBinder, displayListener, downloadProgressListener);
        if (SLogType.TIME.isEnabled()) {
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
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("createLoadingImage");
        }

        sketchView.setImageDrawable(loadingDrawable);
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("setLoadingImage");
        }

        if (SLogType.REQUEST.isEnabled()) {
            SLog.fd(SLogType.REQUEST, LOG_NAME, "submit request. viewHashCode=%s. %s",
                    Integer.toHexString(sketchView.hashCode()), key);
        }

        request.submit();
        if (SLogType.TIME.isEnabled()) {
            Stopwatch.with().record("submitRequest");
        }

        return request;
    }
}