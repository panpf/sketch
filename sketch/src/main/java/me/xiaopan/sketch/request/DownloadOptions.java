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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.uri.UriModel;

/**
 * 下载选项，适用于 {@link me.xiaopan.sketch.Sketch#download(String, DownloadListener)} 方法
 */
public class DownloadOptions {
    /**
     * 禁用磁盘缓存
     */
    private boolean cacheInDiskDisabled;

    /**
     * 请求 Level
     */
    private RequestLevel requestLevel;

    public DownloadOptions() {
        reset();
    }

    /**
     * 从指定的 DownloadOptions 中拷贝所有属性来创建新的 DownloadOptions
     *
     * @param from 从这个 DownloadOptions 里拷贝属性
     */
    @SuppressWarnings("unused")
    public DownloadOptions(@NonNull DownloadOptions from) {
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
    @NonNull
    public DownloadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        this.cacheInDiskDisabled = cacheInDiskDisabled;
        return this;
    }

    /**
     * 获取请求Level
     *
     * @see RequestLevel
     */
    @Nullable
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求Level
     *
     * @param requestLevel {@link RequestLevel}
     * @return this
     * @see RequestLevel
     */
    @NonNull
    public DownloadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    /**
     * 重置所有属性
     */
    public void reset() {
        cacheInDiskDisabled = false;
        requestLevel = null;
    }

    /**
     * 从指定的DownloadOptions中拷贝属性，绝对的覆盖
     */
    public void copy(@Nullable DownloadOptions options) {
        if (options == null) {
            return;
        }

        cacheInDiskDisabled = options.cacheInDiskDisabled;
        requestLevel = options.requestLevel;
    }

    /**
     * 生成选项KEY，用于组装请求或内存缓存key
     *
     * @see SketchImageView#getOptionsKey()
     * @see me.xiaopan.sketch.util.SketchUtils#makeRequestKey(String, UriModel, DownloadOptions)
     */
    @NonNull
    public String makeKey() {
        return "";
    }

    /**
     * 生成StateImage用的选项KEY，用于组装StateImage的内存缓存KEY
     *
     * @see me.xiaopan.sketch.util.SketchUtils#makeStateImageMemoryCacheKey(String, DownloadOptions)
     */
    @NonNull
    public String makeStateImageKey() {
        return "";
    }
}
