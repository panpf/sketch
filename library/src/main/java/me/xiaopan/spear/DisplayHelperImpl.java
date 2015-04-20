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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

    // 下载属性
    protected boolean enableDiskCache = true;
    protected ProgressListener progressListener;

    // 加载属性
    protected boolean disableGifImage;
    protected ImageSize maxSize;
    protected ImageSize resize;
    protected ImageProcessor imageProcessor;
    protected HandleLevel handleLevel = HandleLevel.NET;
    protected ImageView.ScaleType scaleType;

    // 显示属性
    protected String memoryCacheId;
    protected boolean enableMemoryCache = true;
    protected ImageView imageView;
    protected ImageDisplayer imageDisplayer;
    protected DrawableHolder loadingDrawableHolder;
    protected DrawableHolder loadFailDrawableHolder;
    protected DrawableHolder pauseDownloadDrawableHolder;
    protected DisplayListener displayListener;

    protected boolean handleLevelFromPauseDownload;
    protected boolean handleLevelFromPauseLoad;
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
        this.spear = spear;
        this.uri = uri;
        this.imageView = imageView;
        if(spear.getConfiguration().isPauseDownload()){
            this.handleLevel = HandleLevel.LOCAL;
            handleLevelFromPauseDownload = true;
        }
        if(spear.getConfiguration().isPauseLoad()){
            this.handleLevel = HandleLevel.MEMORY;
            handleLevelFromPauseDownload = false;
            handleLevelFromPauseLoad = true;
        }

        if(imageView != null){
            this.maxSize = spear.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(imageView);
            if(this.maxSize == null){
                this.maxSize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(spear.getConfiguration().getContext());
            }
            this.scaleType = imageView.getScaleType();

            if(imageView instanceof SpearImageViewInterface){
                spearImageViewInterface = (SpearImageViewInterface) imageView;
                spearImageViewInterface.onDisplay();
                this.displayListener = spearImageViewInterface.getDisplayListener(handleLevelFromPauseDownload);
                this.progressListener = spearImageViewInterface.getProgressListener();
                options(spearImageViewInterface.getDisplayOptions());
            }
        }

        return this;
    }

    @Override
    public DisplayHelper init(Spear spear, DisplayParams displayParams, ImageView imageView) {
        this.spear = spear;
        this.uri = displayParams.uri;
        this.name = displayParams.name;

        this.enableDiskCache = displayParams.enableDiskCache;
        this.progressListener = displayParams.progressListener;

        this.resize = displayParams.resize;
        this.maxSize = displayParams.maxSize;
        this.scaleType = displayParams.scaleType;
        this.handleLevel = displayParams.handleLevel;
        this.imageProcessor = displayParams.imageProcessor;
        this.disableGifImage = displayParams.disableGifImage;

        this.imageView = imageView;
        this.memoryCacheId = displayParams.memoryCacheId;
        this.enableMemoryCache = displayParams.enableMemoryCache;
        this.imageDisplayer = displayParams.imageDisplayer;
        this.loadingDrawableHolder = displayParams.loadingDrawableHolder;
        this.loadFailDrawableHolder = displayParams.loadFailDrawableHolder;
        this.pauseDownloadDrawableHolder = displayParams.pauseDownloadDrawableHolder;
        this.displayListener = displayParams.displayListener;

        if(spear.getConfiguration().isPauseDownload()){
            this.handleLevel = HandleLevel.LOCAL;
            handleLevelFromPauseDownload = true;
        }
        if(spear.getConfiguration().isPauseLoad()){
            this.handleLevel = HandleLevel.MEMORY;
            handleLevelFromPauseDownload = false;
            handleLevelFromPauseLoad = true;
        }

        if(imageView != null){
            // 根据ImageView的宽高计算maxSize，如果没有计算出合适的maxSize，就获取默认的maxSize
            this.maxSize = spear.getConfiguration().getImageSizeCalculator().calculateImageMaxSize(imageView);
            if(this.maxSize == null){
                this.maxSize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxSize(spear.getConfiguration().getContext());
            }

            this.scaleType = imageView.getScaleType();
        }

        if(imageView instanceof SpearImageViewInterface){
            spearImageViewInterface = (SpearImageViewInterface) imageView;
            spearImageViewInterface.onDisplay();
            this.displayListener = spearImageViewInterface.getDisplayListener(handleLevelFromPauseDownload);
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

        enableDiskCache = true;
        progressListener = null;

        resize = null;
        maxSize = null;
        scaleType = null;
        handleLevel = HandleLevel.NET;
        imageProcessor = null;
        disableGifImage = false;

        memoryCacheId = null;
        enableMemoryCache = true;
        imageView = null;
        imageDisplayer = null;
        loadingDrawableHolder = null;
        loadFailDrawableHolder = null;
        pauseDownloadDrawableHolder = null;
        displayListener = null;

        handleLevelFromPauseDownload = false;
        handleLevelFromPauseLoad = false;
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

            displayParams.enableDiskCache = enableDiskCache;
            displayParams.progressListener = progressListener;

            displayParams.resize = resize;
            displayParams.maxSize = maxSize;
            displayParams.scaleType = scaleType;
            displayParams.handleLevel = handleLevel;
            displayParams.imageProcessor = imageProcessor;
            displayParams.disableGifImage = disableGifImage;

            displayParams.memoryCacheId = memoryCacheId;
            displayParams.enableMemoryCache = enableMemoryCache;
            displayParams.imageDisplayer = imageDisplayer;
            displayParams.loadingDrawableHolder = loadingDrawableHolder;
            displayParams.loadFailDrawableHolder = loadFailDrawableHolder;
            displayParams.pauseDownloadDrawableHolder = pauseDownloadDrawableHolder;
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
    public DisplayHelperImpl disableGifImage() {
        this.disableGifImage = true;
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
    public DisplayHelperImpl loadingDrawable(int drawableResId) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        return this;
    }

    @Override
    public DisplayHelperImpl loadingDrawable(int drawableResId, boolean isProcess) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        loadingDrawableHolder.setProcess(isProcess);
        return this;
    }

    @Override
    public DisplayHelperImpl loadFailDrawable(int drawableResId) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        loadFailDrawableHolder.setProcess(false);
        return this;
    }

    @Override
    public DisplayHelperImpl loadFailDrawable(int drawableResId, boolean isProcess) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        loadFailDrawableHolder.setProcess(isProcess);
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadDrawable(int drawableResId) {
        if(pauseDownloadDrawableHolder == null){
            pauseDownloadDrawableHolder = new DrawableHolder();
        }
        pauseDownloadDrawableHolder.setResId(drawableResId);
        pauseDownloadDrawableHolder.setProcess(false);
        return this;
    }

    @Override
    public DisplayHelperImpl pauseDownloadDrawable(int drawableResId, boolean isProcess) {
        if(pauseDownloadDrawableHolder == null){
            pauseDownloadDrawableHolder = new DrawableHolder();
        }
        pauseDownloadDrawableHolder.setResId(drawableResId);
        pauseDownloadDrawableHolder.setProcess(isProcess);
        return this;
    }

    @Override
    public DisplayHelperImpl progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public DisplayHelperImpl handleLevel(HandleLevel handleLevel){
        if(handleLevel != null){
            this.handleLevel = handleLevel;
            handleLevelFromPauseDownload = false;
            handleLevelFromPauseLoad = false;
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
        this.disableGifImage = options.isDisableGifImage();
        if(this.loadingDrawableHolder == null){
            this.loadingDrawableHolder = options.getLoadingDrawableHolder();
        }
        if(this.loadFailDrawableHolder == null){
            this.loadFailDrawableHolder = options.getLoadFailDrawableHolder();
        }
        if(this.pauseDownloadDrawableHolder == null){
            this.pauseDownloadDrawableHolder = options.getPauseDownloadDrawableHolder();
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

        if(imageProcessor == null && resize!=null){
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
            Drawable failDrawable = getDrawableFromDrawableHolder(loadFailDrawableHolder);
            if(failDrawable != null){
                imageView.setImageDrawable(failDrawable);
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
            Drawable failDrawable = getDrawableFromDrawableHolder(loadFailDrawableHolder);
            if(failDrawable != null){
                imageView.setImageDrawable(failDrawable);
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
                        displayListener.onCompleted(ImageFrom.MEMORY_CACHE);
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
        if(handleLevel == HandleLevel.MEMORY){
            BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
            imageView.clearAnimation();
            imageView.setImageDrawable(loadingBitmapDrawable);
            if(displayListener != null){
                displayListener.onCanceled(handleLevelFromPauseLoad ?CancelCause.PAUSE_LOAD :CancelCause.LEVEL_IS_MEMORY);
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "canceled" + " - " + (handleLevelFromPauseLoad ?"pause load":"handleLevel is memory") + " - " + name);
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
                    Log.d(Spear.TAG, NAME + " - " + "无需取消" + "；" + "ImageViewCode" + "=" + imageView.hashCode() + "；" + potentialRequest.getName());
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

        request.setEnableDiskCache(enableDiskCache);
        request.setProgressListener(progressListener);

        request.setResize(resize);
        request.setMaxSize(maxSize);
        request.setScaleType(scaleType);
        request.setHandleLevel(handleLevel);
        request.setImageProcessor(imageProcessor);
        request.setDisableGifImage(disableGifImage);

        request.setImageDisplayer(imageDisplayer);
        request.setDisplayListener(displayListener);
        request.setEnableMemoryCache(enableMemoryCache);
        request.setLoadFailDrawableHolder(loadFailDrawableHolder);
        request.setPauseDownloadDrawableHolder(pauseDownloadDrawableHolder);
        request.setHandleLevelFromPauseDownload(true);

        // 显示默认图片
        BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
        Bitmap loadingBitmap = null;
        if(loadingBitmapDrawable != null){
            loadingBitmap = loadingBitmapDrawable.getBitmap();
        }
        imageView.setImageDrawable(new BindBitmapDrawable(spear.getConfiguration().getContext().getResources(), loadingBitmap, request));

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

    private BitmapDrawable getDrawableFromDrawableHolder(DrawableHolder drawableHolder){
        if(drawableHolder != null){
            return drawableHolder.getDrawable(spear.getConfiguration().getContext(), resize, scaleType, imageProcessor, imageDisplayer!=null&&imageDisplayer instanceof TransitionImageDisplayer);
        }else{
            return null;
        }
    }
}
