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
package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.formatFileSize
import kotlin.math.roundToLong

/**
 * A bitmap memory cache that manages the cache according to a least-used rule
 */
class LruMemoryCache constructor(
    override val maxSize: Long,
    val valueLimitedSize: Long = (maxSize * 0.3f).roundToLong()
) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"
    }

    private val cache: LruCache<String, Value> =
        object : LruCache<String, Value>(maxSize) {
            override fun sizeOf(key: String, value: Value): Int {
                val valueSize = value.size
                return if (valueSize == 0) 1 else valueSize
            }
        }

    override var logger: Logger? = null
    override val size: Long
        get() = cache.size()

    override fun put(key: String, value: Value): Boolean {
        if (!value.checkValid()) {
            logger?.w(MODULE, "put. invalid. $value. $key")
            return false
        }
        if (cache[key] != null) {
            logger?.w(MODULE, "put. exist. $value. $key")
            return false
        }
        val valueSize = value.size
        if (valueSize > valueLimitedSize) {
            logger?.w(MODULE) {
                "put. value size exceeds limited. valueSize=${valueSize.formatFileSize()}, $value. $key"
            }
            return false
        }
        cache.put(key, value)
        return true
    }

    override fun remove(key: String): Value? = cache.remove(key)

    override fun get(key: String): Value? {
        val value = cache[key] ?: return null
        if (!value.checkValid()) {
            logger?.w(MODULE, "get. invalid. $value. $key")
            return null
        }
        return value
    }

    override fun exist(key: String): Boolean {
        val value = cache[key] ?: return false
        if (!value.checkValid()) {
            logger?.w(MODULE, "exist. invalid. $value. $key")
            return false
        }
        return true
    }

    override fun trim(targetSize: Long) {
        val oldSize = size
        cache.trimToSize(targetSize)
        logger?.d(MODULE) {
            val releasedSize = oldSize - size
            "trim. targetSize=${targetSize.formatFileSize()}, " +
                    "releasedSize=${releasedSize.formatFileSize()}, " +
                    "size=${size.formatFileSize()}"
        }
    }

    override fun keys(): Set<String> {
        return cache.keys()
    }

    override fun clear() {
        val oldSize = size
        cache.evictAll()
        logger?.d(MODULE) {
            "clear. clearedSize=${oldSize.formatFileSize()}"
        }
    }

    override fun toString(): String =
        "$MODULE(maxSize=${maxSize.formatFileSize()},valueLimitedSize=${valueLimitedSize.formatFileSize()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LruMemoryCache
        if (maxSize != other.maxSize) return false
        return true
    }

    override fun hashCode(): Int {
        return maxSize.hashCode()
    }
}