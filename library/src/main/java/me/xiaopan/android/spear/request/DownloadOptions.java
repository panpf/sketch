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

/**
 * 下载选项
 */
public class DownloadOptions implements RequestOptions {
    private long diskCacheTimeout;	//磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
    private boolean enableDiskCache = true;	//是否开启磁盘缓存

    /**
     * 关闭磁盘缓存
     * @return DownloadOptions
     */
    public DownloadOptions disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置磁盘缓存超时时间
     * @param diskCacheTimeout 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
     * @return DownloadOptions
     */
    public DownloadOptions diskCacheTimeout(long diskCacheTimeout) {
        this.diskCacheTimeout = diskCacheTimeout;
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
}
