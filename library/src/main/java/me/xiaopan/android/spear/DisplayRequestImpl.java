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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import me.xiaopan.android.spear.display.ImageDisplayer;
import me.xiaopan.android.spear.display.TransitionImageDisplayer;
import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.process.ImageProcessor;

/**
 * 显示请求
 */
public class DisplayRequestImpl implements DisplayRequest, Runnable{
    private static final String NAME = "DisplayRequestImpl";

    // Base fields
    private Spear spear;  // Spear
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;	// Uri协议格式

    // Download fields
    private boolean enableDiskCache = true;	// 是否开启磁盘缓存
    private ProgressListener progressListener;  // 下载进度监听器

    // Load fields
    private RequestHandleLevel requestHandleLevel = RequestHandleLevel.NET;  // Level
    private ImageSize resize;	// 裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来裁剪图片
    private ImageSize maxsize;	// 最大尺寸，用于读取图片时计算inSampleSize
    private ImageProcessor imageProcessor;	// 图片处理器
    private ImageView.ScaleType scaleType; // 图片缩放方式，ImageProcessor会根据resize和scaleType来创建新的图片

    // Display fields
    private String memoryCacheId;	// 内存缓存ID
    private boolean enableMemoryCache = true;	// 是否开启内存缓存
    private boolean thisIsGifImage; // 这是一张GIF图
    private DrawableHolder loadFailDrawableHolder;	// 当加载失败时显示的图片
    private DrawableHolder pauseDownloadDrawableHolder;	// 当暂停下载时显示的图片
    private ImageDisplayer imageDisplayer;	// 图片显示器
    private DisplayListener displayListener;	// 监听器

    // Runtime fields
    private File cacheFile;	// 缓存文件
    private byte[] imageData;   // 如果不使用磁盘缓存的话下载完成后图片数据就用字节数组保存着
    private ImageFrom imageFrom;    // 图片来自哪里
    private FailCause failCause;    // 失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态
    private Drawable resultDrawable;    // 最终的图片
    private ImageViewHolder imageViewHolder;    // 绑定ImageView
    private boolean levelFromPauseDownload;

    public DisplayRequestImpl(Spear spear, String uri, UriScheme uriScheme, String memoryCacheId, ImageView imageView) {
        this.spear = spear;
        this.uri = uri;
        this.uriScheme = uriScheme;
        this.memoryCacheId = memoryCacheId;
        this.imageViewHolder = new ImageViewHolder(imageView, this);
    }

    /****************************************** Base methods ******************************************/
    @Override
    public Spear getSpear() {
        return spear;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UriScheme getUriScheme() {
        return uriScheme;
    }

    /****************************************** Download methods ******************************************/
    @Override
    public void setEnableDiskCache(boolean enableDiskCache) {
        this.enableDiskCache = enableDiskCache;
    }

    @Override
    public boolean isEnableDiskCache() {
        return enableDiskCache;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /****************************************** Load methods ******************************************/
    @Override
    public void setRequestHandleLevel(RequestHandleLevel requestHandleLevel) {
        this.requestHandleLevel = requestHandleLevel;
    }

    @Override
    public ImageSize getResize() {
        return resize;
    }

    @Override
    public void setResize(ImageSize resize) {
        this.resize = resize;
    }

    @Override
    public ImageSize getMaxsize() {
        return maxsize;
    }

    @Override
    public void setMaxsize(ImageSize maxsize) {
        this.maxsize = maxsize;
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    @Override
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    @Override
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    /****************************************** Display methods ******************************************/
    @Override
    public String getMemoryCacheId() {
        return memoryCacheId;
    }

    @Override
    public void setEnableMemoryCache(boolean enableMemoryCache) {
        this.enableMemoryCache = enableMemoryCache;
    }

    @Override
    public void setImageDisplayer(ImageDisplayer imageDisplayer) {
        this.imageDisplayer = imageDisplayer;
    }

    @Override
    public void setLoadFailDrawableHolder(DrawableHolder loadFailDrawableHolder) {
        this.loadFailDrawableHolder = loadFailDrawableHolder;
    }

    @Override
    public BitmapDrawable getLoadFailDrawable() {
        if(loadFailDrawableHolder == null){
            return null;
        }
        ImageProcessor processor;
        if(getImageProcessor() != null){
            processor = getImageProcessor();
        }else if(getResize() != null){
            processor = spear.getConfiguration().getDefaultCutImageProcessor();
        }else{
            processor = null;
        }
        return loadFailDrawableHolder.getDrawable(spear.getConfiguration().getContext(), getResize(), getScaleType(), processor, imageDisplayer!=null&&imageDisplayer instanceof TransitionImageDisplayer);
    }

    @Override
    public void setPauseDownloadDrawableHolder(DrawableHolder pauseDownloadDrawableHolder) {
        this.pauseDownloadDrawableHolder = pauseDownloadDrawableHolder;
    }

    @Override
    public BitmapDrawable getPauseDownloadDrawable() {
        if(pauseDownloadDrawableHolder == null){
            return null;
        }
        ImageProcessor processor;
        if(getImageProcessor() != null){
            processor = getImageProcessor();
        }else if(getResize() != null){
            processor = spear.getConfiguration().getDefaultCutImageProcessor();
        }else{
            processor = null;
        }
        return pauseDownloadDrawableHolder.getDrawable(spear.getConfiguration().getContext(), getResize(), getScaleType(), processor, imageDisplayer!=null&&imageDisplayer instanceof TransitionImageDisplayer);
    }

    @Override
    public void setDisplayListener(DisplayListener displayListener) {
        this.displayListener = displayListener;
    }

    /****************************************** Runtime methods ******************************************/
    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        toCanceledStatus(CancelCause.NORMAL);
        return true;
    }

    @Override
    public File getCacheFile() {
        return cacheFile;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    /****************************************** Other methods ******************************************/
    @Override
    public boolean isCanceled() {
        boolean isCanceled = requestStatus == RequestStatus.CANCELED;
        if(!isCanceled){
            isCanceled = imageViewHolder != null && imageViewHolder.isCollected();
            if(isCanceled){
                toCanceledStatus(CancelCause.NORMAL);
            }
        }
        return isCanceled;
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.WAIT_DISPLAY);
        spear.getConfiguration().getDisplayCallbackHandler().failCallback(this);
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            spear.getConfiguration().getDisplayCallbackHandler().cancelCallback(this);
        }
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            spear.getConfiguration().getDisplayCallbackHandler().updateProgressCallback(this, totalLength, completedLength);
        }
    }

    @Override
    public void postRunDispatch() {
        setRequestStatus(RequestStatus.WAIT_DISPATCH);
        this.runStatus = RunStatus.DISPATCH;
        spear.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void postRunDownload() {
        setRequestStatus(RequestStatus.WAIT_DOWNLOAD);
        this.runStatus = RunStatus.DOWNLOAD;
        spear.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void postRunLoad() {
        setRequestStatus(RequestStatus.WAIT_LOAD);
        this.runStatus = RunStatus.LOAD;
        spear.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    @Override
    public void run() {
        switch(runStatus){
            case DISPATCH:
                executeDispatch();
                break;
            case LOAD:
                executeLoad();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalArgumentException(runStatus.name()+" unknown runStatus").printStackTrace();
                break;
        }
    }

    /**
     * 执行分发
     */
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS){
            File diskCacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().getCacheFile(uri):null;
            if(diskCacheFile != null && diskCacheFile.exists()){
                this.cacheFile = diskCacheFile;
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "diskCache" + " - " + name);
                }
            }else{
                if(requestHandleLevel == RequestHandleLevel.LOCAL){
                    if(levelFromPauseDownload){
                        setRequestStatus(RequestStatus.WAIT_DISPLAY);
                        spear.getConfiguration().getDisplayCallbackHandler().pauseDownloadCallback(this);
                        if(Spear.isDebugMode()){
                            Log.w(Spear.TAG, NAME + " - " + "canceled" + " - " + "pause download" + " - " + name);
                        }
                    }else{
                        toCanceledStatus(CancelCause.LEVEL_IS_LOCAL);
                        if(Spear.isDebugMode()){
                            Log.w(Spear.TAG, NAME + " - " + "canceled" + " - " + "level is local" + " - " + name);
                        }
                    }
                    return;
                }

                postRunDownload();
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "download" + " - " + name);
                }
            }
        }else{
            this.imageFrom = ImageFrom.LOCAL;
            postRunLoad();
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "local" + " - " + name);
            }
        }
    }

    /**
     * 执行下载
     */
    private void executeDownload() {
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeDownload" +" - "+"canceled" + " - " + "start download" + " - " + name);
            }
            return;
        }

        ImageDownloader.DownloadResult downloadResult = spear.getConfiguration().getImageDownloader().download(this);

        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeDownload" +" - "+"canceled" + " - " + "download after" + " - " + name);
            }
            return;
        }

        if(downloadResult != null  && downloadResult.getResult() != null){
            if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                this.cacheFile = (File) downloadResult.getResult();
            }else{
                this.imageData = (byte[]) downloadResult.getResult();
            }
            this.imageFrom = downloadResult.isFromNetwork()?ImageFrom.NETWORK:ImageFrom.DISK_CACHE;
            postRunLoad();
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    /**
     * 执行加载
     */
    private void executeLoad(){
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled" + " - " + "startLoad" + " - " + name);
            }
            return;
        }

        setRequestStatus(RequestStatus.LOADING);

        // 解码
        Bitmap bitmap = spear.getConfiguration().getImageDecoder().decode(this);
        if(bitmap != null && !bitmap.isRecycled()){
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "new bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + name);
            }
        }else{
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "decodeFailed" + " - " + name);
            }
        }

        if(isCanceled()){
            if(bitmap != null){
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "recycle bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "decodeAfter:cancel" + " - " + name);
                }
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled" + " - " + "decodeAfter" + " - " + name);
            }
            return;
        }

        //处理
        if(bitmap != null && !bitmap.isRecycled()){
            ImageProcessor imageProcessor = getImageProcessor();
            if(imageProcessor == null && getResize() != null){
                imageProcessor = spear.getConfiguration().getDefaultCutImageProcessor();
            }
            if(imageProcessor != null){
                Bitmap newBitmap = imageProcessor.process(bitmap, getResize(), getScaleType());
                if(newBitmap != bitmap){
                    if(Spear.isDebugMode()){
                        Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "new bitmap@"+Integer.toHexString(newBitmap.hashCode())+" - " + "recycle old bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "processAfter" + " - " + name);
                    }
                    bitmap.recycle();
                    bitmap = newBitmap;
                }
            }
        }

        if(isCanceled()){
            if(bitmap != null){
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "recycle bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "processAfter:cancel" + " - " + name);
                }
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled "+ " - " + "processAfter" + " - " + name);
            }
            return;
        }

        if(bitmap != null && !bitmap.isRecycled()){
            resultDrawable = new RecycleBitmapDrawable(spear.getConfiguration().getContext().getResources(), bitmap);
            if(enableMemoryCache && memoryCacheId != null){
                spear.getConfiguration().getMemoryCache().put(memoryCacheId, resultDrawable);
            }

            // 显示
            setRequestStatus(RequestStatus.WAIT_DISPLAY);
            spear.getConfiguration().getDisplayCallbackHandler().completeCallback(this);
        }else{
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }

    @Override
    public void handleCompletedOnMainThread() {
        if(isCanceled()){
            if(resultDrawable != null && resultDrawable instanceof RecycleDrawable){
                ((RecycleDrawable) resultDrawable).cancelWaitDisplay("completedCallback:cancel");
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "handleCompletedOnMainThread" + " - " + "canceled" + " - " + name);
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);
        if(imageDisplayer == null){
            imageDisplayer = spear.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(imageViewHolder.getImageView(), resultDrawable);
        setRequestStatus(RequestStatus.COMPLETED);
        if(displayListener != null){
            displayListener.onCompleted(imageFrom);
        }
    }

    @Override
    public void handleFailedOnMainThread() {
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "handleFailedOnMainThread" + " - " + "canceled" + " - " + name);
            }
            return;
        }

        setRequestStatus(RequestStatus.DISPLAYING);
        if(imageDisplayer == null){
            imageDisplayer = spear.getConfiguration().getDefaultImageDisplayer();
        }
        imageDisplayer.display(imageViewHolder.getImageView(), getLoadFailDrawable());
        setRequestStatus(RequestStatus.FAILED);
        if(displayListener != null){
            displayListener.onFailed(failCause);
        }
    }

    @Override
    public void handleCanceledOnMainThread() {
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    @Override
    public void handlePauseDownloadOnMainThread() {
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "handlePauseDownloadOnMainThread" + " - " + "canceled" + " - " + name);
            }
            return;
        }

        if(pauseDownloadDrawableHolder != null){
            setRequestStatus(RequestStatus.DISPLAYING);
            if(imageDisplayer == null){
                imageDisplayer = spear.getConfiguration().getDefaultImageDisplayer();
            }
            imageDisplayer.display(imageViewHolder.getImageView(), getPauseDownloadDrawable());
        }

        cancelCause = CancelCause.PAUSE_DOWNLOAD;
        setRequestStatus(RequestStatus.CANCELED);
        if(displayListener != null){
            displayListener.onCanceled(cancelCause);
        }
    }

    @Override
    public void updateProgressOnMainThread(int totalLength, int completedLength) {
        if(isFinished()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "updateProgressOnMainThread" + " - " + "finished" + " - " + name);
            }
            return;
        }

        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    public void setLevelFromPauseDownload(boolean levelFromPauseDownload) {
        this.levelFromPauseDownload = levelFromPauseDownload;
    }

    @Override
    public void setThisIsGifImage(boolean thisIsGifImage) {
        this.thisIsGifImage = thisIsGifImage;
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
    }
}