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
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.totalAvailableMemoryBytes
import kotlin.math.roundToLong

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

    interface Value {

        val image: Image

        val size: Long

        val extras: Map<String, Any?>?

        fun checkValid(): Boolean
    }

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
        fun maxSizePercent(
            percent: Double = context.platformDefaultMemoryCacheSizePercent()
        ) = apply {
            require(percent in 0.1..1.0) { "percent must be in the range [0.1, 1.0]." }
            this.maxSizePercent = percent
        }

        fun build(): MemoryCache {
            val maxSizeBytes = maxSizeBytes
            val maxSizePercent = maxSizePercent
            val finalMaxSizeBytes = if (maxSizeBytes != null) {
                maxSizeBytes
            } else {
                val totalAvailableMemoryBytes = context.totalAvailableMemoryBytes()
                val finalMaxSizePercent =
                    maxSizePercent ?: context.platformDefaultMemoryCacheSizePercent()
                (totalAvailableMemoryBytes * finalMaxSizePercent).roundToLong()
            }
            return LruMemoryCache(finalMaxSizeBytes)
        }
    }
}

/** Return the default percent of the application's total memory to use for the memory cache. */
internal expect fun PlatformContext.platformDefaultMemoryCacheSizePercent(): Double

val RequestContext.memoryCacheKey: String
    get() = cacheKey

fun MemoryCache.Value.getImageInfo(): ImageInfo? {
    return extras?.get("imageInfo") as? ImageInfo
}

fun MemoryCache.Value.getResize(): Resize? {
    return extras?.get("resize") as? Resize
}

fun MemoryCache.Value.getTransformeds(): List<String>? {
    @Suppress("UNCHECKED_CAST")
    return extras?.get("transformeds") as? List<String>
}

fun MemoryCache.Value.getExtras(): Map<String, String>? {
    @Suppress("UNCHECKED_CAST")
    return extras?.get("extras") as? Map<String, String>
}

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