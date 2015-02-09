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
import me.xiaopan.android.spear.util.ImageScheme;

/**
 * 下载请求
 */
public class DownloadRequest implements Request {
    public static final boolean DEFAULT_ENABLE_DISK_CACHE = true;

    /* 任务基础属性 */
    private Status status = Status.WAITING;  // 状态

    /* 必须属性 */
    private File cacheFile;	// 缓存文件
    private Spear spear;
    private String uri;	// 图片地址
    private String name;	// 名称，用于在输出LOG的时候区分不同的请求
    private ImageScheme imageScheme;	// Uri协议格式
    private DownloadListener downloadListener;  // 下载监听器
    private ProgressListener downloadProgressListener;  // 下载进度监听器

    /* 可配置属性 */
    private boolean enableDiskCache = DEFAULT_ENABLE_DISK_CACHE;	// 是否开启磁盘缓存

    @Override
	public String getName() {
		return name;
	}

    @Override
    public String getUri() {
        return uri;
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
	public ImageScheme getImageScheme() {
		return imageScheme;
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

    /**
     * 设置Spear
     */
    public void setSpear(Spear spear) {
        this.spear = spear;
    }

    /**
     * 设置uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 设置名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置图片协议类型
     */
    public void setImageScheme(ImageScheme imageScheme) {
        this.imageScheme = imageScheme;
    }

    /**
     * 设置开启或关闭磁盘缓存功能
     */
    public void setEnableDiskCache(boolean enableDiskCache) {
        this.enableDiskCache = enableDiskCache;
    }
}