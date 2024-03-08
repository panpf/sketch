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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.cache.internal.LruMemoryCache
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.defaultMemoryCacheSizePercent
import com.github.panpf.sketch.util.totalAvailableMemoryBytes
import kotlin.math.roundToLong

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
     * Trim memory based [targetSize]
     */
    fun trim(targetSize: Long)

    /**
     * Get all cached keys
     */
    fun keys(): Set<String>

    /**
     * Clear all cached bitmaps
     */
    fun clear()

    interface Value {

        val image: Image

        val size: Int

        val extras: Map<String, Any?>

        fun checkValid(): Boolean
    }

    data class Options(val maxSize: Long)

    fun interface Factory {
        fun create(context: PlatformContext): MemoryCache
    }

    class OptionsFactory(
        private val lazyOptions: (PlatformContext) -> Options
    ) : Factory {
        override fun create(context: PlatformContext): MemoryCache {
            val options = lazyOptions(context)
            return LruMemoryCache(maxSize = options.maxSize)
        }
    }

    class DefaultFactory : Factory {
        override fun create(context: PlatformContext): MemoryCache {
            val defaultMemoryCacheBytes =
                context.totalAvailableMemoryBytes() * context.defaultMemoryCacheSizePercent()
            return LruMemoryCache((defaultMemoryCacheBytes * 1f).roundToLong())
        }
    }
}