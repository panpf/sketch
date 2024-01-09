/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.Logger

/**
 * Memory cache for bitmap
 */
interface MemoryCache {

    var logger: Logger?

    /**
     * Maximum allowed sum of the size of the all cache
     */
    val maxSize: Long

    /**
     * Sum of the size of the all cache
     */
    val size: Long

    /**
     * Caches [value] for [key]
     */
    fun put(key: String, value: Value): Boolean

    /**
     * Deletes the cache of the [key]
     * @return If null is returned, there is no cache
     */
    fun remove(key: String): Value?

    /**
     * Get the cache of the key
     */
    operator fun get(key: String): Value?

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
     * Get all cached keys
     */
    fun keys(): Set<String>

    /**
     * Clear all cached bitmaps
     */
    fun clear()

    // TODO In 4.0, a Map<String, Any> will be created to store these metadata.
    class Value constructor(
        val countBitmap: CountBitmap,
        val imageUri: String,
        val requestKey: String,
        val cacheKey: String,    // TODO remove
        val imageInfo: ImageInfo,
        /**
         * Store the transformation history of the Bitmap
         */
        val transformedList: List<String>?,
        /**
         * Store some additional information for consumer use
         */
        val extras: Map<String, String>?,
    )
}