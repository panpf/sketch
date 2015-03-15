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

import android.util.Log;

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.download.ImageDownloader;

/**
 * 下载请求
 */
public class DownloadRequest implements Request, Runnable, StatusManager, RunManager{
    public static final boolean DEFAULT_ENABLE_DISK_CACHE = true;
    private static final String NAME = "DownloadRequest";

    protected Spear spear;
    protected String uri;	// 图片地址
    protected String name;	// 名称，用于在输出LOG的时候区分不同的请求
    protected boolean enableDiskCache = DEFAULT_ENABLE_DISK_CACHE;	// 是否开启磁盘缓存
    protected UriScheme uriScheme;	// Uri协议格式
    protected ProgressListener progressListener;  // 下载进度监听器

    private DownloadListener downloadListener;  // 下载监听器

    protected Status status = Status.WAIT_DISPATCH;  // 状态
    protected RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么
    protected FailCause failCause;    // 失败原因
    protected CancelCause cancelCause;

    protected File cacheFile;	// 缓存文件
    private ImageDownloader.DownloadResult downloadResult;    // 下载结果

    public DownloadRequest(Spear spear, String uri, UriScheme uriScheme) {
        this.spear = spear;
        this.uri = uri;
        this.uriScheme = uriScheme;
    }

    @Override
    public String getUri() {
        return uri;
    }

    /**
     * 获取Uri协议类型
     */
    public UriScheme getUriScheme() {
        return uriScheme;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 设置请求名称，用于在log中区分请求
     * @param name 请求名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取进度监听器哦
     * @return 进度监听器哦
     */
    public ProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     */
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * 设置是否开启磁盘缓存
     * @param enableDiskCache 是否开启磁盘缓存
     */
    public void setEnableDiskCache(boolean enableDiskCache) {
        this.enableDiskCache = enableDiskCache;
    }

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    /**
     * 获取Spear
     * @return Spear
     */
    public Spear getSpear() {
        return spear;
    }

    /**
     * 获取缓存文件
     * @return 缓存文件
     */
    public File getCacheFile() {
        return cacheFile;
    }

    /**
     * 获取失败原因
     * @return 失败原因
     */
    public FailCause getFailCause() {
        return failCause;
    }

    /**
     * 获取取消原因
     * @return 取消原因
     */
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    @Override
    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED;
    }

    @Override
    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    @Override
    public boolean cancel() {
        if(isFinished()){
            return false;
        }
        toCanceledStatus(CancelCause.NORMAL);
        return true;
    }

    public void setStatus(Status status) {
        this.status = status;
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
                new IllegalStateException(runStatus.name()+" 属于未知的类型，没法搞").printStackTrace();
                break;
        }
    }

    /**
     * 分发请求
     */
    public void executeDispatch() {
        toDispatchingStatus();
        // 要先创建缓存文件
        this.cacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().createCacheFile(this):null;

        // 从网络下载
        runDownload();
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - " + "executeDispatch：" + name);
        }
    }

    /**
     * 执行下载
     */
    protected final void executeDownload() {
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + " - " + "已取消下载（下载刚开始）" + "；" + name);
            }
            return;
        }

        ImageDownloader.DownloadResult downloadResult = spear.getConfiguration().getImageDownloader().download(this);

        if(isCanceled()){
            return;
        }

        if(downloadResult != null  && downloadResult.getResult() != null){
            handleDownloadCompleted(downloadResult);
        }else{
            toFailedStatus(FailCause.DOWNLOAD_FAIL);
        }
    }

    @Override
    public void toWaitDispatchStatus() {
        this.status = Status.WAIT_DISPATCH;
    }

    @Override
    public void toDispatchingStatus() {
        this.status = Status.DISPATCHING;
    }

    @Override
    public void toWaitDownloadStatus() {
        this.status = Status.WAIT_DOWNLOAD;
    }

    @Override
    public void toGetDownloadLockStatus() {
        this.status = Status.GET_DOWNLOAD_LOCK;
    }

    @Override
    public void toDownloadingStatus() {
        this.status = Status.DOWNLOADING;
    }

    @Override
    public void toWaitLoadStatus() {
        this.status = Status.WAIT_LOAD;
    }

    @Override
    public void toLoadingStatus() {
        this.status = Status.LOADING;
    }

    @Override
    public void toWaitDisplayStatus() {
        this.status = Status.WAIT_DISPLAY;
    }

    @Override
    public void toDisplayingStatus() {
        this.status = Status.DISPLAYING;
    }

    @Override
    public void toCompletedStatus() {
        this.status = Status.COMPLETED;
        if(downloadListener != null){
            if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                downloadListener.onCompleted((File) downloadResult.getResult(), downloadResult.isFromNetwork());
            }else{
                downloadListener.onCompleted((byte[]) downloadResult.getResult());
            }
        }
    }

    @Override
    public void toFailedStatus(FailCause failCause) {
        this.status = Status.FAILED;
        this.failCause = failCause;
        if(downloadListener != null){
            downloadListener.onFailed(failCause);
        }
    }

    @Override
    public void toCanceledStatus(CancelCause cancelCause) {
        this.status = Status.CANCELED;
        this.cancelCause = cancelCause;
        if(downloadListener != null){
            downloadListener.onCanceled();
        }
    }

    @Override
    public void runDispatch() {
        toWaitDispatchStatus();
        this.runStatus = RunStatus.DISPATCH;
        spear.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void runDownload() {
        toWaitDownloadStatus();
        this.runStatus = RunStatus.DOWNLOAD;
        spear.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void runLoad() {
        toWaitLoadStatus();
        this.runStatus = RunStatus.LOAD;
        spear.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    /**
     * 更新进度
     * @param totalLength 总长度
     * @param completedLength 已完成长度
     */
    public void handleUpdateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    /**
     * 下载完成
     * @param downloadResult 下载结果
     */
    protected void handleDownloadCompleted(ImageDownloader.DownloadResult downloadResult){
        this.downloadResult = downloadResult;
        toCompletedStatus();
    }
}