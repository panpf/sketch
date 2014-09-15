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

package me.xiaopan.android.spear.download;

import me.xiaopan.android.spear.request.DownloadRequest;

/**
 * 下载器
 */
public interface ImageDownloader {
	/**
	 * 下载
	 */
	public Object download(DownloadRequest downloadRequest);
	
	/**
	 * 判断给定缓存文件地址的文件是否正在下载
	 */
	public boolean isDownloadingByCacheFilePath(String cacheFilePath);

    /**
     * 设置最大重试次数
     * @param maxRetryCount 最大重试次数，默认1
     */
    public void setMaxRetryCount(int maxRetryCount);

    /**
     * 设置超时时间
     * @param timeOut 超时时间，单位毫秒，默认15秒
     */
    public void setTimeout(int timeOut);
}
