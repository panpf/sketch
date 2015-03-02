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
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayRequest;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.request.ImageFrom;
import me.xiaopan.android.spear.request.ProgressListener;
import me.xiaopan.android.spear.request.RequestFuture;
import me.xiaopan.android.spear.util.AsyncDrawable;
import me.xiaopan.android.spear.util.DrawableHolder;
import me.xiaopan.android.spear.request.FailureCause;
import me.xiaopan.android.spear.util.ImageScheme;
import me.xiaopan.android.spear.util.ImageSize;
import me.xiaopan.android.spear.util.ImageViewHolder;

/**
 * DisplayHelper
 */
public class DisplayHelper {
    private static final String NAME = "DisplayHelper";

    protected Spear spear;
    protected String uri;
    
    protected String memoryCacheId;

    protected boolean enableDiskCache = DownloadRequest.DEFAULT_ENABLE_DISK_CACHE;

    protected ImageSize maxsize;
    protected ImageSize resize;
    protected ImageProcessor imageProcessor;
    protected ImageView.ScaleType scaleType;

    protected boolean enableMemoryCache = DisplayRequest.DEFAULT_ENABLE_MEMORY_CACHE;
    protected ImageDisplayer imageDisplayer;
    protected DrawableHolder loadingDrawableHolder;
    protected DrawableHolder loadFailDrawableHolder;

    protected DisplayListener displayListener;
    protected ProgressListener progressListener;

    protected ImageView imageView;

    protected boolean resizeByImageViewLayoutSize;
    protected boolean resizeByImageViewLayoutSizeAndFromDisplayer;

    protected boolean returnRequestFuture;

    /**
     * 创建显示请求生成器
     * @param spear Spear
     * @param uri 支持以下6种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * <br>“/mnt/sdcard/image.png“ // from SD card
     * <br>“content://media/external/audio/albumart/13“ // from content provider
     * <br>“assets://image.png“ // from assets
     * <br>“drawable://" + R.drawable.image // from drawables
     * </blockquote>
     */
    public DisplayHelper(Spear spear, String uri, ImageView imageView) {
        reset(spear, uri, imageView);
    }

    /**
     * 重置
     */
    public DisplayHelper reset(Spear spear, String uri, ImageView imageView){
        this.spear = spear;
        this.uri = uri;
        this.imageView = imageView;

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
            options(spearImageView.getDisplayOptions());
            this.displayListener = spearImageView.getDisplayListener();
            this.progressListener = spearImageView.getProgressListener();
            this.returnRequestFuture = spearImageView.isReturnRequestFuture();
            spearImageView.tryResetDebugFlagAndProgressStatus();
        }

        return this;
    }

    /**
     * 恢复默认值
     */
    public void restoreDefault(){
        spear = null;
        uri = null;

        enableDiskCache = DownloadRequest.DEFAULT_ENABLE_DISK_CACHE;

        maxsize = null;
        resize = null;
        imageProcessor = null;
        scaleType = null;

        enableMemoryCache = DisplayRequest.DEFAULT_ENABLE_MEMORY_CACHE;
        imageDisplayer = null;
        loadingDrawableHolder = null;
        loadFailDrawableHolder = null;

        displayListener = null;
        progressListener = null;

        imageView = null;

        resizeByImageViewLayoutSize = false;
        resizeByImageViewLayoutSizeAndFromDisplayer = false;
    }

    /**
     * 设置内存缓存ID（大多数情况下你不需要手动设置缓存ID，除非你想使用通过putBitmap()放到缓存中的图片）
     * @param memoryCacheId 内存缓存ID
     * @return DisplayHelper
     */
    public DisplayHelper memoryCacheId(String memoryCacheId){
        this.memoryCacheId = memoryCacheId;
        return this;
    }

    /**
     * 关闭硬盘缓存
     * @return DisplayHelper
     */
    public DisplayHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxsize 最大尺寸
     * @return DisplayHelper
     */
    public DisplayHelper maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return DisplayHelper
     */
    public DisplayHelper maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param resize 新的尺寸
     * @return DisplayHelper
     */
    public DisplayHelper resize(ImageSize resize){
        this.resize = resize;
        this.resizeByImageViewLayoutSize = false;
        this.resizeByImageViewLayoutSizeAndFromDisplayer = false;
        return this;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param width 宽
     * @param height 高
     * @return DisplayHelper
     */
    public DisplayHelper resize(int width, int height){
        this.resize = new ImageSize(width, height);
        this.resizeByImageViewLayoutSize = false;
        this.resizeByImageViewLayoutSizeAndFromDisplayer = false;
        return this;
    }

    /**
     * 根据ImageView的LayoutSize裁剪图片
     */
    public DisplayHelper resizeByImageViewLayoutSize(){
        this.resize = spear.getConfiguration().getImageSizeCalculator().calculateImageResize(imageView);
        this.resizeByImageViewLayoutSize = true;
        this.resizeByImageViewLayoutSizeAndFromDisplayer = false;
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor Bitmap处理器
     * @return DisplayHelper
     */
    public DisplayHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置ScaleType，ImageProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return DisplayHelper
     */
    public DisplayHelper scaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 关闭内存缓存
     */
    public DisplayHelper disableMemoryCache() {
        this.enableMemoryCache = false;
        return this;
    }

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    public DisplayHelper listener(DisplayListener displayListener) {
        this.displayListener = displayListener;
        return this;
    }

    /**
     * 设置图片显示器，在加载完成后会调用此显示器来显示图片
     * @param displayer 图片显示器
     */
    public DisplayHelper displayer(ImageDisplayer displayer) {
        this.imageDisplayer = displayer;
        if(this.imageDisplayer != null && this.imageDisplayer instanceof TransitionImageDisplayer){
            if(!resizeByImageViewLayoutSize){
                this.resize = spear.getConfiguration().getImageSizeCalculator().calculateImageResize(imageView);
                this.resizeByImageViewLayoutSize = true;
                this.resizeByImageViewLayoutSizeAndFromDisplayer = true;
            }
        }else if(this.resizeByImageViewLayoutSizeAndFromDisplayer){
            this.resizeByImageViewLayoutSize = false;
            this.resizeByImageViewLayoutSizeAndFromDisplayer = false;
        }
        return this;
    }

    /**
     * 设置正在加载的时候显示的图片
     * @param drawableResId 正在加载的时候显示的图片
     */
    public DisplayHelper loadingDrawable(int drawableResId) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        return this;
    }

    /**
     * 设置正在加载的时候显示的图片
     * @param drawableResId 正在加载的时候显示的图片
     * @param isProcess 是否使用ImageProcessor对当前图片进行处理
     */
    public DisplayHelper loadingDrawable(int drawableResId, boolean isProcess) {
        if(loadingDrawableHolder == null){
            loadingDrawableHolder = new DrawableHolder();
        }
        loadingDrawableHolder.setResId(drawableResId);
        loadingDrawableHolder.setProcess(isProcess);
        return this;
    }

    /**
     * 设置当加载失败的时候显示的图片
     * @param drawableResId 当加载失败的时候显示的图片
     */
    public DisplayHelper loadFailDrawable(int drawableResId) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        return this;
    }

    /**
     * 设置当加载失败的时候显示的图片
     * @param drawableResId 当加载失败的时候显示的图片
     * @param isProcess 是否使用ImageProcessor对当前图片进行处理
     */
    public DisplayHelper loadFailDrawable(int drawableResId, boolean isProcess) {
        if(loadFailDrawableHolder == null){
            loadFailDrawableHolder = new DrawableHolder();
        }
        loadFailDrawableHolder.setResId(drawableResId);
        loadFailDrawableHolder.setProcess(isProcess);
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return DisplayHelper
     */
    public DisplayHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * fire之后返回RequestFuture，默认情况下fire方法返回null
     * @return DisplayHelper
     */
    public DisplayHelper returnRequestFuture(){
        this.returnRequestFuture = true;
        return this;
    }

    /**
     * 设置显示参数
     * @param options 显示参数
     * @return DisplayHelper
     */
    public DisplayHelper options(DisplayOptions options){
        if(options == null){
            return this;
        }

        if(options.isEnableDiskCache() != DownloadRequest.DEFAULT_ENABLE_DISK_CACHE){
            this.enableDiskCache = options.isEnableDiskCache();
        }
        if(options.isEnableMemoryCache() != DisplayRequest.DEFAULT_ENABLE_MEMORY_CACHE){
            this.enableMemoryCache = options.isEnableMemoryCache();
        }
        if(this.maxsize == null || (options.getMaxsize() != null && spear.getConfiguration().getImageSizeCalculator().compareMaxsize(options.getMaxsize(), this.maxsize) < 0)){
            this.maxsize = options.getMaxsize();
        }
        if(!resizeByImageViewLayoutSize || this.resize == null){
            if(options.isResizeByImageViewLayoutSize()){
                this.resize = spear.getConfiguration().getImageSizeCalculator().calculateImageResize(imageView);
                this.resizeByImageViewLayoutSize = options.isResizeByImageViewLayoutSize();
                this.resizeByImageViewLayoutSizeAndFromDisplayer = options.isResizeByImageViewLayoutSizeFromDisplayer();
            }else if(this.resize == null && options.getResize() != null){
                this.resize = options.getResize();
                this.resizeByImageViewLayoutSize = false;
                this.resizeByImageViewLayoutSizeAndFromDisplayer = false;
            }
        }
        if(this.scaleType == null){
            this.scaleType = options.getScaleType();
        }
        if(this.imageProcessor == null){
            this.imageProcessor = options.getImageProcessor();
        }
        if(this.imageDisplayer == null){
            this.imageDisplayer = options.getImageDisplayer();
        }
        if(this.loadingDrawableHolder == null){
            this.loadingDrawableHolder = options.getLoadingDrawableHolder();
        }
        if(this.loadFailDrawableHolder == null){
            this.loadFailDrawableHolder = options.getLoadFailDrawableHolder();
        }

        return this;
    }

    /**
     * 设置显示参数，你只需要提前将DisplayOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return DisplayHelper
     */
    public DisplayHelper options(Enum<?> optionsName){
        return options((DisplayOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return RequestFuture 你可以通过RequestFuture来查看请求的状态或者取消这个请求
     */
    public RequestFuture fire() {
        spear.getConfiguration().getDisplayCallbackHandler().startCallbackOnFire(displayListener);

        // 验证imageView参数
        if(imageView == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "：" + "imageView不能为null");
            }
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(null, null, FailureCause.IMAGE_VIEW_NULL, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "：" + "uri不能为null或空");
            }
            BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(imageView, loadingBitmapDrawable, FailureCause.URI_NULL_OR_EMPTY, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        ImageScheme imageScheme = ImageScheme.valueOfUri(uri);
        if(imageScheme == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + "：" + "未知的协议类型" + " URI" + "=" + uri);
            }
            spear.getConfiguration().getDisplayCallbackHandler().failCallbackOnFire(imageView, getDrawableFromDrawableHolder(loadFailDrawableHolder), FailureCause.URI_NO_SUPPORT, displayListener);
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 尝试从内存中寻找缓存图片
        String requestId = memoryCacheId!=null?memoryCacheId:createMemoryCacheId(uri, maxsize, resize, scaleType, imageProcessor);
        if(enableMemoryCache){
            BitmapDrawable cacheDrawable = spear.getConfiguration().getMemoryCache().get(requestId);
            if(cacheDrawable != null){
                spear.getConfiguration().getDisplayCallbackHandler().completeCallbackOnFire(imageView, uri, cacheDrawable, displayListener, ImageFrom.MEMORY_CACHE);
                spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
                return null;
            }
        }

        // 如果已经暂停了的话就不再从本地或网络加载了
        if(spear.isPaused()){
            BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
            imageView.clearAnimation();
            imageView.setImageDrawable(loadingBitmapDrawable);
            if(displayListener != null){
                displayListener.onCanceled();
            }
            return null;
        }

        // 试图取消当前ImageView上正在加载的请求，如果有结果返回表示正在加载的任务与目前的是一样的无需取消
        DisplayRequest potentialRequest = cancelPotentialDisplayRequest(imageView, requestId);
        if(potentialRequest != null){
            spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);
            return new RequestFuture(potentialRequest);
        }

        // 组织请求
        final DisplayRequest request = new DisplayRequest();

        request.setUri(uri);
        request.setName(uri);
        request.setSpear(spear);
        request.setImageScheme(imageScheme);
        request.setEnableDiskCache(enableDiskCache);

        request.setMaxsize(maxsize);
        request.setResize(resize);
        request.setImageProcessor(imageProcessor);
        request.setScaleType(scaleType);

        request.setId(requestId);
        request.setEnableMemoryCache(enableMemoryCache);
        request.setImageViewHolder(new ImageViewHolder(imageView, request));
        request.setImageDisplayer(imageDisplayer);
        request.setLoadFailDrawableHolder(loadFailDrawableHolder);

        request.setDisplayListener(displayListener);
        request.setProgressListener(progressListener);

        request.setResizeByImageViewLayoutSizeAndFromDisplayer(resizeByImageViewLayoutSizeAndFromDisplayer);

        // 显示默认图片
        BitmapDrawable loadingBitmapDrawable = getDrawableFromDrawableHolder(loadingDrawableHolder);
        imageView.clearAnimation();
        imageView.setImageDrawable(new AsyncDrawable(spear.getConfiguration().getContext().getResources(), loadingBitmapDrawable != null ? loadingBitmapDrawable.getBitmap() : null, request));

        // 分发请求
        request.runDispatch();

        spear.getConfiguration().getDisplayHelperManager().recoveryDisplayHelper(this);

        if(returnRequestFuture){
            RequestFuture requestFuture = new RequestFuture(request);
            if(imageView instanceof SpearImageView){
                ((SpearImageView) imageView).setRequestFuture(requestFuture);
            }
            return requestFuture;
        }else{
            return null;
        }
    }

    protected ImageProcessor getImageProcessor(){
        if(imageProcessor != null){
            return imageProcessor;
        }else if(resize!=null){
            return spear.getConfiguration().getDefaultCutImageProcessor();
        }else{
            return null;
        }
    }

    protected BitmapDrawable getDrawableFromDrawableHolder(DrawableHolder drawableHolder){
        if(drawableHolder != null){
            return drawableHolder.getDrawable(spear.getConfiguration().getContext(), resize, scaleType, getImageProcessor(), resizeByImageViewLayoutSizeAndFromDisplayer);
        }else{
            return null;
        }
    }

    /**
     * 生成内存缓存ID
     */
    protected String createMemoryCacheId(String uri, ImageSize maxsize, ImageSize resize, ImageView.ScaleType scaleType, ImageProcessor imageProcessor){
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

    /**
     * 取消潜在的请求
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    protected static DisplayRequest cancelPotentialDisplayRequest(ImageView imageView, String newRequestId) {
        final DisplayRequest potentialDisplayRequest = AsyncDrawable.getDisplayRequestByAsyncDrawable(imageView);
        boolean cancelled = true;
        if (potentialDisplayRequest != null) {
            final String oldRequestId = potentialDisplayRequest.getId();
            if (oldRequestId != null && oldRequestId.equals(newRequestId)) {
                cancelled = false;
            }else{
                potentialDisplayRequest.cancel();
                cancelled = true;
            }
            if(!cancelled && Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + "：" + "无需取消" + "；" + "ImageViewCode" + "=" + imageView.hashCode() + "；" + potentialDisplayRequest.getName());
            }
        }
        return cancelled?null:potentialDisplayRequest;
    }
}
