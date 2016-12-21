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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.LoadingDrawable;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.drawable.RefBitmapDrawable;
import me.xiaopan.sketch.drawable.ShapeBitmapDrawable;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.state.StateImage;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.Stopwatch;

public class DisplayHelper {
    protected String logName = "DisplayHelper";

    protected Sketch sketch;

    protected DisplayInfo displayInfo = new DisplayInfo();
    protected DisplayOptions displayOptions = new DisplayOptions();
    protected DisplayListener displayListener;
    protected DownloadProgressListener downloadProgressListener;

    protected ViewInfo viewInfo = new ViewInfo();
    protected ImageViewInterface imageViewInterface;

    /**
     * 支持以下几种图片Uri
     * <blockQuote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"file:///mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockQuote>
     */
    public DisplayHelper(Sketch sketch, String uri, ImageViewInterface imageViewInterface) {
        init(sketch, uri, imageViewInterface);
    }

    public DisplayHelper(Sketch sketch, DisplayParams displayParams, ImageViewInterface imageViewInterface) {
        init(sketch, displayParams, imageViewInterface);
    }

    /**
     * 初始化
     */
    public DisplayHelper init(Sketch sketch, String uri, ImageViewInterface imageViewInterface) {
        this.sketch = sketch;
        this.imageViewInterface = imageViewInterface;

        if (Sketch.isDebugMode()) {
            Stopwatch.with().start(Sketch.TAG, logName + ". display use time");
        }

        // onDisplay一定要在最前面执行，因为在onDisplay中会设置一些属性，这些属性会影响到后续一些get方法返回的结果
        this.imageViewInterface.onDisplay(displayInfo.getUriScheme());
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("onDisplay");
        }

        displayInfo.reset(uri);
        viewInfo.reset(imageViewInterface, sketch);
        displayOptions.copy(imageViewInterface.getOptions());
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("init");
        }

        displayListener = imageViewInterface.getDisplayListener();
        downloadProgressListener = imageViewInterface.getDownloadProgressListener();

        return this;
    }

    /**
     * 初始化，此方法用来在RecyclerView中恢复使用
     */
    public DisplayHelper init(Sketch sketch, DisplayParams params, ImageViewInterface imageViewInterface) {
        this.sketch = sketch;
        this.imageViewInterface = imageViewInterface;

        if (Sketch.isDebugMode()) {
            Stopwatch.with().start(Sketch.TAG, logName + ". display use time");
        }

        // onDisplay一定要在最前面执行，因为在onDisplay中会设置一些属性，这些属性会影响到后续一些get方法返回的结果
        this.imageViewInterface.onDisplay(displayInfo.getUriScheme());
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("onDisplay");
        }

        displayInfo.copy(params.info);
        viewInfo.reset(imageViewInterface, sketch);
        displayOptions.copy(params.options);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("init");
        }

        displayListener = imageViewInterface.getDisplayListener();
        downloadProgressListener = imageViewInterface.getDownloadProgressListener();

        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        sketch = null;

        displayInfo.reset(null);
        displayOptions.reset();
        displayListener = null;
        downloadProgressListener = null;
        viewInfo.reset(null, null);
        imageViewInterface = null;
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
     * 批量设置显示参数，这会是一个合并的过程，并不会完全覆盖
     */
    public DisplayHelper options(DisplayOptions newOptions) {
        displayOptions.apply(newOptions);
        return this;
    }

    /**
     * 批量设置显示参数，你只需要提前将DisplayOptions通过Sketch.putDisplayOptions()方法存起来，
     * 然后在这里指定其名称即可，另外这会是一个合并的过程，并不会完全覆盖
     */
    @SuppressWarnings("unused")
    public DisplayHelper optionsByName(Enum<?> optionsName) {
        return options(Sketch.getDisplayOptions(optionsName));
    }

    /**
     * 提交请求
     */
    public DisplayRequest commit() {
        if (!SketchUtils.isMainThread()) {
            Log.w(Sketch.TAG, SketchUtils.concat(logName,
                    ". Please perform a commit in the UI thread",
                    ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                    ". ", displayInfo.getUri()));
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(displayInfo.getUri());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        CallbackHandler.postCallbackStarted(displayListener, false);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("callbackStarted");
        }

        preProcess();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("preProcess");
        }

        saveParams();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("saveParams");
        }

        boolean checkResult = checkUri();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("checkUri");
        }
        if (!checkResult) {
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(displayInfo.getId());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        checkResult = checkMemoryCache();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("checkMemoryCache");
        }
        if (!checkResult) {
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(displayInfo.getId());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        checkResult = checkRequestLevel();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("checkRequestLevel");
        }
        if (!checkResult) {
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(displayInfo.getId());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        DisplayRequest potentialRequest = checkRepeatRequest();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("checkRepeatRequest");
        }
        if (potentialRequest != null) {
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(displayInfo.getId());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return potentialRequest;
        }

        DisplayRequest request = submitRequest();

        if (Sketch.isDebugMode()) {
            Stopwatch.with().print(displayInfo.getId());
        }
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    /**
     * 对相关参数进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();
        ImageSizeCalculator imageSizeCalculator = sketch.getConfiguration().getImageSizeCalculator();

        // 用ImageVie的固定宽高作为resize
        if (displayOptions.isResizeByFixedSize()) {
            FixedSize fixedSize = viewInfo.getFixedSize();
            if (fixedSize != null) {
                displayOptions.setResize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the resizeByFixedSize function");
            }
        }

        // 用ImageVie的固定宽高作为shape size
        if (displayOptions.isShapeSizeByFixedSize()) {
            FixedSize fixedSize = viewInfo.getFixedSize();
            if (fixedSize != null) {
                displayOptions.setShapeSize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                throw new IllegalStateException("ImageView's width and height are not fixed," +
                        " can not be applied with the shapeSizeByFixedSize function");
            }
        }

        // 如果没有设置ScaleType的话就从ImageView身上取
        Resize resize = displayOptions.getResize();
        if (resize != null && resize.getScaleType() == null && imageViewInterface != null) {
            resize.setScaleType(viewInfo.getScaleType());
        }

        // 没有ImageProcessor但有resize的话就需要设置一个默认的图片裁剪处理器
        if (resize != null && displayOptions.getImageProcessor() == null) {
            displayOptions.setImageProcessor(configuration.getResizeImageProcessor());
        }

        // 检查Resize的宽高都必须大于0
        if (resize != null && (resize.getWidth() == 0 || resize.getHeight() == 0)) {
            throw new IllegalArgumentException("Resize width and height must be > 0");
        }

        // 没有设置maxSize的话，如果ImageView的宽高是的固定的就根据ImageView的宽高来作为maxSize，否则就用默认的maxSize
        if (displayOptions.getMaxSize() == null) {
            MaxSize maxSize = imageSizeCalculator.calculateImageMaxSize(imageViewInterface);
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
            FixedSize fixedSize = viewInfo.getFixedSize();
            if (fixedSize != null) {
                displayOptions.setShapeSize(fixedSize.getWidth(), fixedSize.getHeight());
            } else {
                View imageView = imageViewInterface.getSelf();
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                String errorInfo = SketchUtils.concat(
                        "If you use TransitionImageDisplayer and loadingImage, " +
                                "You must be setup ShapeSize or imageView width and height must be fixed",
                        ". width=", SketchUtils.viewLayoutFormatted(layoutParams.width),
                        ", height=", SketchUtils.viewLayoutFormatted(layoutParams.height));
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(logName,
                            ". ", errorInfo,
                            ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                            ". ", displayInfo.getId()));
                }
                throw new IllegalArgumentException(errorInfo);
            }
        }

        // 根据URI和显示选项生成请求ID
        if (displayInfo.getId() == null) {
            displayInfo.setId(SketchUtils.makeRequestId(displayInfo.getUri(), displayOptions));
        }
    }

    /**
     * 将相关信息保存在SketchImageView中，以便在RecyclerView中恢复显示使用
     */
    private void saveParams() {
        DisplayParams displayParams = imageViewInterface.getDisplayParams();
        if (displayParams == null) {
            displayParams = new DisplayParams();
            imageViewInterface.setDisplayParams(displayParams);
        }

        displayParams.info.copy(displayInfo);
        displayParams.options.copy(displayOptions);
    }

    private boolean checkUri() {
        if (displayInfo.getUri() == null || "".equals(displayInfo.getUri().trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName,
                        ". uri is null or empty",
                        ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode())));
            }

            Drawable drawable = null;
            if (displayOptions.getErrorImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getErrorImage().getDrawable(context, imageViewInterface, displayOptions);
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, imageViewInterface, displayOptions);
            }
            imageViewInterface.setImageDrawable(drawable);

            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NULL_OR_EMPTY, false);
            return false;
        }

        if (displayInfo.getUriScheme() == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName,
                    ". unknown uri scheme: ", displayInfo.getUri(),
                    ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                    ". ", displayInfo.getId()));

            Drawable drawable = null;
            if (displayOptions.getErrorImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getErrorImage().getDrawable(context, imageViewInterface, displayOptions);
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, imageViewInterface, displayOptions);
            }
            imageViewInterface.setImageDrawable(drawable);

            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NO_SUPPORT, false);
            return false;
        }

        return true;
    }

    private boolean checkMemoryCache() {
        if (displayOptions.isCacheInMemoryDisabled()) {
            return true;
        }

        RefBitmap cachedRefBitmap = sketch.getConfiguration().getMemoryCache().get(displayInfo.getMemoryCacheKey());
        if (cachedRefBitmap == null) {
            return true;
        }

        if (cachedRefBitmap.isRecycled()) {
            sketch.getConfiguration().getMemoryCache().remove(displayInfo.getMemoryCacheKey());
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName,
                        ". ", "memory cache drawable recycled",
                        ". ", cachedRefBitmap.getInfo(),
                        ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode())));
            }
            return true;
        }

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, SketchUtils.concat(logName,
                    ". image display completed",
                    ". ", ImageFrom.MEMORY_CACHE.name(),
                    ". ", cachedRefBitmap.getInfo(),
                    ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode())));
        }

        RefBitmapDrawable refBitmapDrawable = new RefBitmapDrawable(cachedRefBitmap);
        refBitmapDrawable.setImageFrom(ImageFrom.MEMORY_CACHE);

        Drawable finalDrawable;
        if (displayOptions.getShapeSize() != null || displayOptions.getImageShaper() != null) {
            finalDrawable = new ShapeBitmapDrawable(refBitmapDrawable,
                    displayOptions.getShapeSize(), displayOptions.getImageShaper());
        } else {
            finalDrawable = refBitmapDrawable;
        }

        ImageDisplayer imageDisplayer = displayOptions.getImageDisplayer();
        if (imageDisplayer != null && imageDisplayer.isAlwaysUse()) {
            imageDisplayer.display(imageViewInterface, finalDrawable);
        } else {
            imageViewInterface.setImageDrawable(finalDrawable);
        }
        if (displayListener != null) {
            displayListener.onCompleted(ImageFrom.MEMORY_CACHE, cachedRefBitmap.getMimeType());
        }

        return false;
    }

    private boolean checkRequestLevel() {
        // 如果已经暂停加载的话就不再从本地或网络加载了
        if (displayOptions.getRequestLevel() == RequestLevel.MEMORY) {
            boolean isPauseLoad = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD;

            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        ". canceled",
                        ". ", isPauseLoad ? "pause load" : "requestLevel is memory",
                        ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                        ". ", displayInfo.getId()));
            }

            Drawable loadingDrawable = null;
            if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                loadingDrawable = displayOptions.getLoadingImage().getDrawable(context, imageViewInterface, displayOptions);
            }
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(loadingDrawable);

            CancelCause cancelCause = isPauseLoad ? CancelCause.PAUSE_LOAD : CancelCause.REQUEST_LEVEL_IS_MEMORY;
            CallbackHandler.postCallbackCanceled(displayListener, cancelCause, false);
            return false;
        }

        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL
                && displayInfo.getUriScheme() == UriScheme.NET
                && !sketch.getConfiguration().getDiskCache().exist(displayInfo.getDiskCacheKey())) {
            boolean isPauseDownload = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(logName,
                        ". canceled",
                        ". ", isPauseDownload ? "pause download" : "requestLevel is local",
                        ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                        ". ", displayInfo.getId()));
            }

            // 显示暂停下载图片
            Drawable drawable = null;
            if (displayOptions.getPauseDownloadImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getPauseDownloadImage().getDrawable(context, imageViewInterface, displayOptions);
                imageViewInterface.clearAnimation();
            } else if (displayOptions.getLoadingImage() != null) {
                Context context = sketch.getConfiguration().getContext();
                drawable = displayOptions.getLoadingImage().getDrawable(context, imageViewInterface, displayOptions);
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            ". pauseDownloadDrawable is null",
                            ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                            ". ", displayInfo.getId()));
                }
            }
            imageViewInterface.setImageDrawable(drawable);

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
        DisplayRequest potentialRequest = SketchUtils.findDisplayRequest(imageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (displayInfo.getId().equals(potentialRequest.getId())) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(logName,
                            ". repeat request",
                            ". newId=", displayInfo.getId(),
                            ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode())));
                }
                return potentialRequest;
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            ". cancel old request",
                            ". newId=", displayInfo.getId(),
                            ". oldId=", potentialRequest.getId(),
                            ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode())));
                }
                potentialRequest.cancel(CancelCause.BE_REPLACED_ON_HELPER);
            }
        }

        return null;
    }

    private DisplayRequest submitRequest() {
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        RequestAndViewBinder requestAndViewBinder = new RequestAndViewBinder(imageViewInterface);
        DisplayRequest request = requestFactory.newDisplayRequest(
                sketch, displayInfo, displayOptions, viewInfo,
                requestAndViewBinder, displayListener, downloadProgressListener);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("createRequest");
        }

        LoadingDrawable loadingDrawable;
        StateImage loadingImage = displayOptions.getLoadingImage();
        if (loadingImage != null) {
            Context context = sketch.getConfiguration().getContext();
            Drawable drawable = loadingImage.getDrawable(context, imageViewInterface, displayOptions);
            loadingDrawable = new LoadingDrawable(drawable, request);
        } else {
            loadingDrawable = new LoadingDrawable(null, request);
        }
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("createLoadingImage");
        }

        imageViewInterface.setImageDrawable(loadingDrawable);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("setLoadingImage");
        }

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, SketchUtils.concat(logName,
                    ". submit request",
                    ". viewHashCode=", Integer.toHexString(imageViewInterface.hashCode()),
                    ". ", displayInfo.getId()));
        }

        request.submit();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("submitRequest");
        }

        return request;
    }
}