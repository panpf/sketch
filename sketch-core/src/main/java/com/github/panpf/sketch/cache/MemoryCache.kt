/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.cache

import com.github.panpf.sketch.drawable.SketchRefBitmap
import kotlinx.coroutines.sync.Mutex

/**
 * 内存缓存管理器
 */
interface MemoryCache {

    /**
     * 获取已用容量
     */
    val size: Long

    /**
     * 获取最大容量
     */
    val maxSize: Long

    /**
     * 是否禁用
     */
    var isDisabled: Boolean

    /**
     * 是否已关闭
     */
    val isClosed: Boolean

    /**
     * 缓存一张图片
     *
     * @param key       缓存 key
     * @param refBitmap 待缓存图片
     */
    fun put(key: String, refBitmap: SketchRefBitmap)

    /**
     * 根据指定 key 获取图片
     *
     * @param key 缓存 key
     */
    operator fun get(key: String): SketchRefBitmap?

    /**
     * 根据指定 key 删除图片
     *
     * @param key 缓存 key
     */
    fun remove(key: String): SketchRefBitmap?

    /**
     * 根据 level 修整缓存
     *
     * @param level 修剪级别，对应 APP 的不同状态
     * @see android.content.ComponentCallbacks2
     */
    fun trimMemory(level: Int)

    /**
     * 清除缓存
     */
    fun clear()

    /**
     * 关闭，关闭后就彻底不能用了，如果你只是想暂时的关闭就使用 [isDisabled]
     */
    fun close()

    fun getOrCreateEditMutexLock(key: String): Mutex
}