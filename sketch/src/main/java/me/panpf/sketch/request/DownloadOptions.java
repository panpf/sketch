/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.state.StateImage;
import me.panpf.sketch.uri.UriModel;

/**
 * 下载选项，适用于 {@link Sketch#download(String, DownloadListener)} 方法
 */
public class DownloadOptions {
    /**
     * 禁用磁盘缓存
     */
    private boolean cacheInDiskDisabled;

    /**
     * 请求 level，限制请求处理深度，参考 {@link RequestLevel}
     */
    private RequestLevel requestLevel;

    public DownloadOptions() {
        reset();
    }

    /**
     * 从指定的 {@link DownloadOptions} 中拷贝所有属性来创建新的 {@link DownloadOptions}
     *
     * @param from 从这个 {@link DownloadOptions} 里拷贝属性
     */
    @SuppressWarnings("unused")
    public DownloadOptions(@NonNull DownloadOptions from) {
        copy(from);
    }

    /**
     * 获取请求 level
     *
     * @return {@link RequestLevel}
     */
    @Nullable
    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    /**
     * 设置请求 level，限制请求处理深度，参考 {@link RequestLevel}
     *
     * @param requestLevel {@link RequestLevel}
     * @return {@link DownloadOptions}. 为了支持链式调用
     */
    @NonNull
    public DownloadOptions setRequestLevel(@Nullable RequestLevel requestLevel) {
        this.requestLevel = requestLevel;
        return this;
    }

    /**
     * 是否禁用磁盘缓存
     */
    public boolean isCacheInDiskDisabled() {
        return cacheInDiskDisabled;
    }

    /**
     * 设置是否禁用磁盘缓存
     *
     * @param cacheInDiskDisabled 禁用磁盘缓存
     * @return {@link DownloadOptions}. 为了支持链式调用
     */
    @NonNull
    public DownloadOptions setCacheInDiskDisabled(boolean cacheInDiskDisabled) {
        this.cacheInDiskDisabled = cacheInDiskDisabled;
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
     * 从指定的 {@link DownloadOptions} 中拷贝属性，绝对的覆盖
     */
    public void copy(@Nullable DownloadOptions options) {
        if (options == null) {
            return;
        }

        cacheInDiskDisabled = options.cacheInDiskDisabled;
        requestLevel = options.requestLevel;
    }

    /**
     * 生成选项 key，用于组装请求或内存缓存 key
     *
     * @see SketchImageView#getOptionsKey()
     * @see me.panpf.sketch.util.SketchUtils#makeRequestKey(String, UriModel, String)
     */
    @NonNull
    public String makeKey() {
        return "";
    }

    /**
     * 生成 {@link StateImage} 用的选项 key，用于组装 {@link StateImage} 的内存缓存 key
     *
     * @see me.panpf.sketch.util.SketchUtils#makeRequestKey(String, UriModel, String)
     */
    @NonNull
    public String makeStateImageKey() {
        return "";
    }
}
