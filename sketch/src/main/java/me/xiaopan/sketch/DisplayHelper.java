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

package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

public class DisplayHelper {
    private static final String NAME = "DisplayHelper";

    protected Sketch sketch;
    protected String uri;
    protected String name;

    protected String memoryCacheId;
    protected DisplayOptions options;

    protected DisplayListener displayListener;
    protected DownloadProgressListener downloadProgressListener;

    protected FixedSize fixedSize;
    protected ScaleType scaleType;
    protected SketchImageViewInterface sketchImageViewInterface;

    /**
     * 支持以下几种图片Uri
     * <blockQuote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockQuote>
     */
    public DisplayHelper(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        this.options = new DisplayOptions();
        init(sketch, uri, sketchImageViewInterface);
    }

    public DisplayHelper(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        this.options = new DisplayOptions();
        init(sketch, displayParams, sketchImageViewInterface);
    }

    /**
     * 初始化
     */
    public DisplayHelper init(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        this.sketch = sketch;
        this.sketchImageViewInterface = sketchImageViewInterface;

        this.uri = uri;
        options(this.sketchImageViewInterface.getDisplayOptions());

        return this;
    }

    /**
     * 初始化，此方法用来在RecyclerView中恢复使用
     */
    public DisplayHelper init(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        this.sketch = sketch;
        this.sketchImageViewInterface = sketchImageViewInterface;

        recoverParamsFromImageView(displayParams);

        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        sketch = null;
        uri = null;
        name = null;

        memoryCacheId = null;
        options.reset();
        displayListener = null;
        downloadProgressListener = null;

        fixedSize = null;
        scaleType = null;
        sketchImageViewInterface = null;
    }

    /**
     * 将相关信息保存在SketchImageView中，以便在RecyclerView中恢复显示使用
     */
    public void saveParamToImageView() {
        DisplayParams displayParams = sketchImageViewInterface.getDisplayParams();
        if (displayParams == null) {
            displayParams = new DisplayParams();
            displayParams.options = new DisplayOptions(options);
            sketchImageViewInterface.setDisplayParams(displayParams);
        }

        displayParams.uri = uri;
        displayParams.name = name;
        displayParams.options.copy(options);
    }

    /**
     * SketchImageView的DisplayParams中恢复相关属性
     */
    private void recoverParamsFromImageView(DisplayParams displayParams) {
        this.uri = displayParams.uri;
        this.name = displayParams.name;
        this.options.copy(displayParams.options);
    }

    /**
     * 设置名称，用于在log总区分请求
     */
    public DisplayHelper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 关闭硬盘缓存
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableDiskCache() {
        options.setCacheInDisk(false);
        return this;
    }

    /**
     * 设置请求Level
     */
    public DisplayHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            options.setRequestLevel(requestLevel);
            options.setRequestLevelFrom(null);
        }
        return this;
    }

    /**
     * 禁止解码Gif图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableDecodeGifImage() {
        options.setDecodeGifImage(false);
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     */
    public DisplayHelper maxSize(int width, int height) {
        options.setMaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize
     */
    public DisplayHelper resize(int width, int height) {
        options.setResize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize，如果需要必须同resize一致可以设置forceUseResize
     */
    public DisplayHelper resize(int width, int height, ScaleType scaleType) {
        options.setResize(new Resize(width, height, scaleType));
        return this;
    }

    /**
     * 使用ImageView的layout_width和layout_height作为resize
     */
    @SuppressWarnings("unused")
    public DisplayHelper resizeByFixedSize() {
        options.setResizeByFixedSize(true);
        return this;
    }

    /**
     * 强制使经过resize处理后的图片同resize的尺寸一致
     */
    public DisplayHelper forceUseResize() {
        options.setForceUseResize(true);
        return this;
    }

    /**
     * 返回低质量的图片
     */
    public DisplayHelper lowQualityImage() {
        options.setLowQualityImage(true);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper processor(ImageProcessor processor) {
        options.setImageProcessor(processor);
        return this;
    }

    /**
     * 关闭内存缓存
     */
    @SuppressWarnings("unused")
    public DisplayHelper disableMemoryCache() {
        options.setCacheInMemory(false);
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper displayer(ImageDisplayer displayer) {
        options.setImageDisplayer(displayer);
        return this;
    }

    /**
     * 设置内存缓存ID（大多数情况下你不需要手动设置缓存ID，除非你想使用通过putBitmap()放到缓存中的图片）
     */
    @SuppressWarnings("unused")
    public DisplayHelper memoryCacheId(String memoryCacheId) {
        this.memoryCacheId = memoryCacheId;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     */
    public DisplayHelper loadingImage(ImageHolder loadingImageHolder) {
        options.setLoadingImage(loadingImageHolder);
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
    public DisplayHelper failureImage(ImageHolder failureImageHolder) {
        options.setFailureImage(failureImageHolder);
        return this;
    }

    /**
     * 设置失败时显示的图片
     */
    @SuppressWarnings("unused")
    public DisplayHelper failureImage(int drawableResId) {
        failureImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     */
    public DisplayHelper pauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        options.setPauseDownloadImage(pauseDownloadImageHolder);
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
        options.apply(newOptions);
        return this;
    }

    /**
     * 批量设置显示参数，你只需要提前将DisplayOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可，另外这会是一个合并的过程，并不会完全覆盖
     */
    public DisplayHelper options(Enum<?> optionsName) {
        return options((DisplayOptions) Sketch.getOptions(optionsName));
    }

    /**
     * 对属性进行预处理
     */
    protected void preProcess() {
        Configuration configuration = sketch.getConfiguration();

        scaleType = sketchImageViewInterface.getScaleType();

        // 计算ImageVie的固定大小
        fixedSize = configuration.getImageSizeCalculator().calculateImageFixedSize(sketchImageViewInterface);

        // 根据ImageVie的固定大小计算resize
        if (options.isResizeByFixedSize()) {
            options.setResize(configuration.getImageSizeCalculator().calculateImageResize(sketchImageViewInterface));
        }

        // 如果没有设置ScaleType的话就从ImageView身上取
        if (options.getResize() != null && options.getResize().getScaleType() == null && sketchImageViewInterface != null) {
            options.getResize().setScaleType(scaleType);
        }

        // 没有ImageProcessor但有resize的话就需要设置一个默认的图片裁剪处理器
        if (options.getImageProcessor() == null && options.getResize() != null) {
            options.setImageProcessor(configuration.getDefaultCutImageProcessor());
        }

        // 没有设置maxSize的话，如果ImageView的宽高是的固定的就根据ImageView的宽高来作为maxSize，否则就用默认的maxSize
        if (options.getMaxSize() == null) {
            MaxSize maxSize = configuration.getImageSizeCalculator().calculateImageMaxSize(sketchImageViewInterface);
            if (maxSize == null) {
                maxSize = configuration.getImageSizeCalculator().getDefaultImageMaxSize(configuration.getContext());
            }
            options.setMaxSize(maxSize);
        }

        // 如果设置了全局禁止解码GIF图的话就强制关闭解码GIF图功能
        if (!configuration.isDecodeGifImage()) {
            options.setDecodeGifImage(false);
        }

        // 如果设置了全局禁止使用磁盘缓存的话就强制关闭磁盘缓存功能
        if (!configuration.isCacheInDisk()) {
            options.setCacheInDisk(false);
        }

        // 如果设置了全局禁止使用内存缓存的话就强制内存磁盘缓存功能
        if (!configuration.isCacheInMemory()) {
            options.setCacheInMemory(false);
        }

        // 如果设置了全局使用低质量图片的话就强制使用低质量的图片
        if (configuration.isLowQualityImage()) {
            options.setLowQualityImage(true);
        }

        // 如果没有设置请求Level的话就跟据暂停下载和暂停加载功能来设置请求Level
        if (options.getRequestLevel() == null) {
            if (configuration.isPauseDownload()) {
                options.setRequestLevel(RequestLevel.LOCAL);
                options.setRequestLevelFrom(RequestLevelFrom.PAUSE_DOWNLOAD);
            }

            if (configuration.isPauseLoad()) {
                options.setRequestLevel(RequestLevel.MEMORY);
                options.setRequestLevelFrom(RequestLevelFrom.PAUSE_LOAD);
            }
        }

        // 使用过渡图片显示器的时候，如果使用了loadingImage的话ImageView就必须采用固定宽高以及ScaleType必须是CENTER_CROP
        if (options.getImageDisplayer() instanceof TransitionImageDisplayer
                && options.getLoadingImageHolder() != null
                && (fixedSize == null || scaleType != ScaleType.CENTER_CROP)) {
            String errorInfo = "If you use TransitionImageDisplayer and loadingImage, ImageView layout_width and layout_height must be fixed as well as the ScaleType must be CENTER_CROP.";
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", errorInfo, " - ", uri));
            }
            throw new IllegalArgumentException(errorInfo);
        }

        // 没有设置内存缓存ID的话就计算内存缓存ID，这个通常是不是需要使用者主动设置的，除非你想使用你自己放入MemoryCache中的图片
        if (memoryCacheId == null) {
            memoryCacheId = options.appendMemoryCacheKey(new StringBuilder(uri)).toString();
        }

        // 没有设置名称的话就用内存缓存ID作为名称，名称主要用来在log中区分请求的
        if (name == null) {
            name = memoryCacheId;
        }

        // onDisplay一定要放在getDisplayListener()和getProgressListener()之前调用，因为在onDisplay的时候会设置一些属性，这些属性会影响到getDisplayListener()和getProgressListener()的结果
        sketchImageViewInterface.onDisplay();

        displayListener = sketchImageViewInterface.getDisplayListener(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_DOWNLOAD);
        downloadProgressListener = sketchImageViewInterface.getDownloadProgressListener();
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public DisplayRequest commit() {
        saveParamToImageView();

        preProcess();

        Configuration configuration = sketch.getConfiguration();
        Context context = configuration.getContext();

        if (displayListener != null) {
            displayListener.onStarted();
        }

        // 验证imageView参数
        if (sketchImageViewInterface == null) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "sketchImageViewInterface is null", " - ", (name != null ? name : uri)));
            }
            if (displayListener != null) {
                displayListener.onFailed(FailCause.IMAGE_VIEW_NULL);
            }
            configuration.getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if (uri == null || "".equals(uri.trim())) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if (sketchImageViewInterface != null) {
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = options.getFailureImage() != null ? options.getFailureImage().getRecycleBitmapDrawable(context) : null;
            if (failureDrawable != null) {
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if (displayListener != null) {
                displayListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            configuration.getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if (uriScheme == null) {
            Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme: ", uri, " - ", (name != null ? name : uri)));
            if (sketchImageViewInterface != null) {
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = options.getFailureImage() != null ? options.getFailureImage().getRecycleBitmapDrawable(context) : null;
            if (failureDrawable != null) {
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if (displayListener != null) {
                displayListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            configuration.getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        if (options.isCacheInMemory()) {
            Drawable cacheDrawable = configuration.getMemoryCache().get(memoryCacheId);
            if (cacheDrawable != null) {
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if (!recycleDrawable.isRecycled()) {
                    if (Sketch.isDebugMode()) {
                        Log.i(Sketch.TAG, SketchUtils.concat(NAME, " - ", "from memory get bitmap", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                    if (sketchImageViewInterface != null) {
                        sketchImageViewInterface.setDisplayRequest(null);
                    }
                    sketchImageViewInterface.setImageDrawable(cacheDrawable);
                    if (displayListener != null) {
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    }
                    configuration.getHelperFactory().recycleDisplayHelper(this);
                    return null;
                } else {
                    configuration.getMemoryCache().remove(memoryCacheId);
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if (options.getRequestLevel() == RequestLevel.MEMORY) {
            Drawable loadingDrawable = options.getLoadingImageHolder() != null ? options.getLoadingImageHolder().getRecycleBitmapDrawable(context) : null;
            sketchImageViewInterface.clearAnimation();
            sketchImageViewInterface.setImageDrawable(loadingDrawable);
            if (displayListener != null) {
                displayListener.onCanceled(options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD ? CancelCause.PAUSE_LOAD : CancelCause.LEVEL_IS_MEMORY);
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", (options.getRequestLevelFrom() == RequestLevelFrom.PAUSE_LOAD ? "pause load" : "requestLevel is memory"), " - ", name));
                }
            }
            if (sketchImageViewInterface != null) {
                sketchImageViewInterface.setDisplayRequest(null);
            }
            return null;
        }

        // 试图取消已经存在的请求
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            if (memoryCacheId.equals(potentialRequest.getMemoryCacheId())) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "don't need to cancel", "；", "ImageViewCode", "=", Integer.toHexString(sketchImageViewInterface.hashCode()), "；", potentialRequest.getAttrs().getName()));
                }
                configuration.getHelperFactory().recycleDisplayHelper(this);
                return potentialRequest;
            } else {
                potentialRequest.cancel();
            }
        }

        // 组织请求
        RequestAttrs attrs = new RequestAttrs(sketch, uri, uriScheme, name);
        final DisplayRequest request = configuration.getRequestFactory().newDisplayRequest(attrs, memoryCacheId, fixedSize, sketchImageViewInterface, options, displayListener);

        request.setDownloadProgressListener(downloadProgressListener);

        // 显示默认图片
        Drawable loadingBindDrawable;
        if (options.getLoadingImageHolder() != null) {
            RecycleBitmapDrawable loadingDrawable = options.getLoadingImageHolder().getRecycleBitmapDrawable(context);
            // 如果使用了TransitionImageDisplayer并且ImageVie是固定大小并且ScaleType是CENT_CROP那么就需要根据ImageVie的固定大小来裁剪loadingImage
            FixedSize tempFixedSize = null;
            if (options.getImageDisplayer() != null && options.getImageDisplayer() instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ScaleType.CENTER_CROP) {
                tempFixedSize = fixedSize;
            }
            loadingBindDrawable = new BindFixedRecycleBitmapDrawable(loadingDrawable, tempFixedSize, request);
        } else {
            loadingBindDrawable = new BindFixedRecycleBitmapDrawable(null, request);
        }
        sketchImageViewInterface.setImageDrawable(loadingBindDrawable);

        if (sketchImageViewInterface != null) {
            sketchImageViewInterface.setDisplayRequest(request);
        }

        // 分发请求
        request.submit();
        configuration.getHelperFactory().recycleDisplayHelper(this);
        return request;
    }
}