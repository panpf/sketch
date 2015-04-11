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
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.process.ImageProcessor;

/**
 * 加载请求
 */
public class LoadRequestImpl implements LoadRequest, Runnable{
    private static final String NAME = "LoadRequestImpl";

    // Base fields
    private Spear spear;  // Spear
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;	// Uri协议格式

    // Download fields
    private boolean enableDiskCache = true;	// 是否开启磁盘缓存
    private ProgressListener progressListener;  // 下载进度监听器

    // Load fields
    private ImageSize resize;	// 裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来裁剪图片
    private ImageSize maxsize;	// 最大尺寸，用于读取图片时计算inSampleSize
    private ImageProcessor imageProcessor;	// 图片处理器
    private ImageView.ScaleType scaleType; // 图片缩放方式，ImageProcessor会根据resize和scaleType来创建新的图片
    private LoadListener loadListener;	// 监听器

    // Runtime fields
    private File cacheFile;	// 缓存文件
    private byte[] imageData;
    private ImageFrom imageFrom;    // 图片来源
    private FailCause failCause;    // 失败原因
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private CancelCause cancelCause;  // 取消原因
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

    public LoadRequestImpl(Spear spear, String uri, UriScheme uriScheme) {
        this.spear = spear;
        this.uri = uri;
        this.uriScheme = uriScheme;
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
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /****************************************** Load methods ******************************************/
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

    @Override
    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    /****************************************** Runtime methods ******************************************/
    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    @Override
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
    }

    @Override
    public File getCacheFile() {
        return cacheFile;
    }

    @Override
    public FailCause getFailCause() {
        return failCause;
    }

    @Override
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    /****************************************** Other methods ******************************************/
    @Override
    public boolean isFinished() {
        return requestStatus == RequestStatus.COMPLETED || requestStatus == RequestStatus.CANCELED || requestStatus == RequestStatus.FAILED;
    }

    @Override
    public boolean isCanceled() {
        return requestStatus == RequestStatus.CANCELED;
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
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.FAILED);
        if(loadListener != null){
            loadListener.onFailed(failCause);
        }
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
        setRequestStatus(RequestStatus.CANCELED);
        if(loadListener != null){
            loadListener.onCanceled(cancelCause);
        }
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
            this.cacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().createCacheFile(this):null;
            if(cacheFile != null && cacheFile.exists()){
                this.imageFrom = ImageFrom.DISK_CACHE;
                postRunLoad();
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "diskCache" + " - " + name);
                }
            }else{
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
            this.imageFrom = downloadResult.isFromNetwork()?ImageFrom.NETWORK:ImageFrom.DISK_CACHE;

            if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                this.cacheFile = (File) downloadResult.getResult();
            }else{
                this.imageData = (byte[]) downloadResult.getResult();
            }

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
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled" + " - " + "start load" + " - " + name);
            }
            return;
        }

        setRequestStatus(RequestStatus.LOADING);

        // 解码
        Bitmap bitmap = spear.getConfiguration().getImageDecoder().decode(this);
        if(bitmap != null && !bitmap.isRecycled()){
            if(Spear.isDebugMode()){
                Log.d(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "new bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "executeLoad" + " - " + name);
            }
        }else{
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "decode failed - " + name);
            }
        }

        if(isCanceled()){
            if(bitmap != null){
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "recycle bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "decode after - cancel" + " - " + name);
                }
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled" + " - " + "decode after" + " - " + name);
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
                        Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "new bitmap@"+Integer.toHexString(newBitmap.hashCode())+" - " + "recycle old bitmap@" + Integer.toHexString(bitmap.hashCode()) + " - " + "process after" + " - " + name);
                    }
                    bitmap.recycle();
                    bitmap = newBitmap;
                }
            }
        }

        if(isCanceled()){
            if(bitmap != null){
                if(Spear.isDebugMode()){
                    Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "recycle bitmap@" + Integer.toHexString(bitmap.hashCode()) + "（executeLoad - processAfter - cancel）");
                }
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "executeLoad" + " - " + "canceled "+ " - " + "process after" + " - " + name);
            }
            return;
        }

        if(bitmap != null && !bitmap.isRecycled()){
            this.requestStatus = RequestStatus.COMPLETED;
            if(loadListener != null){
                loadListener.onCompleted(bitmap, imageFrom);
            }
        }else{
            toFailedStatus(FailCause.DECODE_FAIL);
        }
    }
}
