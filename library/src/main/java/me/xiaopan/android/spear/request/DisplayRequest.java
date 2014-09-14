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

package me.xiaopan.android.spear.request;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.DefaultImageDisplayer;
import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.process.CutImageProcessor;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.AsyncDrawable;
import me.xiaopan.android.spear.util.DrawableHolder;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.ImageSize;
import me.xiaopan.android.spear.util.ImageViewHolder;
import me.xiaopan.android.spear.util.Scheme;

/**
 * 显示请求
 */
public class DisplayRequest extends LoadRequest{
    private static final String LOG_TAG = DisplayRequest.class.getSimpleName();

    private static ImageDisplayer defaultImageDisplayer;

    private String id;	//ID
    private boolean enableMemoryCache = true;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
    private DrawableHolder failedDrawableHolder;	//当加载失败时显示的图片
    private ImageDisplayer imageDisplayer;	//图片显示器
	private ImageViewHolder imageViewHolder;	//ImageView持有器

	private DisplayListener displayListener;	//监听器
    private ProgressCallback displayProgressCallback; // 显示进度监听器

    /**
     * 获取ID，此ID用来在内存缓存Bitmap时作为其KEY
     * @return ID
     */
	public String getId() {
		return id;
	}

    /**
     * 获取显示监听器
     * @return 显示监听器
     */
	public DisplayListener getDisplayListener() {
		return displayListener;
	}

    /**
     * 获取ImageView持有器
     * @return ImageView持有器
     */
	public ImageViewHolder getImageViewHolder() {
		return imageViewHolder;
	}

    /**
     * 是否开启内存缓存
     * @return 是否开启内存缓存
     */
    public boolean isEnableMemoryCache() {
        return enableMemoryCache;
    }

    /**
     * 获取图片显示器用于在图片加载完成后显示图片
     * @return 图片显示器
     */
    public ImageDisplayer getImageDisplayer() {
        if(imageDisplayer != null){
            return imageDisplayer;
        }else{
            if(defaultImageDisplayer == null){
                defaultImageDisplayer = new DefaultImageDisplayer();
            }
            return defaultImageDisplayer;
        }
    }

    /**
     * 获取加载失败时显示的图片
     * @return 加载失败时显示的图片
     */
    public BitmapDrawable getFailedDrawable() {
        return failedDrawableHolder!=null?failedDrawableHolder.getDrawable(spear.getContext(), getImageProcessor()):null;
    }

    @Override
    public boolean isCanceled() {
        boolean isCanceled = super.isCanceled();
        if(!isCanceled){
            isCanceled = imageViewHolder != null && imageViewHolder.isCollected();
            if(isCanceled){
                setStatus(Status.CANCELED);
            }
        }
        return isCanceled;
    }

    /**
     * 获取显示进度回调
     * @return 显示进度回调
     */
    public ProgressCallback getDisplayProgressCallback() {
        return displayProgressCallback;
    }

    /**
     * 生成ID
     */
    public static String createId(String uri, ImageSize maxsize, ImageSize resize, ImageProcessor imageProcessor){
        StringBuilder stringBuffer = new StringBuilder(uri);
        if(maxsize != null){
            stringBuffer.append("_");
            stringBuffer.append(maxsize.getWidth());
            stringBuffer.append("x");
            stringBuffer.append(maxsize.getHeight());
        }
        if(resize != null){
            stringBuffer.append("_");
            stringBuffer.append(resize.getWidth());
            stringBuffer.append("x");
            stringBuffer.append(resize.getHeight());
        }
        if(imageProcessor != null){
            stringBuffer.append("_");
            stringBuffer.append(imageProcessor.getClass().getName());
        }
        return stringBuffer.toString();
    }

    /**
     * 生成器，生成显示请求
     */
    public static class Builder{
        private Spear spear;
        private String uri;

        private long diskCacheTimeout;
        private boolean enableDiskCache = true;

        private ImageSize maxsize;
        private ImageSize resize;
        private ImageProcessor imageProcessor;
        private ImageView.ScaleType scaleType;

        private boolean enableMemoryCache = true;
        private ImageDisplayer imageDisplayer;
        private DrawableHolder loadingDrawableHolder;
        private DrawableHolder loadFailedDrawableHolder;

        private DisplayListener displayListener;
        private ProgressCallback progressCallback;

        private ImageView imageView;

        /**
         * 创建显示请求生成器
         * @param spear Spear
         * @param uri 支持以下6种类型
         * <blockquote>“http://site.com/image.png“  // from Web
         * <br>“https://site.com/image.png“ // from Web
         * <br>“file:///mnt/sdcard/image.png“ // from SD card
         * <br>“content://media/external/audio/albumart/13“ // from content provider
         * <br>“assets://image.png“ // from assets
         * <br>“drawable://" + R.drawable.image // from drawables
         * </blockquote>
         */
        public Builder(Spear spear, String uri, ImageView imageView) {
            this.spear = spear;
            this.uri = uri;
            this.imageView = imageView;

            if(imageView != null){
                // 根据ImageView的宽高计算maxsize
                this.maxsize = spear.getImageSizeCalculator().calculateImageMaxsize(imageView);

                // 如果根据ImageView没有计算出合适的maxsize，就以当前设备屏幕的1.5倍作为maxsize
                if(this.maxsize == null){
                    DisplayMetrics displayMetrics = spear.getContext().getResources().getDisplayMetrics();
                    this.maxsize = new ImageSize((int) (displayMetrics.widthPixels*1.5f), (int) (displayMetrics.heightPixels*1.5f));
                }

                // 根据ImageView的宽高计算resize
                this.resize = spear.getImageSizeCalculator().calculateImageResize(imageView);

                this.scaleType = imageView.getScaleType();
            }
        }

        /**
         * 关闭硬盘缓存
         * @return Builder
         */
        public Builder disableDiskCache() {
            this.enableDiskCache = false;
            return this;
        }

        /**
         * 设置磁盘缓存超时时间
         * @param diskCacheTimeout 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
         * @return Builder
         */
        public Builder diskCacheTimeout(long diskCacheTimeout) {
            this.diskCacheTimeout = diskCacheTimeout;
            return this;
        }

        /**
         * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
         * @param maxsize 最大尺寸
         * @return Builder
         */
        public Builder maxsize(ImageSize maxsize){
            this.maxsize = maxsize;
            return this;
        }

        /**
         * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
         * @param width 宽
         * @param height 高
         * @return Builder
         */
        public Builder maxsize(int width, int height){
            this.maxsize = new ImageSize(width, height);
            return this;
        }

        /**
         * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
         * @param resize 新的尺寸
         * @return Builder
         */
        public Builder resize(ImageSize resize){
            this.resize = resize;
            if(this.resize != null && imageProcessor == null){
                imageProcessor = new CutImageProcessor();
            }
            return this;
        }

        /**
         * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
         * @param width 宽
         * @param height 高
         * @return Builder
         */
        public Builder resize(int width, int height){
            this.resize = new ImageSize(width, height);
            if(imageProcessor == null){
                imageProcessor = new CutImageProcessor();
            }
            return this;
        }

        /**
         * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
         * @param processor Bitmap处理器
         * @return Builder
         */
        public Builder processor(ImageProcessor processor){
            this.imageProcessor = processor;
            return this;
        }

        /**
         * 设置ScaleType，BitmapProcessor会根据resize和ScaleType创建一张新的图片
         * @param scaleType ScaleType
         * @return Builder
         */
        public Builder scaleType(ImageView.ScaleType scaleType){
            this.scaleType = scaleType;
            return this;
        }

        /**
         * 关闭内存缓存
         */
        public Builder disableMemoryCache() {
            this.enableMemoryCache = false;
            return this;
        }

        /**
         * 设置显示监听器
         * @param displayListener 显示监听器
         */
        public Builder listener(DisplayListener displayListener) {
            this.displayListener = displayListener;
            return this;
        }

        /**
         * 设置图片显示器，在加载完成后会调用此显示器来显示图片
         * @param displayer 图片显示器
         */
        public Builder displayer(ImageDisplayer displayer) {
            this.imageDisplayer = displayer;
            return this;
        }

        /**
         * 设置正在加载的时候显示的图片
         * @param drawableResId 正在加载的时候显示的图片
         */
        public Builder loadingDrawable(int drawableResId) {
            if(loadingDrawableHolder == null){
                loadingDrawableHolder = new DrawableHolder();
            }
            loadingDrawableHolder.setResId(drawableResId);
            return this;
        }

        /**
         * 设置正在加载的时候显示的图片
         * @param drawableResId 正在加载的时候显示的图片
         * @param isProcess 是否使用BitmapProcessor对当前图片进行处理
         */
        public Builder loadingDrawable(int drawableResId, boolean isProcess) {
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
        public Builder loadFailedDrawable(int drawableResId) {
            if(loadFailedDrawableHolder == null){
                loadFailedDrawableHolder = new DrawableHolder();
            }
            loadFailedDrawableHolder.setResId(drawableResId);
            return this;
        }

        /**
         * 设置当加载失败的时候显示的图片
         * @param drawableResId 当加载失败的时候显示的图片
         * @param isProcess 是否使用BitmapProcessor对当前图片进行处理
         */
        public Builder loadFailedDrawable(int drawableResId, boolean isProcess) {
            if(loadFailedDrawableHolder == null){
                loadFailedDrawableHolder = new DrawableHolder();
            }
            loadFailedDrawableHolder.setResId(drawableResId);
            loadFailedDrawableHolder.setProcess(isProcess);
            return this;
        }

        /**
         * 设置进度回调
         * @param progressCallback 进度回调
         * @return Builder
         */
        public Builder progressCallback(ProgressCallback progressCallback){
            this.progressCallback = progressCallback;
            return this;
        }

        /**
         * 设置显示参数
         * @param options 显示参数
         * @return Builder
         */
        public Builder options(DisplayOptions options){
            if(options == null){
                return null;
            }

            this.enableDiskCache = options.isEnableDiskCache();
            this.diskCacheTimeout = options.getDiskCacheTimeout();

            if(this.maxsize == null || (options.getMaxsize() != null && spear.getImageSizeCalculator().compareMaxsize(options.getMaxsize(), this.maxsize) < 0)){
                this.maxsize = options.getMaxsize();
            }
            if(this.resize == null || (options.getResize() != null && spear.getImageSizeCalculator().compareResize(options.getResize(), this.resize) < 0)){
                this.resize = options.getResize();
            }
            if(this.scaleType == null || (options.getScaleType() != null && this.scaleType != options.getScaleType())){
                this.scaleType = options.getScaleType();
            }
            this.imageProcessor = options.getImageProcessor();

            this.enableMemoryCache = options.isEnableMemoryCache();
            this.imageDisplayer = options.getImageDisplayer();
            this.loadingDrawableHolder = options.getLoadingDrawableHolder();
            this.loadFailedDrawableHolder = options.getLoadFailedDrawableHolder();

            return this;
        }

        /**
         * 设置显示参数，你只需要提前将DisplayOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
         * @param optionsName 参数名称
         * @return Builder
         */
        public Builder options(Enum<?> optionsName){
            return options((DisplayOptions) Spear.getOptions(optionsName));
        }

        /**
         * 执行请求
         * @return RequestFuture 你可以通过RequestFuture来查看请求的状态或者取消这个请求
         */
        public RequestFuture fire() {
            // 执行请求
            if(displayListener != null){
                displayListener.onStarted();
            }

            // 验证imageView参数
            if(imageView == null){
                if(spear.isDebugMode()){
                    Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "imageView不能为null");
                }
                if(displayListener != null){
                    displayListener.onFailed(FailureCause.IMAGE_VIEW_NULL);
                }
                return null;
            }

            // 验证uri参数
            if(uri == null || "".equals(uri.trim())){
                if(loadFailedDrawableHolder != null){
                    Drawable loadFailedDrawable = loadFailedDrawableHolder.getDrawable(spear.getContext(), imageProcessor);
                    if(loadFailedDrawable != null){
                        imageView.setImageDrawable(loadFailedDrawable);
                    }
                }
                if(spear.isDebugMode()){
                    Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "uri不能为null或空");
                }
                if(displayListener != null){
                    displayListener.onFailed(FailureCause.URI_NULL_OR_EMPTY);
                }
                return null;
            }

            // 过滤掉不支持的URI协议类型
            Scheme scheme = Scheme.valueOfUri(uri);
            if(scheme == Scheme.UNKNOWN){
                if(spear.isDebugMode()){
                    Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "未知的协议类型" + " URI" + "=" + uri);
                }
                if(displayListener != null){
                    displayListener.onFailed(FailureCause.URI_NO_SUPPORT);
                }
                return null;
            }

            // 计算解码尺寸、处理尺寸和请求ID
            String requestId = DisplayRequest.createId(encodeUrl(uri), maxsize, resize, imageProcessor);

            // 尝试显示
            if(enableMemoryCache){
                BitmapDrawable cacheDrawable = spear.getMemoryCache().get(requestId);
                if(cacheDrawable != null){
                    imageView.clearAnimation();
                    imageView.setImageDrawable(cacheDrawable);
                    if(displayListener != null){
                        displayListener.onCompleted(uri, imageView, cacheDrawable);
                    }
                    return null;
                }
            }

            // 试图取消当前ImageView上正在加载的请求
            DisplayRequest potentialRequest = cancelPotentialDisplayRequest(imageView, requestId);
            if(potentialRequest != null){
                return new RequestFuture(potentialRequest);
            }

            // 创建请求
            DisplayRequest request = new DisplayRequest();

            request.uri = uri;
            request.name = uri;
            request.spear = spear;
            request.scheme = scheme;
            request.enableDiskCache = enableDiskCache;
            request.diskCacheTimeout = diskCacheTimeout;

            request.maxsize = maxsize;
            request.resize = resize;
            request.imageProcessor = imageProcessor;
            request.scaleType = scaleType;

            request.id = requestId;
            request.enableMemoryCache = enableMemoryCache;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
            request.imageViewHolder = new ImageViewHolder(imageView, request);
            request.imageDisplayer = imageDisplayer;
            request.failedDrawableHolder = loadFailedDrawableHolder;

            request.displayListener = displayListener;
            request.displayProgressCallback = progressCallback;

            // 显示默认图片
            BitmapDrawable loadingBitmapDrawable = loadingDrawableHolder!=null?loadingDrawableHolder.getDrawable(spear.getContext(), imageProcessor):null;
            imageView.clearAnimation();
            imageView.setImageDrawable(new AsyncDrawable(spear.getContext().getResources(), loadingBitmapDrawable != null ? loadingBitmapDrawable.getBitmap() : null, request));

            spear.getRequestExecutor().execute(request);
            return new RequestFuture(request);
        }
    }

    /**
     * 取消潜在的请求
     * @return true：取消成功；false：ImageView所关联的任务就是所需的无需取消
     */
    private static DisplayRequest cancelPotentialDisplayRequest(ImageView imageView, String newRequestId) {
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
            if(!cancelled && potentialDisplayRequest.getSpear().isDebugMode()){
                Log.d(Spear.LOG_TAG, LOG_TAG + "：" + "无需取消" + "；" + "ImageViewCode" + "=" + imageView.hashCode() + "；" + potentialDisplayRequest.getName());
            }
        }
        return cancelled?null:potentialDisplayRequest;
    }

    /**
     * 编码URL
     * @param url 待编码的URL
     * @return 经过URL编码规则编码后的URL
     */
    public static String encodeUrl(String url){
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }
}
