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

import me.xiaopan.sketch.SketchImageView;

/**
 * 下载选项，适用于 {@link me.xiaopan.sketch.Sketch#download(String, DownloadListener)} 方法
 */
public class DownloadOptions {
    /**
     * 禁用磁盘缓存
     */
    private boolean cacheInDiskDisabled;

    /**
     * 请求Level
     */
    private RequestLevel requestLevel;

    /**
     * 请求Level的来源
     */
    private RequestLevelFrom requestLevelFrom;

    public DownloadOptions() {
        reset();
    }

    /**
     * 从指定的DownloadOptions中拷贝所有属性来创建新的DownloadOptions
     *
     * @param from 从这个DownloadOptions里拷贝属性
     */
    @SuppressWarnings("unused")
    public DownloadOptions(DownloadOptions from) {
        copy(from);
    }

    /**
     * 不使用磁盘缓存？
     */
    public boolean isCacheInDiskDisabled() {
        return cacheInDiskDisabled;
    }

    /**
     * 设置不使用磁盘缓存
     *
     * @param cacheInDiskDisabled 不使用磁盘缓存
     * @return this
     */
    public DownloadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        this.cacheInDiskDisabled = cacheInDiskDisabled;
        return this;
    }

    /**
     * 获取请求Level
     *
     * @see RequestLevel
     */
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求Level
     *
     * @param requestLevel {@link RequestLevelFrom}
     * @return this
     * @see RequestLevel
     */
    public DownloadOptions setRequestLevel(RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    /**
     * 获取请求Level的来源
     *
     * @see RequestLevelFrom
     */
    public RequestLevelFrom getRequestLevelFrom() {
        return requestLevelFrom;
    }

    /**
     * 设置请求Level的来源
     *
     * @param requestLevelFrom {@link RequestLevelFrom}
     * @return this
     * @see RequestLevelFrom
     */
    DownloadOptions setRequestLevelFrom(RequestLevelFrom requestLevelFrom) {
        this.requestLevelFrom = requestLevelFrom;
        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        cacheInDiskDisabled = false;
        requestLevel = null;
        requestLevelFrom = null;
    }

    /**
     * 从指定的DownloadOptions中拷贝属性，绝对的覆盖
     */
    public void copy(DownloadOptions options) {
        if (options == null) {
            return;
        }

        cacheInDiskDisabled = options.cacheInDiskDisabled;
        requestLevel = options.requestLevel;
        requestLevelFrom = options.requestLevelFrom;
    }

    /**
     * 合并指定的DownloadOptions，合并的过程并不是绝对的覆盖，专门为{@link DownloadHelper#options(DownloadOptions)}方法提供
     * <br>简单来说自己已经设置了的属性不会被覆盖，对于都设置了但可以比较大小的，较小的优先
     */
    public void merge(DownloadOptions options) {
        if (options == null) {
            return;
        }

        if (!cacheInDiskDisabled) {
            cacheInDiskDisabled = options.cacheInDiskDisabled;
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

    /**
     * 生成选项KEY，用于组装请求或内存缓存key
     *
     * @see SketchImageView#getOptionsKey()
     * @see me.xiaopan.sketch.util.SketchUtils#makeRequestKey(String, DownloadOptions)
     */
    public StringBuilder makeKey(StringBuilder builder) {
        return builder;
    }

    /**
     * 生成StateImage用的选项KEY，用于组装StateImage的内存缓存KEY
     *
     * @see me.xiaopan.sketch.util.SketchUtils#makeStateImageMemoryCacheKey(String, DownloadOptions)
     */
    public StringBuilder makeStateImageKey(StringBuilder builder) {
        return builder;
    }
}
