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

import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * DisplayHelper
 */
public class DisplayHelperImpl implements DisplayHelper{
    private static final String NAME = "DisplayHelperImpl";

    // 基本属性
    protected Sketch sketch;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean enableDiskCache = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected Resize resize;
    protected boolean decodeGifImage = true;
    protected MaxSize maxSize;
    protected boolean imagesOfLowQuality;
    protected ImageProcessor imageProcessor;

    // 显示属性
    protected String memoryCacheId;
    protected boolean enableMemoryCache = true;
    protected SketchImageViewInterface sketchImageViewInterface;
    protected FixedSize fixedSize;
    protected ImageDisplayer imageDisplayer;
    protected ImageHolder loadingImageHolder;
    protected ImageHolder failureImageHolder;
    protected ImageHolder pauseDownloadImageHolder;
    protected DisplayListener displayListener;

    protected Context context;

    /**
     * 创建显示请求生成器
     * @param sketch Sketch
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param sketchImageViewInterface 图片View
     */
    public DisplayHelperImpl(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, uri, sketchImageViewInterface);
    }

    /**
     * 创建显示请求生成器
     * @param sketch Sketch
     * @param displayParams 参数集
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param sketchImageViewInterface 图片View
     */
    public DisplayHelperImpl(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        init(sketch, displayParams, sketchImageViewInterface);
    }

    @Override
    public DisplayHelperImpl init(Sketch sketch, String uri, SketchImageViewInterface sketchImageViewInterface){
        this.context = sketch.getConfiguration().getContext();
        this.sketch = sketch;
        this.uri = uri;
        this.sketchImageViewInterface = sketchImageViewInterface;

        if(sketch.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
        if(sketch.getConfiguration().isPauseLoad()){
            this.requestLevel = RequestLevel.MEMORY;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_LOAD;
        }

        if(sketchImageViewInterface != null){
            this.fixedSize = sketch.getConfiguration().getImageSizeCalculator().calculateImageFixedSize(sketchImageViewInterface);
            this.maxSize = sketch.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(sketchImageViewInterface);

            this.sketchImageViewInterface = (SketchImageViewInterface) sketchImageViewInterface;
            this.sketchImageViewInterface.onDisplay();
            options(this.sketchImageViewInterface.getDisplayOptions());

            this.displayListener = this.sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
            this.progressListener = this.sketchImageViewInterface.getProgressListener();
        }

        return this;
    }

    @Override
    public DisplayHelper init(Sketch sketch, DisplayParams displayParams, SketchImageViewInterface sketchImageViewInterface) {
        this.context = sketch.getConfiguration().getContext();

        this.sketch = sketch;
        this.uri = displayParams.uri;
        this.name = displayParams.name;
        this.requestLevel = displayParams.requestLevel;
        this.requestLevelFrom = displayParams.requestLevelFrom;

        this.enableDiskCache = displayParams.enableDiskCache;
        this.progressListener = displayParams.progressListener;

        this.resize = displayParams.resize;
        this.maxSize = displayParams.maxSize;
        this.imagesOfLowQuality = displayParams.imagesOfLowQuality;
        this.requestLevel = displayParams.requestLevel;
        this.imageProcessor = displayParams.imageProcessor;
        this.decodeGifImage = displayParams.decodeGifImage;

        this.sketchImageViewInterface = sketchImageViewInterface;
        this.fixedSize = displayParams.fixedSize;
        this.memoryCacheId = displayParams.memoryCacheId;
        this.enableMemoryCache = displayParams.enableMemoryCache;
        this.imageDisplayer = displayParams.imageDisplayer;
        this.loadingImageHolder = displayParams.loadingImageHolder;
        this.failureImageHolder = displayParams.loadFailImageHolder;
        this.pauseDownloadImageHolder = displayParams.pauseDownloadImageHolder;
        this.displayListener = displayParams.displayListener;

        this.sketchImageViewInterface = (SketchImageViewInterface) sketchImageViewInterface;
        this.sketchImageViewInterface.onDisplay();

        this.displayListener = this.sketchImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
        this.progressListener = this.sketchImageViewInterface.getProgressListener();

        return this;
    }

    @Override
    public void reset(){
        sketch = null;
        uri = null;
        name = null;
        requestLevel = RequestLevel.NET;
        requestLevelFrom = null;

        enableDiskCache = true;
        progressListener = null;

        resize = null;
        maxSize = null;
        imagesOfLowQuality = false;
        imageProcessor = null;
        decodeGifImage = true;

        memoryCacheId = null;
        fixedSize = null;
        enableMemoryCache = true;
        sketchImageViewInterface = null;
        imageDisplayer = null;
        loadingImageHolder = null;
        failureImageHolder = null;
        pauseDownloadImageHolder = null;
        displayListener = null;
    }

    @Override
    public void inflateDisplayParams(){
        if(sketchImageViewInterface != null){
            DisplayParams displayParams = sketchImageViewInterface.getDisplayParams();
            if(displayParams == null){
                displayParams = new DisplayParams();
            }

            displayParams.uri = uri;
            displayParams.name = name;
            displayParams.requestLevel = requestLevel;
            displayParams.requestLevelFrom = requestLevelFrom;

            displayParams.enableDiskCache = enableDiskCache;
            displayParams.progressListener = progressListener;

            displayParams.resize = resize;
            displayParams.maxSize = maxSize;
            displayParams.imagesOfLowQuality = imagesOfLowQuality;
            displayParams.imageProcessor = imageProcessor;
            displayParams.decodeGifImage = decodeGifImage;

            displayParams.memoryCacheId = memoryCacheId;
            displayParams.fixedSize = fixedSize;
            displayParams.enableMemoryCache = enableMemoryCache;
            displayParams.imageDisplayer = imageDisplayer;
            displayParams.loadingImageHolder = loadingImageHolder;
            displayParams.loadFailImageHolder = failureImageHolder;
            displayParams.pauseDownloadImageHolder = pauseDownloadImageHolder;
            displayParams.displayListener = displayListener;

            sketchImageViewInterface.setDisplayParams(displayParams);
        }
    }

    @Override
    public DisplayHelperImpl name(String name){
        this.name = name;
        return this;
    }

    @Override
    public DisplayHelperImpl memoryCacheId(String memoryCacheId){
        this.memoryCacheId = memoryCacheId;
        return this;
    }

    @Override
    public DisplayHelperImpl disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    @Override
    public DisplayHelperImpl disableDecodeGifImage() {
        this.decodeGifImage = false;
        return this;
    }

    @Override
    public DisplayHelperImpl maxSize(MaxSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public DisplayHelperImpl maxSize(int width, int height){
        this.maxSize = new MaxSize(width, height);
        return this;
    }

    @Override
    public DisplayHelperImpl resize(Resize resize){
        this.resize = resize;
        return this;
    }

    @Override
    public DisplayHelperImpl resize(int width, int height){
        this.resize = new Resize(width, height);
        return this;
    }

    @Override
    public DisplayHelperImpl resizeByImageViewLayoutSize(){
        this.resize = sketch.getConfiguration().getImageSizeCalculator().calculateImageResize(sketchImageViewInterface);
        return this;
    }

    @Override
    public DisplayHelperImpl imagesOfLowQuality() {
        this.imagesOfLowQuality = true;
        return this;
    }

    @Override
    public DisplayHelperImpl processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    @Override
    public DisplayHelperImpl disableMemoryCache() {
        this.enableMemoryCache = false;
        return this;
    }

    @Override
    public DisplayHelperImpl listener(DisplayListener displayListener) {
        this.displayListener = displayListener;
        return this;
    }

    @Override
    public DisplayHelperImpl displayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        return this;
    }

    @Override
    public DisplayHelperImpl loadingImage(ImageHolder loadingImage) {
        this.loadingImageHolder = loadingImage;
        return this;
    }

    @Override
    public DisplayHelperImpl loadingImage(int drawableResId) {
        loadingImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl failureImage(ImageHolder failureImage) {
        this.failureImageHolder = failureImage;
        return this;
    }

    @Override
    public DisplayHelperImpl failureImage(int drawableResId) {
        failureImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadImage(ImageHolder pauseDownloadImage) {
        this.pauseDownloadImageHolder = pauseDownloadImage;
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadImage(int drawableResId) {
        pauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DisplayHelperImpl requestLevel(RequestLevel requestLevel){
        if(requestLevel != null){
            this.requestLevel = requestLevel;
            this.requestLevelFrom = null;
        }
        return this;
    }

    @Override
    public DisplayHelperImpl options(DisplayOptions options){
        if(options == null){
            return this;
        }

        this.enableDiskCache = options.isEnableDiskCache();
        this.enableMemoryCache = options.isEnableMemoryCache();
        if(this.maxSize == null || (options.getMaxSize() != null && sketch.getConfiguration().getImageSizeCalculator().compareMaxSize(options.getMaxSize(), this.maxSize) < 0)){
            this.maxSize = options.getMaxSize();
        }
        if(this.resize == null){
            this.resize = options.getResize();
        }
        this.imagesOfLowQuality = options.isImagesOfLowQuality();
        if(options.isResizeByImageViewLayoutSize()){
            resizeByImageViewLayoutSize();
        }
        if(this.imageProcessor == null){
            this.imageProcessor = options.getImageProcessor();
        }
        if(this.imageDisplayer == null){
            displayer(options.getImageDisplayer());
        }
        this.decodeGifImage = options.isDecodeGifImage();
        if(this.loadingImageHolder == null){
            this.loadingImageHolder = options.getLoadingImage();
        }
        if(this.failureImageHolder == null){
            this.failureImageHolder = options.getFailureImage();
        }
        if(this.pauseDownloadImageHolder == null){
            this.pauseDownloadImageHolder = options.getPauseDownloadImage();
        }
        RequestLevel optionRequestLevel = options.getRequestLevel();
        if(requestLevel != null && optionRequestLevel != null){
            if(optionRequestLevel.getLevel() < requestLevel.getLevel()){
                this.requestLevel = optionRequestLevel;
                this.requestLevelFrom = null;
            }
        }else if(optionRequestLevel != null){
            this.requestLevel = optionRequestLevel;
            this.requestLevelFrom = null;
        }

        return this;
    }

    @Override
    public DisplayHelperImpl options(Enum<?> optionsName){
        return options((DisplayOptions) Sketch.getOptions(optionsName));
    }

    /**
     * 处理一下参数
     */
    protected void handleParams(){
        if(resize != null && imageProcessor == null){
            imageProcessor = sketch.getConfiguration().getDefaultCutImageProcessor();
        }
        if(maxSize == null){
            maxSize = sketch.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(sketch.getConfiguration().getContext());
        }
        if(name == null && memoryCacheId != null){
            name = memoryCacheId;
        }
        if(!sketch.getConfiguration().isDecodeGifImage()){
            decodeGifImage = false;
        }
        if(!sketch.getConfiguration().isEnableDiskCache()){
            enableDiskCache = false;
        }
        if(!sketch.getConfiguration().isEnableMemoryCache()){
            enableMemoryCache = false;
        }
        if(sketch.getConfiguration().isImagesOfLowQuality()){
            imagesOfLowQuality = true;
        }
    }

    @Override
    public Request commit() {
        inflateDisplayParams();

        handleParams();

        if(displayListener != null){
            displayListener.onStarted();
        }

        // 验证imageView参数
        if(sketchImageViewInterface == null){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "sketchImageViewInterface is null", " - ", (name != null ? name : uri)));
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.IMAGE_VIEW_NULL);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "uri is null or empty"));
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = getFixedRecycleBitmapDrawableFromImageHolder(failureImageHolder);
            if(failureDrawable != null){
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Sketch.isDebugMode()){
                Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "unknown uri scheme: ", uri, " - ", (name != null ? name : uri)));
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = getFixedRecycleBitmapDrawableFromImageHolder(failureImageHolder);
            if(failureDrawable != null){
                sketchImageViewInterface.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String memoryCacheId = this.memoryCacheId !=null? this.memoryCacheId : generateMemoryCacheId(uri, maxSize, resize, imagesOfLowQuality, imageProcessor);
        if(name == null){
            name = memoryCacheId;
        }
        if(enableMemoryCache){
            Drawable cacheDrawable = sketch.getConfiguration().getMemoryCache().get(memoryCacheId);
            if(cacheDrawable != null){
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if(!recycleDrawable.isRecycled()){
                    if(Sketch.isDebugMode()){
                        Log.i(Sketch.TAG, SketchUtils.concat(NAME, " - ", "from memory get bitmap", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                    if(sketchImageViewInterface != null){
                        sketchImageViewInterface.setDisplayRequest(null);
                    }
                    sketchImageViewInterface.setImageDrawable(cacheDrawable);
                    if(displayListener != null){
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    }
                    sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                    return null;
                }else{
                    sketch.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(Sketch.isDebugMode()){
                        Log.e(Sketch.TAG, SketchUtils.concat(NAME, " - ", "bitmap recycled", " - ", recycleDrawable.getInfo(), " - ", name));
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if(requestLevel == RequestLevel.MEMORY){
            Drawable loadingDrawable = getFixedRecycleBitmapDrawableFromImageHolder(loadingImageHolder);
            sketchImageViewInterface.clearAnimation();
            sketchImageViewInterface.setImageDrawable(loadingDrawable);
            if(displayListener != null){
                displayListener.onCanceled(requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? CancelCause.PAUSE_LOAD :CancelCause.LEVEL_IS_MEMORY);
                if(Sketch.isDebugMode()){
                    Log.w(Sketch.TAG, SketchUtils.concat(NAME, " - ", "canceled", " - ", (requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? "pause load" : "requestLevel is memory"), " - ", name));
                }
            }
            if(sketchImageViewInterface != null){
                sketchImageViewInterface.setDisplayRequest(null);
            }
            return null;
        }

        // 试图取消已经存在的请求
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.getDisplayRequestBySketchImageInterface(sketchImageViewInterface);
        if(potentialRequest != null && !potentialRequest.isFinished()){
            if(memoryCacheId.equals(potentialRequest.getMemoryCacheId())){
                if(Sketch.isDebugMode()){
                    Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "don't need to cancel", "；", "ImageViewCode", "=", Integer.toHexString(sketchImageViewInterface.hashCode()), "；", potentialRequest.getName()));
                }
                sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                return potentialRequest;
            }else{
                potentialRequest.cancel();
            }
        }

        // 组织请求
        final DisplayRequest request = sketch.getConfiguration().getRequestFactory().newDisplayRequest(sketch, uri, uriScheme, memoryCacheId, sketchImageViewInterface);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setEnableDiskCache(enableDiskCache);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setImagesOfLowQuality(imagesOfLowQuality);
        request.setImageProcessor(imageProcessor);
        request.setDecodeGifImage(decodeGifImage);

        request.setFixedSize(fixedSize);
        request.setImageDisplayer(imageDisplayer);
        request.setDisplayListener(displayListener);
        request.setEnableMemoryCache(enableMemoryCache);
        request.setFailureImageHolder(failureImageHolder);
        request.setPauseDownloadImageHolder(pauseDownloadImageHolder);

        // 显示默认图片
        sketchImageViewInterface.setImageDrawable(getBindFixedRecycleBitmapDrawableFromImageHolder(loadingImageHolder, request));

        if(sketchImageViewInterface != null){
            sketchImageViewInterface.setDisplayRequest(request);
        }

        // 分发请求
        request.postRunDispatch();
        sketch.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    @Override
    public String generateMemoryCacheId(String uri, MaxSize maxSize, Resize resize, boolean imagesOfLowQuality, ImageProcessor imageProcessor){
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        if(maxSize != null){
            builder.append("_");
            maxSize.appendIdentifier(builder);
        }
        if(resize != null){
            builder.append("_");
            resize.appendIdentifier(builder);
        }
        if(imagesOfLowQuality){
            builder.append("_LowQuality");
        }
        if(imageProcessor != null){
            builder.append("_");
            imageProcessor.appendIdentifier(builder);
        }
        return builder.toString();
    }

    private Drawable getFixedRecycleBitmapDrawableFromImageHolder(ImageHolder imageHolder){
        if(imageHolder == null){
            return null;
        }
        FixedSize tempFixedSize = imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null?fixedSize:null;
        return imageHolder.getFixedRecycleBitmapDrawable(context, tempFixedSize);
    }

    private Drawable getBindFixedRecycleBitmapDrawableFromImageHolder(ImageHolder imageHolder, DisplayRequest request){
        if(imageHolder == null){
            return new BindFixedRecycleBitmapDrawable(null, request);
        }
        FixedSize tempFixedSize = imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer && fixedSize != null?fixedSize:null;
        return imageHolder.getBindFixedRecycleBitmapDrawable(context, tempFixedSize, request);
    }
}
