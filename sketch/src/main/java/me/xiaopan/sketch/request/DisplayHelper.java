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
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.drawable.BindFixedRecycleBitmapDrawable;
import me.xiaopan.sketch.drawable.RecycleDrawable;
import me.xiaopan.sketch.feture.ImageSizeCalculator;
import me.xiaopan.sketch.feture.RequestFactory;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.util.Stopwatch;

public class DisplayHelper {
    protected String logName = "DisplayHelper";

    protected Sketch sketch;

    protected RequestAttrs requestAttrs = new RequestAttrs();
    protected DisplayAttrs displayAttrs = new DisplayAttrs();
    protected DisplayOptions displayOptions = new DisplayOptions();
    protected DisplayListener displayListener;
    protected DownloadProgressListener progressListener;
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
            Stopwatch.with().start(Sketch.TAG, logName + " - " + "DisplayUseTime");
        }

        requestAttrs.reset(uri);
        displayAttrs.reset(imageViewInterface, sketch);

        // onDisplay一定要放在getDisplayListener()和getProgressListener()之前调用，
        // 因为在onDisplay的时候会设置一些属性，这些属性会影响到getDisplayListener()和getProgressListener()的结果
        this.imageViewInterface.onDisplay(requestAttrs.getUriScheme());
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("onDisplay");
        }

        displayOptions.copy(imageViewInterface.getOptions());
        displayListener = imageViewInterface.getDisplayListener();
        progressListener = imageViewInterface.getDownloadProgressListener();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("copyOptions");
        }

        return this;
    }

    /**
     * 初始化，此方法用来在RecyclerView中恢复使用
     */
    public DisplayHelper init(Sketch sketch, DisplayParams params, ImageViewInterface imageViewInterface) {
        this.sketch = sketch;
        this.imageViewInterface = imageViewInterface;

        if (Sketch.isDebugMode()) {
            Stopwatch.with().start(Sketch.TAG, logName + " - " + "DisplayUseTime");
        }

        requestAttrs.copy(params.attrs);
        displayAttrs.reset(imageViewInterface, sketch);

        // onDisplay一定要放在getDisplayListener()和getProgressListener()之前调用，
        // 因为在onDisplay的时候会设置一些属性，这些属性会影响到getDisplayListener()和getProgressListener()的结果
        this.imageViewInterface.onDisplay(requestAttrs.getUriScheme());
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("onDisplay");
        }

        displayOptions.copy(params.options);
        displayListener = imageViewInterface.getDisplayListener();
        progressListener = imageViewInterface.getDownloadProgressListener();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("copyOptions");
        }

        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        sketch = null;

        requestAttrs.reset(null);
        displayOptions.reset();
        displayListener = null;
        progressListener = null;
        displayAttrs.reset(null, null);
        imageViewInterface = null;
    }

    /**
     * 禁用磁盘缓存
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInDisk() {
        displayOptions.setDisableCacheInDisk(true);
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
     * 禁用内存缓存
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableCacheInMemory() {
        displayOptions.setDisableCacheInMemory(true);
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
     * 设置内存缓存ID（大多数情况下你不需要手动设置缓存ID，除非你想使用通过putBitmap()放到缓存中的图片）
     */
    @SuppressWarnings("unused")
    public DisplayHelper memoryCacheId(String memoryCacheId) {
        this.requestAttrs.setId(memoryCacheId);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    public DisplayHelper loadingImage(ImageHolder loadingImageHolder) {
        displayOptions.setLoadingImage(loadingImageHolder);
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper loadingImage(int drawableResId) {
        loadingImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置失败时显示的图片
     */
    public DisplayHelper failedImage(ImageHolder failedImageHolder) {
        displayOptions.setFailedImage(failedImageHolder);
        return this;
    }

    /**
     * 设置失败时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper failedImage(int drawableResId) {
        failedImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    public DisplayHelper pauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        displayOptions.setPauseDownloadImage(pauseDownloadImageHolder);
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper pauseDownloadImage(int drawableResId) {
        pauseDownloadImage(new ImageHolder(drawableResId));
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
                    " - ", "Please perform a commit in the UI thread",
                    " - ", requestAttrs.getUri()));
            if (Sketch.isDebugMode()) {
                Stopwatch.with().print(requestAttrs.getUri());
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
                Stopwatch.with().print(requestAttrs.getId());
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
                Stopwatch.with().print(requestAttrs.getId());
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
                Stopwatch.with().print(requestAttrs.getId());
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
                Stopwatch.with().print(requestAttrs.getId());
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return potentialRequest;
        }

        DisplayRequest request = submitRequest();

        if (Sketch.isDebugMode()) {
            Stopwatch.with().print(requestAttrs.getId());
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

        // 根据ImageVie的固定大小计算resize
        if (displayOptions.isResizeByFixedSize()) {
            displayOptions.setResize(imageSizeCalculator.calculateImageResize(imageViewInterface));
        }

        // 如果没有设置ScaleType的话就从ImageView身上取
        if (displayOptions.getResize() != null
                && displayOptions.getResize().getScaleType() == null
                && imageViewInterface != null) {
            displayOptions.getResize().setScaleType(displayAttrs.getScaleType());
        }

        // 没有ImageProcessor但有resize的话就需要设置一个默认的图片裁剪处理器
        if (displayOptions.getImageProcessor() == null && displayOptions.getResize() != null) {
            displayOptions.setImageProcessor(configuration.getResizeImageProcessor());
        }

        // 没有设置maxSize的话，如果ImageView的宽高是的固定的就根据ImageView的宽高来作为maxSize，否则就用默认的maxSize
        if (displayOptions.getMaxSize() == null) {
            MaxSize maxSize = imageSizeCalculator.calculateImageMaxSize(imageViewInterface);
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.getContext());
            }
            displayOptions.setMaxSize(maxSize);
        }

        // 如果设置了全局禁用磁盘缓存就强制关闭磁盘缓存功能
        if (configuration.isGlobalDisableCacheInDisk()) {
            displayOptions.setDisableCacheInDisk(true);
        }

        // 如果设置了全局禁用内存缓存就强制关闭内存缓存功能
        if (configuration.isGlobalDisableCacheInMemory()) {
            displayOptions.setDisableCacheInMemory(true);
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
            if (configuration.isPauseDownload()) {
                displayOptions.setRequestLevel(RequestLevel.LOCAL);
                displayOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_DOWNLOAD);
            }

            if (configuration.isPauseLoad()) {
                displayOptions.setRequestLevel(RequestLevel.MEMORY);
                displayOptions.setRequestLevelFrom(RequestLevelFrom.PAUSE_LOAD);
            }
        }

        // ImageDisplayer必须得有
        if (displayOptions.getImageDisplayer() == null) {
            displayOptions.setImageDisplayer(configuration.getDefaultImageDisplayer());
        }

        // 使用过渡图片显示器的时候，如果使用了loadingImage的话ImageView就必须采用固定宽高以及ScaleType必须是CENTER_CROP
        if (displayOptions.getImageDisplayer() instanceof TransitionImageDisplayer
                && displayOptions.getLoadingImageHolder() != null
                && (displayAttrs.getFixedSize() == null || displayAttrs.getScaleType() != ScaleType.CENTER_CROP)) {
            View imageView = imageViewInterface.getSelf();
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            String errorInfo = SketchUtils.concat(
                    "If you use TransitionImageDisplayer and loadingImage, ",
                    "ImageView width and height must be fixed as well as the ScaleType must be CENTER_CROP. ",
                    "Now ",
                    " width is ", SketchUtils.viewLayoutFormatted(layoutParams.width),
                    ", height is ", SketchUtils.viewLayoutFormatted(layoutParams.height),
                    ", ScaleType is ", displayAttrs.getScaleType().name());
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(logName, " - ", errorInfo, " - ", requestAttrs.getUri()));
            }
            throw new IllegalArgumentException(errorInfo);
        }

        // 根据URI和显示选项生成请求ID
        if (requestAttrs.getId() == null) {
            requestAttrs.setId(requestAttrs.generateId(displayOptions));
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

        displayParams.attrs.copy(requestAttrs);
        displayParams.options.copy(displayOptions);
    }

    private boolean checkUri() {
        if (requestAttrs.getUri() == null || "".equals(requestAttrs.getUri().trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(logName, " - ", "uri is null or empty"));
            }

            Drawable drawable = null;
            if (displayOptions.getFailedImageHolder() != null) {
                drawable = displayOptions.getFailedImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            } else if (displayOptions.getLoadingImageHolder() != null) {
                drawable = displayOptions.getLoadingImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            }
            imageViewInterface.setImageDrawable(drawable);

            CallbackHandler.postCallbackFailed(displayListener, FailedCause.URI_NULL_OR_EMPTY, false);
            return false;
        }

        if (requestAttrs.getUriScheme() == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(logName,
                    " - ", "unknown uri scheme: ", requestAttrs.getUri(),
                    " - ", requestAttrs.getId()));

            Drawable drawable = null;
            if (displayOptions.getFailedImageHolder() != null) {
                drawable = displayOptions.getFailedImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            } else if (displayOptions.getLoadingImageHolder() != null) {
                drawable = displayOptions.getLoadingImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            }
            imageViewInterface.setImageDrawable(drawable);

            CallbackHandler.postCallbackFailed(displayListener, FailedCause.URI_NO_SUPPORT, false);
            return false;
        }

        return true;
    }

    private boolean checkMemoryCache() {
        if (!displayOptions.isDisableCacheInMemory()) {
            Drawable cacheDrawable = sketch.getConfiguration().getMemoryCache().get(requestAttrs.getId());
            if (cacheDrawable != null) {
                RecycleDrawable recycleDrawable = (RecycleDrawable) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, SketchUtils.concat(logName,
                                " - ", "from memory get bitmap",
                                " - ", recycleDrawable.getInfo(),
                                " - ", requestAttrs.getId()));
                    }
                    imageViewInterface.setImageDrawable(cacheDrawable);
                    if (displayListener != null) {
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    }
                    return false;
                } else {
                    sketch.getConfiguration().getMemoryCache().remove(requestAttrs.getId());
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(logName,
                                " - ", "memory cache drawable recycled",
                                " - ", recycleDrawable.getInfo(),
                                " - ", requestAttrs.getId()));
                    }
                }
            }
        }

        return true;
    }

    private boolean checkRequestLevel() {
        // 如果已经暂停加载的话就不再从本地或网络加载了
        if (displayOptions.getRequestLevel() == RequestLevel.MEMORY) {
            boolean isPauseLoad = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD;

            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "canceled",
                        " - ", isPauseLoad ? "pause load" : "requestLevel is memory",
                        " - ", requestAttrs.getId()));
            }

            Drawable loadingDrawable = null;
            if (displayOptions.getLoadingImageHolder() != null) {
                loadingDrawable = displayOptions.getLoadingImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            }
            imageViewInterface.clearAnimation();
            imageViewInterface.setImageDrawable(loadingDrawable);

            CancelCause cancelCause = isPauseLoad ? CancelCause.PAUSE_LOAD : CancelCause.LEVEL_IS_MEMORY;
            CallbackHandler.postCallbackCanceled(displayListener, cancelCause, false);
            return false;
        }

        // 如果只从本地加载并且是网络请求并且磁盘中没有缓存就结束吧
        if (displayOptions.getRequestLevel() == RequestLevel.LOCAL
                && requestAttrs.getUriScheme() == UriScheme.NET
                && !sketch.getConfiguration().getDiskCache().exist(requestAttrs.getUri())) {
            boolean isPauseDownload = displayOptions.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD;

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(logName,
                        " - ", "canceled",
                        " - ", isPauseDownload ? "pause download" : "requestLevel is local",
                        " - ", requestAttrs.getId()));
            }

            // 显示暂停下载图片
            Drawable drawable = null;
            if (displayOptions.getPauseDownloadImageHolder() != null) {
                drawable = displayOptions.getPauseDownloadImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
                imageViewInterface.clearAnimation();
            } else if (displayOptions.getLoadingImageHolder() != null) {
                drawable = displayOptions.getLoadingImageHolder().getDrawable(
                        sketch.getConfiguration().getContext(),
                        displayOptions.getImageDisplayer(),
                        displayAttrs.getFixedSize(),
                        displayAttrs.getScaleType());
            } else {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(logName,
                            " - ", "pauseDownloadDrawable is null",
                            " - ", requestAttrs.getId()));
                }
            }
            imageViewInterface.setImageDrawable(drawable);

            CancelCause cancelCause = isPauseDownload ? CancelCause.PAUSE_DOWNLOAD : CancelCause.LEVEL_IS_LOCAL;
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
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.findDisplayRequest(imageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (requestAttrs.getId().equals(potentialRequest.getAttrs().getId())) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(logName,
                            " - ", "don't need to cancel",
                            "；", "ImageViewCode", "=", Integer.toHexString(imageViewInterface.hashCode()),
                            "；", potentialRequest.getAttrs().getId()));
                }
                return potentialRequest;
            } else {
                potentialRequest.cancel();
            }
        }

        return null;
    }

    private DisplayRequest submitRequest() {
        RequestFactory requestFactory = sketch.getConfiguration().getRequestFactory();
        DisplayBinder displayBinder = new DisplayBinder(imageViewInterface);
        DisplayRequest request = requestFactory.newDisplayRequest(
                sketch, requestAttrs, displayAttrs, displayOptions,
                displayBinder, displayListener, progressListener);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("createRequest");
        }

        // 显示默认图片
        Drawable loadingBindDrawable;
        if (displayOptions.getLoadingImageHolder() != null) {
            loadingBindDrawable = displayOptions.getLoadingImageHolder().getBindDrawable(request);
        } else {
            loadingBindDrawable = new BindFixedRecycleBitmapDrawable(null, request);
        }
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("createLoadingImage");
        }

        imageViewInterface.setImageDrawable(loadingBindDrawable);
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("setLoadingImage");
        }

        request.submit();
        if (Sketch.isDebugMode()) {
            Stopwatch.with().record("submitRequest");
        }

        return request;
    }
}