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

import android.util.Log;

import java.io.File;

import me.xiaopan.android.spear.download.ImageDownloader;

/**
 * 下载请求
 */
public class DownloadRequestImpl implements DownloadRequest, Runnable{
    private static final String NAME = "DownloadRequestImpl";

    // Base fields
    private Spear spear;  // Spear
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private UriScheme uriScheme;	// Uri协议格式

    // Download fields
    private boolean enableDiskCache = true;	// 是否开启磁盘缓存
    private ProgressListener progressListener;  // 下载进度监听器
    private DownloadListener downloadListener;  // 下载监听器

    // Runtime fields
    private File cacheFile;	// 缓存文件
    private RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    private FailCause failCause;    // 失败原因
    private ImageFrom imageFrom;    // 图片来源
    private RequestStatus requestStatus = RequestStatus.WAIT_DISPATCH;  // 状态

    public DownloadRequestImpl(Spear spear, String uri, UriScheme uriScheme) {
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
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
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
        return null;
    }

    @Override
    public ImageFrom getImageFrom() {
        return imageFrom;
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
    public void toFailedStatus(FailCause failCause) {
        this.failCause = failCause;
        setRequestStatus(RequestStatus.FAILED);
        if(downloadListener != null){
            downloadListener.onFailed(failCause);
        }
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.requestStatus = RequestStatus.CANCELED;
        setRequestStatus(RequestStatus.CANCELED);
        if(downloadListener != null){
            downloadListener.onCanceled();
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
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    public void run() {
        switch(runStatus){
            case DISPATCH:
                executeDispatch();
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
     * 分发请求
     */
    private void executeDispatch() {
        setRequestStatus(RequestStatus.DISPATCHING);
        if(uriScheme == UriScheme.HTTP || uriScheme == UriScheme.HTTPS){
            this.cacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().getCacheFile(uri):null;
            if(cacheFile != null && cacheFile.exists()){
                this.imageFrom = ImageFrom.DISK_CACHE;
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "diskCache" + " - " + name);
                }
                if(downloadListener != null){
                    downloadListener.onCompleted(cacheFile, false);
                }
            }else{
                postRunDownload();
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "download" + " - " + name);
                }
            }
        }else{
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "executeDispatch" + " - " + "not support uri:" + uri + " - " + name);
            }
            toFailedStatus(FailCause.URI_NO_SUPPORT);
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
            this.requestStatus = RequestStatus.COMPLETED;
            if(downloadListener != null){
                if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                    downloadListener.onCompleted((File) downloadResult.getResult(), downloadResult.isFromNetwork());
                }else{
                    downloadListener.onCompleted((byte[]) downloadResult.getResult());
                }
            }
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }
}