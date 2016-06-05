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

package me.xiaopan.sketch.request;

/**
 * 下载选项
 */
public class DownloadOptions {
    private boolean disableCacheInDisk;
    private RequestLevel requestLevel;
    private RequestLevelFrom requestLevelFrom;

    public DownloadOptions() {
        reset();
    }

    @SuppressWarnings("unused")
    public DownloadOptions(DownloadOptions from) {
        copy(from);
    }

    /**
     * 是否禁用磁盘缓存
     */
    public boolean isDisableCacheInDisk() {
        return disableCacheInDisk;
    }

    /**
     * 设置是否禁用磁盘缓存
     */
    public DownloadOptions setDisableCacheInDisk(boolean disableCacheInDisk) {
        this.disableCacheInDisk = disableCacheInDisk;
        return this;
    }

    /**
     * 获取请求Level
     */
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求Level
     */
    public DownloadOptions setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    /**
     * 获取请求Level的来源
     */
    public RequestLevelFrom getRequestLevelFrom() {
        return requestLevelFrom;
    }

    /**
     * 设置请求Level的来源
     */
    DownloadOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        this.requestLevelFrom = requestLevelFrom;
        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        disableCacheInDisk = false;
        requestLevel = null;
        requestLevelFrom = null;
    }

    /**
     * 拷贝属性，绝对的覆盖
     */
    public void copy(DownloadOptions options) {
        if (options == null) {
            return;
        }

        disableCacheInDisk = options.disableCacheInDisk;
        requestLevel = options.requestLevel;
        requestLevelFrom = options.requestLevelFrom;
    }

    /**
     * 应用属性，应用的过程并不是绝对的覆盖
     */
    public void apply(DownloadOptions options) {
        if (options == null) {
            return;
        }

        if (!disableCacheInDisk) {
            disableCacheInDisk = options.disableCacheInDisk;
        }

        if (requestLevel == null) {
            requestLevel = options.requestLevel;
            requestLevelFrom = null;
        } else {
            RequestLevel optionRequestLevel = options.getRequestLevel();
            if (optionRequestLevel != null && optionRequestLevel.getLevel() < requestLevel.getLevel()) {
                requestLevel = optionRequestLevel;
                requestLevelFrom = null;
            }
        }
    }

    public StringBuilder appendOptionsToId(StringBuilder builder) {
        return builder;
    }
}
