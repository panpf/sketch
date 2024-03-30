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

import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.formatFileSize
import kotlin.math.roundToLong

/**
 * A bitmap memory cache that manages the cache according to a least-used rule
 *
 * LruMemoryCache is not thread-safe. If you need to use it in multiple threads, please handle the thread-safety issues yourself.
 */
class LruMemoryCache constructor(
    override val maxSize: Long,
    val valueLimitedSize: Long = (maxSize * 0.3f).roundToLong()
) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"
    }

    private val cache = object : LruCache<String, Value>(maxSize) {
        override fun sizeOf(key: String, value: Value): Long {
            val valueSize = value.size
            return if (valueSize == 0L) 1L else valueSize
        }
    }

    override val size: Long
        get() = cache.size

    private fun validateValue(value: Value) {
        require(value.checkValid()) { "Invalid value: $value" }
    }

    override fun put(key: String, value: Value): Int {
        validateValue(value)
        if (cache[key] != null) {
            return -1
        }
        val valueSize = value.size
        if (valueSize > valueLimitedSize) {
            return -2
        }
        cache.put(key, value)
        return 0
    }

    override fun remove(key: String): Value? = cache.remove(key)

    override fun get(key: String): Value? {
        val value = cache[key] ?: return null
        validateValue(value)
        return value
    }

    override fun exist(key: String): Boolean {
        val value = cache[key] ?: return false
        validateValue(value)
        return true
    }

    override fun trim(targetSize: Long) {
        cache.trimToSize(targetSize)
    }

    override fun keys(): Set<String> {
        return cache.keys
    }

    override fun clear() {
        cache.clear()
    }

    override fun toString(): String =
        "$MODULE(maxSize=${maxSize.formatFileSize()},valueLimitedSize=${valueLimitedSize.formatFileSize()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LruMemoryCache) return false
        if (maxSize != other.maxSize) return false
        return true
    }

    override fun hashCode(): Int {
        return maxSize.hashCode()
    }
}