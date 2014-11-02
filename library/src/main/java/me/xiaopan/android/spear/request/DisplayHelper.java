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
 * DisplayHelper
 */
public class DisplayHelper {
    private static final String LOG_TAG = DisplayRequest.class.getSimpleName();

    Spear spear;
    String uri;

    long diskCacheTimeout;
    boolean enableDiskCache = true;

    ImageSize maxsize;
    ImageSize resize;
    ImageProcessor imageProcessor;
    ImageView.ScaleType scaleType;

    boolean enableMemoryCache = true;
    ImageDisplayer imageDisplayer;
    DrawableHolder loadingDrawableHolder;
    DrawableHolder loadFailedDrawableHolder;

    DisplayListener displayListener;
    ProgressListener progressListener;

    ImageView imageView;

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
        return this;
    }

    /**
     * 恢复默认值
     */
    public void restoreDefault(){
        spear = null;
        uri = null;

        diskCacheTimeout = 0;
        enableDiskCache = true;

        maxsize = null;
        resize = null;
        imageProcessor = null;
        scaleType = null;

        enableMemoryCache = true;
        imageDisplayer = null;
        loadingDrawableHolder = null;
        loadFailedDrawableHolder = null;

        displayListener = null;
        progressListener = null;

        imageView = null;
    }

    /**
     * 关闭硬盘缓存
     * @return Helper
     */
    public DisplayHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置磁盘缓存超时时间
     * @param diskCacheTimeout 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
     * @return Helper
     */
    public DisplayHelper diskCacheTimeout(long diskCacheTimeout) {
        this.diskCacheTimeout = diskCacheTimeout;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxsize 最大尺寸
     * @return Helper
     */
    public DisplayHelper maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return Helper
     */
    public DisplayHelper maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
        return this;
    }

    /**
     * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
     * @param resize 新的尺寸
     * @return Helper
     */
    public DisplayHelper resize(ImageSize resize){
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
     * @return Helper
     */
    public DisplayHelper resize(int width, int height){
        this.resize = new ImageSize(width, height);
        if(imageProcessor == null){
            imageProcessor = new CutImageProcessor();
        }
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor Bitmap处理器
     * @return Helper
     */
    public DisplayHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置ScaleType，BitmapProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return Helper
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
     * @param isProcess 是否使用BitmapProcessor对当前图片进行处理
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
    public DisplayHelper loadFailedDrawable(int drawableResId) {
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
    public DisplayHelper loadFailedDrawable(int drawableResId, boolean isProcess) {
        if(loadFailedDrawableHolder == null){
            loadFailedDrawableHolder = new DrawableHolder();
        }
        loadFailedDrawableHolder.setResId(drawableResId);
        loadFailedDrawableHolder.setProcess(isProcess);
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return Helper
     */
    public DisplayHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置显示参数
     * @param options 显示参数
     * @return Helper
     */
    public DisplayHelper options(DisplayOptions options){
        if(options == null){
            return null;
        }

        this.enableDiskCache = options.isEnableDiskCache();
        this.diskCacheTimeout = options.getDiskCacheTimeout();

        if(this.maxsize == null || (options.getMaxsize() != null && spear.getImageSizeCalculator().compareMaxsize(options.getMaxsize(), this.maxsize) < 0)){
            this.maxsize = options.getMaxsize();
        }
        if(this.resize == null){
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
     * @return Helper
     */
    public DisplayHelper options(Enum<?> optionsName){
        return options((DisplayOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return RequestFuture 你可以通过RequestFuture来查看请求的状态或者取消这个请求
     */
    public RequestFuture fire() {
        spear.getDisplayCallbackHandler().startCallbackOnFire(displayListener);

        // 验证imageView参数
        if(imageView == null){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "imageView不能为null");
            }
            spear.getDisplayCallbackHandler().failCallbackOnFire(null, null, FailureCause.IMAGE_VIEW_NULL, displayListener);
            spear.getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "uri不能为null或空");
            }
            Drawable loadFailedDrawable = null;
            if(loadFailedDrawableHolder != null){
                loadFailedDrawable = loadFailedDrawableHolder.getDrawable(spear.getContext(), imageProcessor);
            }
            spear.getDisplayCallbackHandler().failCallbackOnFire(imageView, loadFailedDrawable, FailureCause.URI_NULL_OR_EMPTY, displayListener);
            spear.getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 过滤掉不支持的URI协议类型
        Scheme scheme = Scheme.valueOfUri(uri);
        if(scheme == Scheme.UNKNOWN){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "未知的协议类型" + " URI" + "=" + uri);
            }
            Drawable loadFailedDrawable = null;
            if(loadFailedDrawableHolder != null){
                loadFailedDrawable = loadFailedDrawableHolder.getDrawable(spear.getContext(), imageProcessor);
            }
            spear.getDisplayCallbackHandler().failCallbackOnFire(imageView, loadFailedDrawable, FailureCause.URI_NO_SUPPORT, displayListener);
            spear.getDisplayHelperManager().recoveryDisplayHelper(this);
            return null;
        }

        // 计算解码尺寸、处理尺寸和请求ID
        String requestId = createId(encodeUrl(uri), maxsize, resize, imageProcessor);

        // 尝试显示
        if(enableMemoryCache){
            final BitmapDrawable cacheDrawable = spear.getMemoryCache().get(requestId);
            if(cacheDrawable != null){
                spear.getDisplayCallbackHandler().completeCallbackOnFire(imageView, uri, cacheDrawable, displayListener, DisplayListener.From.MEMORY);
                spear.getDisplayHelperManager().recoveryDisplayHelper(this);
                return null;
            }
        }

        // 试图取消当前ImageView上正在加载的请求
        DisplayRequest potentialRequest = cancelPotentialDisplayRequest(imageView, requestId);
        if(potentialRequest != null){
            spear.getDisplayHelperManager().recoveryDisplayHelper(this);
            return new RequestFuture(potentialRequest);
        }

        // 创建请求
        final DisplayRequest request = new DisplayRequest();

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
        request.displayProgressListener = progressListener;

        // 显示默认图片
        BitmapDrawable loadingBitmapDrawable = loadingDrawableHolder!=null?loadingDrawableHolder.getDrawable(spear.getContext(), imageProcessor):null;
        imageView.clearAnimation();
        imageView.setImageDrawable(new AsyncDrawable(spear.getContext().getResources(), loadingBitmapDrawable != null ? loadingBitmapDrawable.getBitmap() : null, request));

        spear.getRequestExecutor().execute(request);
        spear.getDisplayHelperManager().recoveryDisplayHelper(this);
        return new RequestFuture(request);
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
