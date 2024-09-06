/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.maxMemory
import kotlin.math.roundToLong

/**
 * Memory cache for [Image]
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest
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
     * Caches [value] for [key]
     *
     * @return 0: Success; -1: Exists; -2: Exceeds single cache size limit; -3: failed
     */
    fun put(key: String, value: Value): Int

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

    /**
     * Executes the given [action] under this mutex's lock.
     */
    suspend fun <R> withLock(key: String, action: suspend MemoryCache.() -> R): R

    /**
     * Memory cache value
     */
    interface Value {
        /**
         * Cached image
         */
        val image: Image

        /**
         * Size of the cache
         */
        val size: Long

        /**
         * Extra information
         */
        val extras: Map<String, Any?>?

        /**
         * Check if the cache is valid
         */
        fun checkValid(): Boolean
    }

    /**
     * Builder for [MemoryCache]
     *
     * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testBuilder
     */
    class Builder(val context: PlatformContext) {
        private var maxSizeBytes: Long? = null
        private var maxSizePercent: Double? = null

        /**
         * Set the maximum size of the memory cache in bytes.
         */
        fun maxSizeBytes(size: Long) = apply {
            require(size > 0L) { "size must be greater than 0L." }
            this.maxSizeBytes = size
        }

        /**
         * Set the maximum size of the memory cache as a percentage of this application's
         * available memory.
         */
        fun maxSizePercent(percent: Double) = apply {
            require(percent in 0.1..1.0) { "percent must be in the range [0.1, 1.0]." }
            this.maxSizePercent = percent
        }

        fun build(): MemoryCache {
            val maxSize = this.maxSizeBytes
                ?: this.maxSizePercent?.let { (context.maxMemory() * it).roundToLong() }
                ?: context.defaultMemoryCacheSize()
            return LruMemoryCache(maxSize)
        }
    }
}

/**
 * Returns the default memory cache size
 *
 * @see com.github.panpf.sketch.core.android.test.cache.MemoryCacheAndroidTest.testDefaultMemoryCacheSize
 * @see com.github.panpf.sketch.core.desktop.test.cache.MemoryCacheDesktopTest.testDefaultMemoryCacheSize
 * @see com.github.panpf.sketch.core.ios.test.cache.MemoryCacheIosTest.testDefaultMemoryCacheSize
 * @see com.github.panpf.sketch.core.jscommon.test.cache.MemoryCacheJsCommonTest.testDefaultMemoryCacheSize
 */
internal expect fun PlatformContext.defaultMemoryCacheSize(): Long

/**
 * Memory cache key
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testMemoryCacheKey
 */
val RequestContext.memoryCacheKey: String
    get() = cacheKey

/**
 * Get the image information from the cache value
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testGetImageInfo
 */
fun MemoryCache.Value.getImageInfo(): ImageInfo? {
    return extras?.get("imageInfo") as? ImageInfo
}

/**
 * Get the resize from the cache value
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testGetResize
 */
fun MemoryCache.Value.getResize(): Resize? {
    return extras?.get("resize") as? Resize
}

/**
 * Get the transformed list from the cache value
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testGetTransformeds
 */
fun MemoryCache.Value.getTransformeds(): List<String>? {
    @Suppress("UNCHECKED_CAST")
    return extras?.get("transformeds") as? List<String>
}

/**
 * Get the extras from the cache value
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testGetExtras
 */
fun MemoryCache.Value.getExtras(): Map<String, String>? {
    @Suppress("UNCHECKED_CAST")
    return extras?.get("extras") as? Map<String, String>
}

/**
 * Create a new cache value extras
 *
 * @see com.github.panpf.sketch.core.common.test.cache.MemoryCacheTest.testNewCacheValueExtras
 */
fun newCacheValueExtras(
    imageInfo: ImageInfo,
    resize: Resize,
    transformeds: List<String>?,
    extras: Map<String, String>?,
): Map<String, Any?> {
    return mapOf(
        "imageInfo" to imageInfo,
        "resize" to resize,
        "transformeds" to transformeds,
        "extras" to extras,
    )
}