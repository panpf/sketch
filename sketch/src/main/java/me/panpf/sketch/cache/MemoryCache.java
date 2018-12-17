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

package me.panpf.sketch.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.drawable.SketchRefBitmap;

/**
 * 内存缓存管理器
 */
public interface MemoryCache {
    /**
     * 缓存一张图片
     *
     * @param key       缓存 key
     * @param refBitmap 待缓存图片
     */
    void put(@NonNull String key, @NonNull SketchRefBitmap refBitmap);

    /**
     * 根据指定 key 获取图片
     *
     * @param key 缓存 key
     */
    @Nullable
    SketchRefBitmap get(@NonNull String key);

    /**
     * 根据指定 key 删除图片
     *
     * @param key 缓存 key
     */
    @Nullable
    SketchRefBitmap remove(@NonNull String key);

    /**
     * 获取已用容量
     */
    long getSize();

    /**
     * 获取最大容量
     */
    long getMaxSize();

    /**
     * 根据 level 修整缓存
     *
     * @param level 修剪级别，对应 APP 的不同状态
     * @see android.content.ComponentCallbacks2
     */
    void trimMemory(int level);

    /**
     * 是否禁用
     */
    @SuppressWarnings("unused")
    boolean isDisabled();

    /**
     * 设置是否禁用
     *
     * @param disabled 是否禁用
     */
    void setDisabled(boolean disabled);

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 是否已关闭
     */
    @SuppressWarnings("unused")
    boolean isClosed();

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 {@link #setDisabled(boolean)}
     */
    void close();
}