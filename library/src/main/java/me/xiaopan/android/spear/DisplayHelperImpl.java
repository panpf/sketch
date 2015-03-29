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

package me.xiaopan.android.spear;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.display.TransitionImageDisplayer;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.AsyncDrawable;
import me.xiaopan.android.spear.util.DrawableHolder;
import me.xiaopan.android.spear.util.ImageSize;

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
    protected RequestHandleLevel requestHandleLevel = RequestHandleLevel.NET;
    protected ImageSize maxsize;
    protected ImageSize resize;
    protected ImageProcessor imageProcessor;
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

    protected boolean levelFromPauseDownload;
    protected boolean levelFromPauseLoad;

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

    @Override
    public DisplayHelperImpl init(Spear spear, String uri, ImageView imageView){
        this.spear = spear;
        this.uri = uri;
        this.imageView = imageView;
        if(spear.getConfiguration().isPauseDownload()){
            this.requestHandleLevel = RequestHandleLevel.LOCAL;
            levelFromPauseDownload = true;
        }
        if(spear.getConfiguration().isPauseLoad()){
            this.requestHandleLevel = RequestHandleLevel.MEMORY;
            levelFromPauseDownload = false;
            levelFromPauseLoad = true;
        }

        if(imageView != null){
            // 根据ImageView的宽高计算maxsize，如果没有计算出合适的maxsize，就获取默认maxsize
            this.maxsize = spear.getConfiguration().getImageSizeCalculator().calculateImageMaxsize(imageView);
            if(this.maxsize == null){
                this.maxsize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxsize(spear.getConfiguration().getContext());
            }

            this.scaleType = imageView.getScaleType();
        }

        if(imageView instanceof SpearImageView){
            SpearImageView spearImageView = (SpearImageView) imageView;
            this.displayListener = spearImageView.getDisplayListener(levelFromPauseDownload);
            this.progressListener = spearImageView.getProgressListener();
            options(spearImageView.getDisplayOptions());
            spearImageView.setImageUri(uri);
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

        requestHandleLevel = RequestHandleLevel.NET;
        maxsize = null;
        resize = null;
        imageProcessor = null;
        scaleType = null;

        memoryCacheId = null;
        enableMemoryCache = true;
        imageView = null;
        imageDisplayer = null;
        loadingDrawableHolder = null;
        loadFailDrawableHolder = null;
        pauseDownloadDrawableHolder = null;
        displayListener = null;

        levelFromPauseDownload = false;
        levelFromPauseLoad = false;
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
    public DisplayHelperImpl maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    @Override
    public DisplayHelperImpl maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
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
    public DisplayHelperImpl level(RequestHandleLevel requestHandleLevel){
        if(requestHandleLevel != null){
            this.requestHandleLevel = requestHandleLevel;
            levelFromPauseDownload = false;
            levelFromPauseLoad = false;
        }
        return this;
    }

    @Override
    public DisplayHelperImpl options(DisplayOptions options){
        if(options == null){
            return this;
        }

        if(!options.isEnableDiskCache()){
            this.enableDiskCache = false;
        }
        if(!options.isEnableMemoryCache()){
            this.enableMemoryCache = false;
        }
        if(this.maxsize == null || (options.getMaxsize() != null && spear.getConfiguration().getImageSizeCalculator().compareMaxsize(options.getMaxsize(), this.maxsize) < 0)){
            this.maxsize = options.getMaxsize();
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
        if(imageProcessor == null && resize!=null){
            imageProcessor = spear.getConfiguration().getDefaultCutImageProcessor();
        }

        spear.getConfiguration().getDisplayCallbackHandler().startCallbackOnFire(displayListener);

        // 验证imageView参数
        if(imageView == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "uri is null or empty");
            }
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(null, null, FailCause.IMAGE_VIEW_NULL, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "unknown uri scheme" + " - " + "URI=" + uri);
            }
            BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(imageView, loadingBitmapDrawable, FailCause.URI_NULL_OR_EMPTY, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            if(imageView instanceof SpearImageView){
                ((SpearImageView) imageView).setDisplayRequest(null);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "未知的协议类型" + " URI" + "=" + uri);
            }
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(imageView, getDrawableFromDrawableHolder(loadFailDrawableHolder), FailCause.URI_NO_SUPPORT, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            if(imageView instanceof SpearImageView){
                ((SpearImageView) imageView).setDisplayRequest(null);
            }
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String memoryCacheId = this.memoryCacheId !=null? this.memoryCacheId : generateMemoryCacheId(uri, maxsize, resize, scaleType, imageProcessor);
        if(enableMemoryCache){
            BitmapDrawable cacheDrawable = spear.getConfiguration().getMemoryCache().get(memoryCacheId);
            if(cacheDrawable != null){
                if(!cacheDrawable.getBitmap().isRecycled()){
                    if(imageView instanceof SpearImageView){
                        ((SpearImageView) imageView).setDisplayRequest(null);
                    }
                    spear.getConfiguration().getDisplayCallbackHandler().completeCallbackOnFire(imageView, cacheDrawable, displayListener, ImageFrom.MEMORY_CACHE);
                    spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
                    if(Spear.isDebugMode()){
                        Log.d(Spear.TAG, NAME + " - " + "from memory get bitmap@"+Integer.toHexString(cacheDrawable.getBitmap().hashCode()));
                    }
                    return null;
                }else{
                    spear.getConfiguration().getMemoryCache().remove(memoryCacheId);
                    if(Spear.isDebugMode()){
                        Log.e(Spear.TAG, NAME + " - " + "bitmap@" + Integer.toHexString(cacheDrawable.getBitmap().hashCode()) + " - 已被回收");
                    }
                }
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if(requestHandleLevel == RequestHandleLevel.MEMORY){
            BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
            imageView.clearAnimation();
            imageView.setImageDrawable(loadingBitmapDrawable);
            if(displayListener != null){
                displayListener.onCanceled(levelFromPauseLoad ?CancelCause.PAUSE_LOAD :CancelCause.LEVEL_IS_MEMORY);
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "canceled" + " - " + (levelFromPauseLoad?"pause load":"level is memory") + " - " + name);
                }
            }
            if(imageView instanceof SpearImageView){
                ((SpearImageView) imageView).setDisplayRequest(null);
            }
            return null;
        }

        // 试图取消已经存在的请求
        DisplayRequest potentialRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
        if(potentialRequest != null && !potentialRequest.isFinished()){
            if(memoryCacheId.equals(potentialRequest.getMemoryCacheId())){
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "无需取消" + "；" + "ImageViewCode" + "=" + imageView.hashCode() + "；" + potentialRequest.getName());
                }
                spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
                return potentialRequest;
            }else{
                potentialRequest.cancel();
            }
        }

        // 组织请求
        final DisplayRequest request = spear.getConfiguration().getRequestFactory().newDisplayRequest(spear, uri, uriScheme, memoryCacheId, imageView);

        request.setName(name != null ? name : memoryCacheId);

        request.setEnableDiskCache(enableDiskCache);
        request.setProgressListener(progressListener);

        request.setRequestHandleLevel(requestHandleLevel);
        request.setMaxsize(maxsize);
        request.setResize(resize);
        request.setImageProcessor(imageProcessor);
        request.setScaleType(scaleType);

        request.setEnableMemoryCache(enableMemoryCache);
        request.setImageDisplayer(imageDisplayer);
        request.setLoadFailDrawableHolder(loadFailDrawableHolder);
        request.setPauseDownloadDrawableHolder(pauseDownloadDrawableHolder);
        request.setDisplayListener(displayListener);
        request.setLevelFromPauseDownload(true);

        // 显示默认图片
        BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
        imageView.clearAnimation();
        imageView.setImageDrawable(new AsyncDrawable(spear.getConfiguration().getContext().getResources(), loadingBitmapDrawable != null ? loadingBitmapDrawable.getBitmap() : null, request));

        // 分发请求
        request.postRunDispatch();

        spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);

        if(imageView instanceof SpearImageView){
            ((SpearImageView) imageView).setDisplayRequest(request);
        }
        return request;
    }

    @Override
    public String generateMemoryCacheId(String uri, ImageSize maxsize, ImageSize resize, ImageView.ScaleType scaleType, ImageProcessor imageProcessor){
        StringBuilder stringBuilder = new StringBuilder(uri);
        if(maxsize != null){
            stringBuilder.append("_");
            stringBuilder.append(maxsize.getWidth());
            stringBuilder.append("x");
            stringBuilder.append(maxsize.getHeight());
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
