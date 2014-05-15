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

package me.xiaopan.android.imageloader.task.download;

import me.xiaopan.android.imageloader.task.TaskOptions;

/**
 * 下载选项
 */
public class DownloadOptions extends TaskOptions{
	private int maxRetryCount = 2;	//最大重试次数
	private long diskCachePeriodOfValidity;	//磁盘缓存有效期，单位毫秒
	private boolean enableDiskCache = true;	//是否开启磁盘缓存
	private boolean enableProgressCallback;	// 开启进度回调
	
	/**
	 * 获取最大重试次数
	 * @return 最大重试次数
	 */
	public int getMaxRetryCount() {
		return maxRetryCount;
	}
	
	/**
	 * 设置最大重试次数
	 * @param maxRetryCount 最大重试次数
	 */
	public DownloadOptions setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
        return this;
	}
	
	/**
	 * 是否将网络上的图片缓存到本地
	 * @return 是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 */
	public boolean isEnableDiskCache() {
		return enableDiskCache;
	}

	/**
	 * 设置是否将网络上的图片缓存到本地
	 * @param enableDiskCache 是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 */
	public DownloadOptions setEnableDiskCache(boolean enableDiskCache) {
		this.enableDiskCache = enableDiskCache;
        return this;
	}

	/**
	 * 获取本地缓存文件的有效时间
	 * @return 本地缓存文件的有效时间，单位毫秒
	 */
	public long getDiskCachePeriodOfValidity() {
		return diskCachePeriodOfValidity;
	}

	/**
	 * 设置本地缓存文件的有效时间
	 * @param diskCachePeriodOfValidity 本地缓存文件的有效时间，单位毫秒
	 */
	public DownloadOptions setDiskCachePeriodOfValidity(long diskCachePeriodOfValidity) {
		this.diskCachePeriodOfValidity = diskCachePeriodOfValidity;
        return this;
	}

	/**
	 * 是否开启进度回调功能
	 */
    public boolean isEnableProgressCallback() {
		return enableProgressCallback;
	}

    /**
     * 设置是否开启进度回调功能
	 * @param enableProgressCallback 是否开启进度回调功能
     */
	public DownloadOptions setEnableProgressCallback(boolean enableProgressCallback) {
		this.enableProgressCallback = enableProgressCallback;
        return this;
	}

	/**
	 * 拷贝一份
	 */
	public DownloadOptions copy(){
        return new DownloadOptions()
            .setMaxRetryCount(maxRetryCount)
            .setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity())
            .setEnableDiskCache(isEnableDiskCache())
            .setEnableProgressCallback(enableProgressCallback);
	}
}
