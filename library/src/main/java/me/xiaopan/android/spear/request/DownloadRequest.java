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
import me.xiaopan.android.spear.util.ImageScheme;

/**
 * 下载请求
 */
public class DownloadRequest implements Request{
    public static final boolean DEFAULT_ENABLE_DISK_CACHE = true;
    private static final String NAME = "DownloadRequest";

    /* 通用属性 */
    protected Spear spear;
    protected Status status = Status.WAIT_DISPATCH;  // 状态
    protected String uri;	// 图片地址
    protected String name;	// 名称，用于在输出LOG的时候区分不同的请求
    protected RunStatus runStatus = RunStatus.DISPATCH;    // 运行状态，用于在执行run方法时知道该干什么

    protected ImageScheme imageScheme;	// Uri协议格式

    /* 下载用到的属性 */
    protected boolean enableDiskCache = DEFAULT_ENABLE_DISK_CACHE;	// 是否开启磁盘缓存

    /* 下载过程中用到的属性 */
    protected File cacheFile;	// 缓存文件
    protected FailureCause failureCause;    // 失败原因
    private DownloadListener downloadListener;  // 下载监听器
    protected ProgressListener progressListener;  // 下载进度监听器
    private ImageDownloader.DownloadResult downloadResult;    // 下载结果

    @Override
    public Spear getSpear() {
        return spear;
    }

    @Override
    public void setSpear(Spear spear) {
        this.spear = spear;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public ImageScheme getImageScheme() {
        return imageScheme;
    }

    @Override
    public void setImageScheme(ImageScheme imageScheme) {
        this.imageScheme = imageScheme;
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
    public Status getStatus() {
        return status;
    }

    /**
     * 获取缓存文件
     * @return 缓存文件
     */
    public File getCacheFile() {
        return cacheFile;
    }

    /**
     * 设置开启磁盘缓存功能（默认开启）
     * @param enableDiskCache 是否开启磁盘缓存功能
     */
    public void setEnableDiskCache(boolean enableDiskCache) {
        this.enableDiskCache = enableDiskCache;
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

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    /**
     * 获取失败原因
     * @return 失败原因
     */
    public FailureCause getFailureCause() {
        return failureCause;
    }

    /**
     * 设置失败原因
     * @param failureCause 失败原因
     */
    public void setFailureCause(FailureCause failureCause) {
        this.failureCause = failureCause;
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
        toCanceledStatus();
        return true;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public void run() {
        switch(runStatus){
            case DISPATCH:
                dispatch();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalStateException(runStatus.name()+" 属于未知的类型，没法搞").printStackTrace();
                break;
        }
    }

    @Override
    public void dispatch() {
        // 要先创建缓存文件
        this.cacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().createCacheFile(this):null;

        // 从网络下载
        runDownload();
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - " + "dispatch：" + name);
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
            toFailedStatus(FailureCause.DOWNLOAD_FAIL);
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
    public void toFailedStatus(FailureCause failureCause) {
        this.status = Status.FAILED;
        this.failureCause = failureCause;
        if(downloadListener != null){
            downloadListener.onFailed(failureCause);
        }
    }

    @Override
    public void toCanceledStatus() {
        this.status = Status.CANCELED;
        if(downloadListener != null){
            downloadListener.onCanceled();
        }
    }

    @Override
    public void runDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        spear.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    @Override
    public void runDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        spear.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    @Override
    public void runLoad() {
        this.runStatus = RunStatus.LOAD;
        spear.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    public void handleUpdateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    protected void handleDownloadCompleted(ImageDownloader.DownloadResult downloadResult){
        this.downloadResult = downloadResult;
        toCompletedStatus();
    }
}