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

import com.github.panpf.sketch.util.Logger
import kotlinx.coroutines.sync.Mutex

/**
 * Memory cache for bitmap
 */
interface MemoryCache {

    /**
     * Maximum allowed sum of the size of the all cache
     */
    val maxSize: Long

    /**
     * Sum of the size of the all cache
     */
    val size: Long

    /**
     * Caches [countBitmap] for [key]
     */
    fun put(key: String, countBitmap: CountBitmap): Boolean

    /**
     * Deletes the cache of the [key]
     * @return If null is returned, there is no cache
     */
    fun remove(key: String): CountBitmap?

    /**
     * Get the cache of the key
     */
    operator fun get(key: String): CountBitmap?

    /**
     * Returns exist of the entry named [key]
     */
    fun exist(key: String): Boolean

    /**
     * Trim memory based on the [level]
     *
     * @param level see [android.content.ComponentCallbacks2].TRIM_MEMORY_*
     * @see android.content.ComponentCallbacks2
     */
    fun trim(level: Int)

    /**
     * Clear all cached bitmaps
     */
    fun clear()

    var logger: Logger?
}