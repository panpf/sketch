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
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

/**
 * A bitmap memory cache that manages the cache according to a least-used rule
 */
class LruMemoryCache constructor(override val maxSize: Long) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"
    }

    private val cache: LruCache<String, Value> =
        object : LruCache<String, Value>(maxSize) {
            override fun sizeOf(key: String, value: Value): Int {
                val valueSize = value.size
                return if (valueSize == 0) 1 else valueSize
            }

            override fun entryRemoved(
                evicted: Boolean, key: String, oldValue: Value, newValue: Value?
            ) {
                logger?.d(MODULE) {
                    "removed. ${oldValue}. ${size.formatFileSize()}"
                }
                oldValue.setIsCached(false)
            }
        }
    private val getCount = AtomicInteger()
    private val hitCount = AtomicInteger()

    override var logger: Logger? = null
    override val size: Long
        get() = cache.size()

    override fun put(key: String, value: Value): Boolean {
        require(value.checkValid()) { "cache value invalid. $value" }
        if (cache[key] != null) {
            logger?.w(MODULE, "put. exist. $value")
            return false
        }
        if (value.size >= maxSize * 0.7f) {
            logger?.d(MODULE) {
                val bitmapSize = value.size.formatFileSize()
                val maxSize = maxSize.formatFileSize()
                "put. reject. Bitmap too big: ${bitmapSize}, maxSize is $maxSize, $value"
            }
            return false
        }

        value.setIsCached(true)
        cache.put(key, value)
        logger?.d(MODULE) {
            "put. successful. ${size.formatFileSize()}. $value"
        }
        return true
    }

    override fun remove(key: String): Value? = cache.remove(key)

    override fun get(key: String): Value? {
        val value = cache[key]?.apply {
            require(this.checkValid()) { "cache value invalid. $this" }
        }
        val getCount1 = getCount.addAndGet(1)
        val hitCount1 = if (value != null) {
            hitCount.addAndGet(1)
        } else {
            hitCount.get()
        }
        if (getCount1 == Int.MAX_VALUE || hitCount1 == Int.MAX_VALUE) {
            getCount.set(0)
            hitCount.set(0)
        }
        logger?.d(MODULE) {
            val hitRatio = ((hitCount1.toFloat() / getCount1).format(2) * 100).roundToInt()
            if (value != null) {
                "get. hit($hitRatio%). ${value}}"
            } else {
                "get. miss($hitRatio%). $key"
            }
        }
        return value
    }

    override fun exist(key: String): Boolean {
        val value = cache[key]?.apply {
            require(this.checkValid()) { "cache value invalid. $this" }
        }
        return value != null
    }

    override fun trim(targetSize: Long) {
        val oldSize = size
        cache.trimToSize(targetSize)
        logger?.d(MODULE) {
            val releasedSize = oldSize - size
            "trim. released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
        }
    }

    override fun keys(): Set<String> {
        return cache.keys()
    }

    override fun clear() {
        val oldSize = size
        cache.evictAll()
        logger?.d(MODULE) {
            "clear. cleared ${oldSize.formatFileSize()}"
        }
    }

    override fun toString(): String = "$MODULE(${maxSize.formatFileSize()})"

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