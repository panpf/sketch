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
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.util.ImageScheme;

/**
 * 下载请求
 */
public class DownloadRequest implements Request, Runnable {
    public static final boolean DEFAULT_ENABLE_DISK_CACHE = true;
    private static final String NAME = "DownloadRequest";

    /* 通用属性 */
    private Spear spear;
    private Status status = Status.WAITING;  // 状态
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private ImageScheme imageScheme;	// Uri协议格式

    /* 下载请求用到的属性 */
    private File cacheFile;	// 缓存文件
    private boolean enableDiskCache = DEFAULT_ENABLE_DISK_CACHE;	// 是否开启磁盘缓存
    private DownloadListener downloadListener;  // 下载监听器
    private ProgressListener progressListener;  // 下载进度监听器

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
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    /**
     * 获取缓存文件
     */
	public File getCacheFile() {
		return cacheFile;
	}

    /**
     * 设置缓存文件
     */
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}

    /**
     * 是否开启磁盘缓存（默认开启）
     * @return 是否开启磁盘缓存
     */
    public boolean isEnableDiskCache() {
        return enableDiskCache;
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
     * 获取下载监听器
     */
    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.FAILED || status == Status.CANCELED;
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
        status = Status.CANCELED;
        return true;
    }

    @Override
    public void updateProgress(int totalLength, int completedLength) {
        if(progressListener != null){
            progressListener.onUpdateProgress(totalLength, completedLength);
        }
    }

    @Override
    public void run() {
        executeDownload();
    }

    @Override
    public void dispatch(RequestExecutor requestExecutor) {
        // 要先创建缓存文件
        if(isEnableDiskCache()){
            setCacheFile(getSpear().getConfiguration().getDiskCache().createCacheFile(this));
        }

        // 从网络下载
        requestExecutor.getNetTaskExecutor().execute(this);
        if(Spear.isDebugMode()){
            Log.d(Spear.TAG, NAME + " - dispatch：" + getName());
        }
    }

    /**
     * 执行下载
     */
    public void executeDownload() {
        if(isCanceled()){
            if(getDownloadListener() != null){
                getDownloadListener().onCanceled();
            }
            return;
        }

        setStatus(Request.Status.LOADING);
        ImageDownloader.DownloadResult downloadResult = getSpear().getConfiguration().getImageDownloader().download(this);

        if(isCanceled()){
            if(getDownloadListener() != null){
                getDownloadListener().onCanceled();
            }
            return;
        }

        if(downloadResult != null && downloadResult.getResult() == null){
            downloadResult = null;
        }

        if(downloadResult != null){
            if(!(this instanceof LoadRequest)){
                setStatus(Request.Status.COMPLETED);
            }
            if(getDownloadListener() != null){
                if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
                    getDownloadListener().onCompleted((File) downloadResult.getResult(), downloadResult.isFromNetwork()? DownloadListener.ImageFrom.NETWORK: DownloadListener.ImageFrom.LOCAL_CACHE);
                }else{
                    getDownloadListener().onCompleted((byte[]) downloadResult.getResult(), downloadResult.isFromNetwork()? DownloadListener.ImageFrom.NETWORK: DownloadListener.ImageFrom.LOCAL_CACHE);
                }
            }
        }else{
            if(!(this instanceof LoadRequest)){
                setStatus(Request.Status.FAILED);
            }
            if(getDownloadListener() != null){
                getDownloadListener().onFailed(null);
            }
        }
    }
}