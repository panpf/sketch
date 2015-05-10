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

package me.xiaopan.sketch;

/**
 * 下载选项
 */
public class DownloadOptions implements RequestOptions {
    private boolean enableDiskCache = true;	//是否开启磁盘缓存
    private RequestLevel requestLevel;

    public DownloadOptions(){

    }

    public DownloadOptions(DownloadOptions from){
        copyOf(from);
    }

    /**
     * 设置是否开启磁盘缓存
     * @param isEnableDiskCache 是否开启磁盘缓存
     * @return DownloadOptions
     */
    public DownloadOptions setEnableDiskCache(boolean isEnableDiskCache) {
        this.enableDiskCache = isEnableDiskCache;
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
     * 获取请求Level
     * @return 请求Level
     */
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求Level
     * @param requestLevel 请求Level
     */
    public DownloadOptions setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    public void copyOf(DownloadOptions downloadOptions){
        this.enableDiskCache = downloadOptions.isEnableDiskCache();
        this.requestLevel = downloadOptions.getRequestLevel();
    }
}
