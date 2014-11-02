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

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.task.Task;
import me.xiaopan.android.spear.util.Scheme;

/**
 * 下载请求
 */
public class DownloadRequest implements Request {
    /* 任务基础属性 */
    private Task task;	// 执行当前请求的任务，由于一个请求可能辗转被好几个任务处理
    private Status status = Status.WAITING;  // 状态

    /* 必须属性 */
    private File cacheFile;	// 缓存文件
    Spear spear;
    String uri;	// 图片地址
    String name;	// 名称，用于在输出LOG的时候区分不同的请求
    Scheme scheme;	// Uri协议格式
	DownloadListener downloadListener;  // 下载监听器
    ProgressListener downloadProgressListener;  // 下载进度监听器

    /* 可配置属性 */
    long diskCacheTimeout;	// 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
    boolean enableDiskCache;	// 是否开启磁盘缓存

    @Override
	public String getName() {
		return name;
	}

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
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
        if(task != null){
            task.cancel(true);
        }
        return true;
    }

    /**
     * 获取Spear
     * @return Spear
     */
    public Spear getSpear() {
        return spear;
    }

	/**
	 * 获取Uri协议类型
	 */
	public Scheme getScheme() {
		return scheme;
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
     * 获取下载监听器
     */
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

    /**
     * 设置下载监听器
     * @param downloadListener 下载监听器
     */
    public DownloadRequest setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    /**
     * 获取磁盘缓存超时时间
     * @return 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
     */
    public long getDiskCacheTimeout() {
        return diskCacheTimeout;
    }

    /**
     * 是否开启磁盘缓存
     * @return 是否开启磁盘缓存
     */
    public boolean isEnableDiskCache() {
        return enableDiskCache;
    }

    /**
     * 获取下载进度监听器
     * @return 下载进度监听器
     */
    public ProgressListener getDownloadProgressListener() {
        return downloadProgressListener;
    }

    /**
     * 设置下载进度监听器
     * @param downloadProgressListener 下载进度监听器
     */
    public void setDownloadProgressListener(ProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }
}