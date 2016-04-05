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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

public class DisplayHelper {
    private static final String NAME = "DisplayHelper";

    // 基本属性
    protected Sketch sketch;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean cacheInDisk = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected Resize resize;
    protected boolean decodeGifImage = true;
    protected boolean forceUseResize;
    protected boolean lowQualityImage;
    protected MaxSize maxSize;
    protected ImageProcessor imageProcessor;

    // 显示属性
    protected String memoryCacheId;
    protected boolean cacheInMemory = true;
    protected FixedSize fixedSize;
    protected ImageDisplayer imageDisplayer;
    protected DisplayListener displayListener;
    protected ImageHolder loadingImageHolder;
    protected ImageHolder failureImageHolder;
    protected ImageHolder pauseDownloadImageHolder;
    protected SketchImageViewInterface sketchImageViewInterface;

    protected Context context;
    protected ImageView.ScaleType scaleType;

    /**
     * 创建显示请求生成器
     *
     * @param sketch                   Sketch
     * @param uri                      图片Uri，支持以下几种
     *                                 <blockquote>"http://site.com/image.png"; // from Web
     *                                 <br>"https://site.com/image.png"; // from Web
     *                                 <br>"/mnt/sdcard/image.png"; // from SD card
     *                                 <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     *                                 <br>"content://media/external/audio/albumart/13"; // from content provider
     *                                 <br>"asset://image.png"; // from assets
     *                                 <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     *                                 </blockquote>
     * @param sketchImageViewInterface 图片View
     */
    public DisplayHelper(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, uri, sketchImageViewInterface);
    }

    /**
     * 创建显示请求生成器
     *
     * @param sketch                   Sketch
     * @param displayParams            参数集
     *                                 <blockquote>"http://site.com/image.png"; // from Web
     *                                 <br>"https://site.com/image.png"; // from Web
     *                                 <br>"/mnt/sdcard/image.png"; // from SD card
     *                                 <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     *                                 <br>"content://media/external/audio/albumart/13"; // from content provider
     *                                 <br>"asset://image.png"; // from assets
     *                                 <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     *                                 </blockquote>
     * @param sketchImageViewInterface 图片View
     */
    public DisplayHelper(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, displayParams, sketchImageViewInterface);
    }

    /**
     * 初始化
     *
     * @param sketch Sketch
     * @param uri 图片uri
     * @param sketchImageViewInterface ImageView
     */
    public DisplayHelper init(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();
        this.sketch = sketch;
        this.uri = uri;
        this.sketchImageViewInterface = sketchImageViewInterface;

        if (sketch.getConfiguration().isPauseDownload()) {
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
        if (sketch.getConfiguration().isPauseLoad()) {
            this.requestLevel = RequestLevel.MEMORY;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_LOAD;
        }

        this.scaleType = sketchImageViewInterface.getScaleType();
        this.fixedSize = sketch.getConfiguration().getImageSizeCalculator().calculateImageFixedSize(sketchImageViewInterface);
        this.maxSize = sketch.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(sketchImageViewInterface);

        this.sketchImageViewInterface.onDisplay();
        options(this.sketchImageViewInterface.getDisplayOptions());

        this.displayListener = sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
        this.progressListener = sketchImageViewInterface.getProgressListener();

        return this;
    }

    /**
     * 初始化，此方法用来在RecyclerView中恢复使用
     *
     * @param sketch Sketch
     * @param displayParams 显示参数
     * @param sketchImageViewInterface ImageView
     */
    public DisplayHelper init(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();

        this.sketch = sketch;
        this.uri = displayParams.uri;
        this.name = displayParams.name;
        this.requestLevel = displayParams.requestLevel;
        this.requestLevelFrom = displayParams.requestLevelFrom;

        this.cacheInDisk = displayParams.cacheInDisk;
        this.progressListener = displayParams.progressListener;

        this.resize = displayParams.resize;
        this.maxSize = displayParams.maxSize;
        this.forceUseResize = displayParams.forceUseResize;
        this.lowQualityImage = displayParams.lowQualityImage;
        this.requestLevel = displayParams.requestLevel;
        this.imageProcessor = displayParams.imageProcessor;
        this.decodeGifImage = displayParams.decodeGifImage;

        this.sketchImageViewInterface = sketchImageViewInterface;
        this.fixedSize = displayParams.fixedSize;
        this.memoryCacheId = displayParams.memoryCacheId;
        this.cacheInMemory = displayParams.cacheInMemory;
        this.imageDisplayer = displayParams.imageDisplayer;
        this.loadingImageHolder = displayParams.loadingImageHolder;
        this.failureImageHolder = displayParams.loadFailImageHolder;
        this.pauseDownloadImageHolder = displayParams.pauseDownloadImageHolder;

        this.sketchImageViewInterface.onDisplay();

        this.scaleType = sketchImageViewInterface.getScaleType();
        this.displayListener = sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
        this.progressListener = sketchImageViewInterface.getProgressListener();

        return this;
    }

    /**
     * 恢复默认值
     */
    public void reset() {
        sketch = null;
        uri = null;
        name = null;
        requestLevel = RequestLevel.NET;
        requestLevelFrom = null;

        cacheInDisk = true;
        progressListener = null;

        resize = null;
        maxSize = null;
        forceUseResize = false;
        lowQualityImage = false;
        imageProcessor = null;
        decodeGifImage = true;

        memoryCacheId = null;
        fixedSize = null;
        cacheInMemory = true;
        sketchImageViewInterface = null;
        imageDisplayer = null;
        loadingImageHolder = null;
        failureImageHolder = null;
        pauseDownloadImageHolder = null;
        displayListener = null;
    }

    /**
     * 填充SketchImageView的显示参数
     */
    public void saveDisplayParams() {
        if (sketchImageViewInterface != null) {
            DisplayParams displayParams = sketchImageViewInterface.getDisplayParams();
            if (displayParams == null) {
                displayParams = new DisplayParams();
            }

            displayParams.uri = uri;
            displayParams.name = name;
            displayParams.requestLevel = requestLevel;
            displayParams.requestLevelFrom = requestLevelFrom;

            displayParams.cacheInDisk = cacheInDisk;
            displayParams.progressListener = progressListener;

            displayParams.resize = resize;
            displayParams.maxSize = maxSize;
            displayParams.forceUseResize = forceUseResize;
            displayParams.lowQualityImage = lowQualityImage;
            displayParams.imageProcessor = imageProcessor;
            displayParams.decodeGifImage = decodeGifImage;

            displayParams.memoryCacheId = memoryCacheId;
            displayParams.fixedSize = fixedSize;
            displayParams.cacheInMemory = cacheInMemory;
            displayParams.imageDisplayer = imageDisplayer;
            displayParams.loadingImageHolder = loadingImageHolder;
            displayParams.loadFailImageHolder = failureImageHolder;
            displayParams.pauseDownloadImageHolder = pauseDownloadImageHolder;

            sketchImageViewInterface.setDisplayParams(displayParams);
        }
    }

    /**
     * 设置名称，用于在log总区分请求
     *
     * @param name 名称
     * @return DisplayHelper
     */
    public DisplayHelper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置内存缓存ID（大多数情况下你不需要手动设置缓存ID，除非你想使用通过putBitmap()放到缓存中的图片）
     *
     * @param memoryCacheId 内存缓存ID
     * @return DisplayHelper
     */
    public DisplayHelper memoryCacheId(String memoryCacheId) {
        this.memoryCacheId = memoryCacheId;
        return this;
    }

    /**
     * 关闭硬盘缓存
     *
     * @return DisplayHelper
     */
    public DisplayHelper disableDiskCache() {
        this.cacheInDisk = false;
        return this;
    }

    /**
     * 禁止解码Gif图片
     *
     * @return LoadHelper
     */
    public DisplayHelper disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     *
     * @param maxSize 最大尺寸
     * @return DisplayHelper
     */
    public DisplayHelper maxSize(MaxSize maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码时会使用此Size来计算inSimpleSize
     *
     * @param width  宽
     * @param height 高
     * @return DisplayHelper
     */
    public DisplayHelper maxSize(int width, int height) {
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resi，也有可能小于resize
     *
     * @param width  宽
     * @param height 高
     * @return DisplayHelper
     */
    public DisplayHelper resize(int width, int height) {
        this.resize = new Resize(width, height);
        return this;
    }

    /**
     * 裁剪图片，将原始图片加载到内存中之后根据resize进行裁剪。裁剪的原则就是最终返回的图片的比例一定是跟resize一样的，但尺寸不一定会等于resize，也有可能小于resize
     *
     * @param width     宽
     * @param height    高
     * @param scaleType
     * @return LoadHelper
     */
    public DisplayHelper resize(int width, int height, ScaleType scaleType) {
        this.resize = new Resize(width, height, scaleType);
        return this;
    }

    /**
     * 使用ImageView的LayoutSize作为resize
     */
    public DisplayHelper resizeByFixedSize() {
        this.resize = sketch.getConfiguration().getImageSizeCalculator().calculateImageResize(sketchImageViewInterface);
        return this;
    }

    /**
     * 强制使经过resize返回的图片同resize的尺寸一致
     *
     * @return DisplayHelper
     */
    public DisplayHelper forceUseResize() {
        this.forceUseResize = true;
        return this;
    }

    /**
     * 返回低质量的图片
     *
     * @return LoadHelper
     */
    public DisplayHelper lowQualityImage() {
        this.lowQualityImage = true;
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     *
     * @param processor Bitmap处理器
     * @return DisplayHelper
     */
    public DisplayHelper processor(ImageProcessor processor) {
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 关闭内存缓存
     *
     * @return DisplayHelper
     */
    public DisplayHelper disableMemoryCache() {
        this.cacheInMemory = false;
        return this;
    }

    /**
     * 设置显示监听器
     *
     * @param displayListener 显示监听器
     */
    public DisplayHelper listener(DisplayListener displayListener) {
        this.displayListener = displayListener;
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     *
     * @param displayer 图片显示器
     */
    public DisplayHelper displayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param loadingImageHolder 正在加载时显示的图片
     */
    public DisplayHelper loadingImage(ImageHolder loadingImageHolder) {
        this.loadingImageHolder = loadingImageHolder;
        return this;
    }

    /**
     * 设置正在加载时显示的图片
     *
     * @param drawableResId 资源图片ID
     */
    public DisplayHelper loadingImage(int drawableResId) {
        loadingImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置失败时显示的图片
     *
     * @param failureImageHolder 失败时显示的图片
     */
    public DisplayHelper failureImage(ImageHolder failureImageHolder) {
        this.failureImageHolder = failureImageHolder;
        return this;
    }

    /**
     * 设置失败时显示的图片
     *
     * @param drawableResId 资源图片ID
     */
    public DisplayHelper failureImage(int drawableResId) {
        failureImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param pauseDownloadImageHolder 暂停下载时显示的图片
     */
    public DisplayHelper pauseDownloadImage(ImageHolder pauseDownloadImageHolder) {
        this.pauseDownloadImageHolder = pauseDownloadImageHolder;
        return this;
    }

    /**
     * 设置暂停下载时显示的图片
     *
     * @param drawableResId 资源图片ID
     */
    public DisplayHelper pauseDownloadImage(int drawableResId) {
        pauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    /**
     * 设置进度监听器
     *
     * @param progressListener 进度监听器
     * @return DisplayHelper
     */
    public DisplayHelper progressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置请求Level
     *
     * @param requestLevel 请求Level
     * @return DisplayHelper
     */
    public DisplayHelper requestLevel(RequestLevel requestLevel) {
        if (requestLevel != null) {
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    /**
     * 设置显示参数
     *
     * @param options 显示参数
     * @return DisplayHelper
     */
    public DisplayHelper options(DisplayOptions options) {
        if (options == null) {
            return this;
        }

        this.cacheInDisk = options.isCacheInDisk();
        this.cacheInMemory = options.isCacheInMemory();
        if (this.maxSize == null || (options.getMaxSize() != null && sketch.getConfiguration().getImageSizeCalculator().compareMaxSize(options.getMaxSize(), this.maxSize) < 0)) {
            this.maxSize = options.getMaxSize();
        }
        if (this.resize == null) {
            if (options.isResizeByFixedSize()) {
                resizeByFixedSize();
            } else if (options.getResize() != null) {
                this.resize = new Resize(options.getResize());
            }
        }
        this.forceUseResize = options.isForceUseResize();
        this.lowQualityImage = options.isLowQualityImage();
        if (this.imageProcessor == null) {
            this.imageProcessor = options.getImageProcessor();
        }
        if (this.imageDisplayer == null) {
            this.imageDisplayer = options.getImageDisplayer();
        }
        this.decodeGifImage = options.isDecodeGifImage();
        if (this.loadingImageHolder == null) {
            this.loadingImageHolder = options.getLoadingImageHolder();
        }
        if (this.failureImageHolder == null) {
            this.failureImageHolder = options.getFailureImage();
        }
        if (this.pauseDownloadImageHolder == null) {
            this.pauseDownloadImageHolder = options.getPauseDownloadImage();
        }
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if (requestLevel != null && optionRequestLevel != null) {
            if (optionRequestLevel.getLevel() < requestLevel.getLevel()) {
                this.requestLevel = optionRequestLevel;
                this.requestLevelFrom = null;
            }
        } else if (optionRequestLevel != null) {
            this.requestLevel = optionRequestLevel;
            this.requestLevelFrom = null;
        }

        return this;
    }

    /**
     * 设置显示参数，你只需要提前将DisplayOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     *
     * @param optionsName 参数名称
     * @return DisplayHelper
     */
    public DisplayHelper options(Enum<?> optionsName) {
        return options((DisplayOptions) Sketch.getOptions(optionsName));
    }

    /**
     * 处理一下参数
     */
    protected void handleParams() {
        if (resize != null && resize.getScaleType() == null && sketchImageViewInterface != null) {
            resize.setScaleType(sketchImageViewInterface.getScaleType());
        }
        if (resize != null && imageProcessor == null) {
            imageProcessor = sketch.getConfiguration().getDefaultCutImageProcessor();
        }
        if (maxSize == null) {
            maxSize = sketch.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketch.getConfiguration().getContext());
        }
        if (name == null && memoryCacheId != null) {
            name = memoryCacheId;
        }
        if (!sketch.getConfiguration().isDecodeGifImage()) {
            decodeGifImage = false;
        }
        if (!sketch.getConfiguration().isCacheInDisk()) {
            cacheInDisk = false;
        }
        if (!sketch.getConfiguration().isCacheInMemory()) {
            cacheInMemory = false;
        }
        if (sketch.getConfiguration().isLowQualityImage()) {
            lowQualityImage = true;
        }
        if (imageDisplayer instanceof TransitionImageDisplayer) {
            if (fixedSize != null) {
                if (loadingImageHolder != null && scaleType != ScaleType.CENTER_CROP) {
                    throw new IllegalArgumentException("When using TransitionImageDisplayer ImageView wide tall if is fixed and set the loadingImage, then ScaleType must be CENTER_CTOP");
                }
            } else {
                if (loadingImageHolder != null) {
                    throw new IllegalArgumentException("When using TransitionImageDisplayer ImageView wide tall if is unknown may not be used then loadingImage");
                }
            }
        }
    }

    /**
     * 生成内存缓存ID
     */
    public String generateMemoryCacheId(String uri, MaxSize maxSize, Resize resize, boolean forceUseResize, boolean lowQualityImage, ImageProcessor imageProcessor) {
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        if (maxSize != null) {
            builder.append("_");
            maxSize.appendIdentifier(builder);
        }
        if (resize != null) {
            builder.append("_");
            resize.appendIdentifier(builder);
        }
        if (forceUseResize) {
            builder.append("_");
            builder.append("forceUseResize");
        }
        if (lowQualityImage) {
            builder.append("_");
            builder.append("lowQualityImage");
        }
        if (imageProcessor != null) {
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }

    /**
     * 提交请求
     *
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public Request commit() {
        saveDisplayParams();

        handleParams();

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
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
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
            Drawable failureDrawable = failureImageHolder != null ? failureImageHolder.getRecycleBitmapDrawable(context) : null;
            if (failureDrawable != null) {
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if (displayListener != null) {
                displayListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if (uriScheme == null) {
            if (Sketch.isDebugMode()) {
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme: ", uri, " - ", (name != null ? name : uri)));
            }
            if (sketchImageViewInterface != null) {
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = failureImageHolder != null ? failureImageHolder.getRecycleBitmapDrawable(context) : null;
            if (failureDrawable != null) {
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if (displayListener != null) {
                displayListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String memoryCacheId = this.memoryCacheId != null ? this.memoryCacheId : generateMemoryCacheId(uri, maxSize, resize, forceUseResize, lowQualityImage, imageProcessor);
        if (name == null) {
            name = memoryCacheId;
        }
        if (cacheInMemory) {
            Drawable cacheDrawable = sketch.getConfiguration().getMemoryCache().get(memoryCacheId);
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
                    sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                    return null;
                } else {
                    sketch.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if (Sketch.isDebugMode()) {
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if (requestLevel == RequestLevel.MEMORY) {
            Drawable loadingDrawable = loadingImageHolder != null ? loadingImageHolder.getRecycleBitmapDrawable(context) : null;
            sketchImageViewInterface.clearAnimation();
            sketchImageViewInterface.setImageDrawable(loadingDrawable);
            if (displayListener != null) {
                displayListener.onCanceled(requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? CancelCause.PAUSE_LOAD : CancelCause.LEVEL_IS_MEMORY);
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", (requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? "pause load" : "requestLevel is memory"), " - ", name));
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
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "don't need to cancel", "；", "ImageViewCode", "=", Integer.toHexString(sketchImageViewInterface.hashCode()), "；", potentialRequest.getName()));
                }
                sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                return potentialRequest;
            } else {
                potentialRequest.cancel();
            }
        }

        // 组织请求
        final DisplayRequest request = sketch.getConfiguration().getRequestFactory().newDisplayRequest(sketch, uri, uriScheme, memoryCacheId, sketchImageViewInterface);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setCacheInDisk(cacheInDisk);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setForceUseResize(forceUseResize);
        request.setLowQualityImage(lowQualityImage);
        request.setImageProcessor(imageProcessor);
        request.setDecodeGifImage(decodeGifImage);

        request.setFixedSize(fixedSize);
        request.setImageDisplayer(imageDisplayer);
        request.setDisplayListener(displayListener);
        request.setCacheInMemory(cacheInMemory);
        request.setFailureImageHolder(failureImageHolder);
        request.setPauseDownloadImageHolder(pauseDownloadImageHolder);

        // 显示默认图片
        Drawable loadingBindDrawable;
        if (loadingImageHolder != null) {
            RecycleBitmapDrawable loadingDrawable = loadingImageHolder.getRecycleBitmapDrawable(context);
            FixedSize tempFixedSize = null;
            if (imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null && scaleType == ScaleType.CENTER_CROP) {
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
        request.postRunDispatch();
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }
}