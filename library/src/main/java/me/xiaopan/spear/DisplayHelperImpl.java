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

package me.xiaopan.spear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.spear.display.ImageDisplayer;
import me.xiaopan.spear.display.TransitionImageDisplayer;
import me.xiaopan.spear.process.ImageProcessor;

/**
 * DisplayHelper
 */
public class DisplayHelperImpl implements DisplayHelper{
    private static final String NAME = "DisplayHelperImpl";

    // 基本属性
    protected Spear spear;
    protected String uri;
    protected String name;
    protected RequestLevel requestLevel = RequestLevel.NET;
    protected RequestLevelFrom requestLevelFrom;

    // 下载属性
    protected boolean enableDiskCache = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected boolean decodeGifImage = true;
    protected ImageSize maxSize;
    protected ImageSize resize;
    protected ImageProcessor imageProcessor;
    protected ImageView.ScaleType scaleType;

    // 显示属性
    protected String memoryCacheId;
    protected boolean enableMemoryCache = true;
    protected ImageView imageView;
    protected ImageDisplayer imageDisplayer;
    protected ImageHolder loadingImageHolder;
    protected ImageHolder failureImageHolder;
    protected ImageHolder pauseDownloadImageHolder;
    protected DisplayListener displayListener;

    protected Context context;
    protected SpearImageViewInterface spearImageViewInterface;

    /**
     * 创建显示请求生成器
     * @param spear Spear
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 图片View
     */
    public DisplayHelperImpl(Spear spear, String uri, ImageView imageView) {
        init(spear, uri, imageView);
    }

    /**
     * 创建显示请求生成器
     * @param spear Spear
     * @param displayParams 参数集
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @param imageView 图片View
     */
    public DisplayHelperImpl(Spear spear, DisplayParams displayParams, ImageView imageView) {
        init(spear, displayParams, imageView);
    }

    @Override
    public DisplayHelperImpl init(Spear spear, String uri, ImageView imageView){
        this.context = spear.getConfiguration().getContext();

        this.spear = spear;
        this.uri = uri;
        this.imageView = imageView;
        if(spear.getConfiguration().isPauseDownload()){
            this.requestLevel = RequestLevel.LOCAL;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
        }
        if(spear.getConfiguration().isPauseLoad()){
            this.requestLevel = RequestLevel.MEMORY;
            this.requestLevelFrom = RequestLevelFrom.PAUSE_LOAD;
        }

        if(imageView != null){
            this.maxSize = spear.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(imageView);
            if(this.maxSize == null){
                this.maxSize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(context);
            }
            this.scaleType = imageView.getScaleType();

            if(imageView instanceof SpearImageViewInterface){
                spearImageViewInterface = (SpearImageViewInterface) imageView;
                spearImageViewInterface.onDisplay();
                this.displayListener = spearImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
                this.progressListener = spearImageViewInterface.getProgressListener();
                options(spearImageViewInterface.getDisplayOptions());
            }
        }

        return this;
    }

    @Override
    public DisplayHelper init(Spear spear, DisplayParams displayParams, ImageView imageView) {
        this.context = spear.getConfiguration().getContext();

        this.spear = spear;
        this.uri = displayParams.uri;
        this.name = displayParams.name;
        this.requestLevel = displayParams.requestLevel;
        this.requestLevelFrom = displayParams.requestLevelFrom;

        this.enableDiskCache = displayParams.enableDiskCache;
        this.progressListener = displayParams.progressListener;

        this.resize = displayParams.resize;
        this.maxSize = displayParams.maxSize;
        this.scaleType = displayParams.scaleType;
        this.requestLevel = displayParams.requestLevel;
        this.imageProcessor = displayParams.imageProcessor;
        this.decodeGifImage = displayParams.decodeGifImage;

        this.imageView = imageView;
        this.memoryCacheId = displayParams.memoryCacheId;
        this.enableMemoryCache = displayParams.enableMemoryCache;
        this.imageDisplayer = displayParams.imageDisplayer;
        this.loadingImageHolder = displayParams.loadingImageHolder;
        this.failureImageHolder = displayParams.loadFailImageHolder;
        this.pauseDownloadImageHolder = displayParams.pauseDownloadImageHolder;
        this.displayListener = displayParams.displayListener;

        if(requestLevelFrom != null){
            if(spear.getConfiguration().isPauseDownload()){
                this.requestLevel = RequestLevel.LOCAL;
                this.requestLevelFrom = RequestLevelFrom.PAUSE_DOWNLOAD;
            }
            if(spear.getConfiguration().isPauseLoad()){
                this.requestLevel = RequestLevel.MEMORY;
                this.requestLevelFrom = RequestLevelFrom.PAUSE_LOAD;
            }
        }

        if(imageView != null){
            // 根据ImageView的宽高计算maxSize，如果没有计算出合适的maxSize，就获取默认的maxSize
            this.maxSize = spear.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(imageView);
            if(this.maxSize == null){
                this.maxSize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(context);
            }

            this.scaleType = imageView.getScaleType();
        }

        if(imageView instanceof SpearImageViewInterface){
            spearImageViewInterface = (SpearImageViewInterface) imageView;
            spearImageViewInterface.onDisplay();
            this.displayListener = spearImageViewInterface.getDisplayListener(requestLevelFrom == RequestLevelFrom.PAUSE_DOWNLOAD);
            this.progressListener = spearImageViewInterface.getProgressListener();
            options(spearImageViewInterface.getDisplayOptions());
        }

        return this;
    }

    @Override
    public void reset(){
        spear = null;
        uri = null;
        name = null;
        requestLevel = RequestLevel.NET;
        requestLevelFrom = null;

        enableDiskCache = true;
        progressListener = null;

        resize = null;
        maxSize = null;
        scaleType = null;
        imageProcessor = null;
        decodeGifImage = true;

        memoryCacheId = null;
        enableMemoryCache = true;
        imageView = null;
        imageDisplayer = null;
        loadingImageHolder = null;
        failureImageHolder = null;
        pauseDownloadImageHolder = null;
        displayListener = null;

        spearImageViewInterface = null;
    }

    @Override
    public void fullDisplayParams(){
        if(spearImageViewInterface != null){
            DisplayParams displayParams = spearImageViewInterface.getDisplayParams();
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
            displayParams.scaleType = scaleType;
            displayParams.imageProcessor = imageProcessor;
            displayParams.decodeGifImage = decodeGifImage;

            displayParams.memoryCacheId = memoryCacheId;
            displayParams.enableMemoryCache = enableMemoryCache;
            displayParams.imageDisplayer = imageDisplayer;
            displayParams.loadingImageHolder = loadingImageHolder;
            displayParams.loadFailImageHolder = failureImageHolder;
            displayParams.pauseDownloadImageHolder = pauseDownloadImageHolder;
            displayParams.displayListener = displayListener;

            spearImageViewInterface.setDisplayParams(displayParams);
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
    public DisplayHelperImpl maxSize(ImageSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    @Override
    public DisplayHelperImpl maxSize(int width, int height){
        this.maxSize = new ImageSize(width, height);
        return this;
    }

    @Override
    public DisplayHelperImpl resize(ImageSize resize){
        this.resize = resize;
        return this;
    }

    @Override
    public DisplayHelperImpl resize(int width, int height){
        this.resize = new ImageSize(width, height);
        return this;
    }

    @Override
    public DisplayHelperImpl resizeByImageViewLayoutSize(){
        this.resize = spear.getConfiguration().getImageSizeCalculator().calculateImageResize(imageView);
        return this;
    }

    @Override
    public DisplayHelperImpl processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    @Override
    public DisplayHelperImpl scaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
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
        if(this.imageDisplayer != null && this.imageDisplayer instanceof TransitionImageDisplayer && this.resize == null){
            resizeByImageViewLayoutSize();
        }
        return this;
    }

    @Override
    public DisplayHelperImpl loadingImage(ImageHolder loadingImage) {
        if(this.loadingImageHolder != null){
            this.loadingImageHolder.recycle();
        }
        this.loadingImageHolder = loadingImage;
        return this;
    }

    @Override
    public DisplayHelperImpl loadingImage(int drawableResId) {
        loadingImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl loadingImage(int drawableResId, ImageProcessor imageProcessor) {
        loadingImage(new ImageHolder(drawableResId, imageProcessor));
        return this;
    }

    @Override
    public DisplayHelperImpl failureImage(ImageHolder failureImage) {
        if(this.failureImageHolder != null){
            this.failureImageHolder.recycle();
        }
        this.failureImageHolder = failureImage;
        return this;
    }

    @Override
    public DisplayHelperImpl failureImage(int drawableResId) {
        failureImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl failureImage(int drawableResId, ImageProcessor imageProcessor) {
        failureImage(new ImageHolder(drawableResId, imageProcessor));
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadImage(ImageHolder pauseDownloadImage) {
        if(this.pauseDownloadImageHolder != null){
            this.pauseDownloadImageHolder.recycle();
        }
        this.pauseDownloadImageHolder = pauseDownloadImage;
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadImage(int drawableResId) {
        pauseDownloadImage(new ImageHolder(drawableResId));
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadImage(int drawableResId, ImageProcessor imageProcessor) {
        pauseDownloadImage(new ImageHolder(drawableResId, imageProcessor));
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
        if(this.maxSize == null || (options.getMaxSize() != null && spear.getConfiguration().getImageSizeCalculator().compareMaxSize(options.getMaxSize(), this.maxSize) < 0)){
            this.maxSize = options.getMaxSize();
        }
        if(this.resize == null){
            this.resize = options.getResize();
        }
        if(options.isResizeByImageViewLayoutSize()){
            resizeByImageViewLayoutSize();
        }
        if(this.scaleType == null){
            this.scaleType = options.getScaleType();
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
        return options((DisplayOptions) Spear.getOptions(optionsName));
    }

    @Override
    public Request fire() {
        fullDisplayParams();

        if(imageProcessor == null && resize != null){
            imageProcessor = spear.getConfiguration().getDefaultCutImageProcessor();
        }
        if(name == null && memoryCacheId != null){
            name = memoryCacheId;
        }

        if(displayListener != null){
            displayListener.onStarted();
        }

        // 验证imageView参数
        if(imageView == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "ImageView is null" + " - " + (name!=null?name:uri));
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.IMAGE_VIEW_NULL);
            }
            spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "uri is null or empty");
            }
            if(spearImageViewInterface != null){
                spearImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = getDrawableFromDrawableHolder(failureImageHolder);
            if(failureDrawable != null){
                imageView.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "unknown uri scheme: " + uri + " - " + (name!=null?name:uri));
            }
            if(spearImageViewInterface != null){
                spearImageViewInterface.setDisplayRequest(null);
            }
            Drawable failureDrawable = getDrawableFromDrawableHolder(failureImageHolder);
            if(failureDrawable != null){
                imageView.setImageDrawable(failureDrawable);
            }
            if(displayListener != null){
                displayListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String memoryCacheId = this.memoryCacheId !=null? this.memoryCacheId : generateMemoryCacheId(uri, maxSize, resize, scaleType, imageProcessor);
        if(name == null){
            name = memoryCacheId;
        }
        if(enableMemoryCache){
            Drawable cacheDrawable = spear.getConfiguration().getMemoryCache().get(memoryCacheId);
            if(cacheDrawable != null){
                RecycleDrawableInterface recycleDrawable = (RecycleDrawableInterface) cacheDrawable;
                if(!recycleDrawable.isRecycled()){
                    if(Spear.isDebugMode()){
                        Log.d(Spear.TAG, NAME + " - " + "from memory get bitmap@" + recycleDrawable.getHashCodeByLog() + " - " + name);
                    }
                    if(spearImageViewInterface != null){
                        spearImageViewInterface.setDisplayRequest(null);
                    }
                    imageView.setImageDrawable(cacheDrawable);
                    if(displayListener != null){
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE, recycleDrawable.getMimeType());
                    }
                    spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                    return null;
                }else{
                    spear.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(Spear.isDebugMode()){
                        Log.e(Spear.TAG, NAME + " - " + "bitmap recycled@" + recycleDrawable.getHashCodeByLog() + " - " + name);
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if(requestLevel == RequestLevel.MEMORY){
            Drawable loadingDrawable = getDrawableFromDrawableHolder(loadingImageHolder);
            imageView.clearAnimation();
            imageView.setImageDrawable(loadingDrawable);
            if(displayListener != null){
                displayListener.onCanceled(requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? CancelCause.PAUSE_LOAD :CancelCause.LEVEL_IS_MEMORY);
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "canceled" + " - " + (requestLevelFrom == RequestLevelFrom.PAUSE_LOAD ? "pause load":"requestLevel is memory") + " - " + name);
                }
            }
            if(spearImageViewInterface != null){
                spearImageViewInterface.setDisplayRequest(null);
            }
            return null;
        }

        // 试图取消已经存在的请求
        DisplayRequest potentialRequest = BindBitmapDrawable.getDisplayRequestByImageView(imageView);
        if(potentialRequest != null && !potentialRequest.isFinished()){
            if(memoryCacheId.equals(potentialRequest.getMemoryCacheId())){
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "don't need to cancel" + "；" + "ImageViewCode" + "=" + imageView.hashCode() + "；" + potentialRequest.getName());
                }
                spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
                return potentialRequest;
            }else{
                potentialRequest.cancel();
            }
        }

        // 组织请求
        final DisplayRequest request = spear.getConfiguration().getRequestFactory().newDisplayRequest(spear, uri, uriScheme, memoryCacheId, imageView);

        request.setName(name);
        request.setRequestLevel(requestLevel);
        request.setRequestLevelFrom(requestLevelFrom);

        request.setEnableDiskCache(enableDiskCache);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setScaleType(scaleType);
        request.setImageProcessor(imageProcessor);
        request.setDecodeGifImage(decodeGifImage);

        request.setImageDisplayer(imageDisplayer);
        request.setDisplayListener(displayListener);
        request.setEnableMemoryCache(enableMemoryCache);
        request.setFailureImageHolder(failureImageHolder);
        request.setPauseDownloadImageHolder(pauseDownloadImageHolder);

        // 显示默认图片
        Bitmap loadingBitmap = loadingImageHolder !=null? loadingImageHolder.getBitmap(context):null;
        BindBitmapDrawable bindBitmapDrawable = new BindBitmapDrawable(loadingBitmap, request);
        if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer){
            if(resize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
                bindBitmapDrawable.setFixedSize(resize.getWidth(), resize.getHeight());
            }
        }
        imageView.setImageDrawable(bindBitmapDrawable);

        if(spearImageViewInterface != null){
            spearImageViewInterface.setDisplayRequest(request);
        }

        // 分发请求
        request.postRunDispatch();
        spear.getConfiguration().getHelperFactory().recycleDisplayHelper(this);
        return request;
    }

    @Override
    public String generateMemoryCacheId(String uri, ImageSize maxSize, ImageSize resize, ImageView.ScaleType scaleType, ImageProcessor imageProcessor){
        StringBuilder stringBuilder = new StringBuilder(uri);
        if(maxSize != null){
            stringBuilder.append("_");
            stringBuilder.append(maxSize.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(maxSize.getHeight());
        }
        if(resize != null){
            stringBuilder.append("_");
            stringBuilder.append(resize.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(resize.getHeight());
        }
        if(scaleType != null){
            stringBuilder.append("_");
            stringBuilder.append(scaleType.name());
        }
        if(imageProcessor != null){
            stringBuilder.append("_");
            stringBuilder.append(imageProcessor.getFlag());
        }
        return stringBuilder.toString();
    }

    private Drawable getDrawableFromDrawableHolder(ImageHolder imageHolder){
        if(imageHolder != null){
            Bitmap bitmap = imageHolder.getBitmap(context);
            if(bitmap != null){
                SrcBitmapDrawable srcBitmapDrawable = new SrcBitmapDrawable(bitmap);
                if(imageDisplayer != null && imageDisplayer instanceof TransitionImageDisplayer){
                    if(resize != null && scaleType == ImageView.ScaleType.CENTER_CROP){
                        srcBitmapDrawable.setFixedSize(resize.getWidth(), resize.getHeight());
                    }
                }
                return srcBitmapDrawable;
            }
        }

        return null;
    }
}
