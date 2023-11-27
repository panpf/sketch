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

import android.content.ComponentCallbacks2
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.getTrimLevelName
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
        object : LruCache<String, Value>(maxSize.toInt()) {
            override fun sizeOf(key: String, value: Value): Int {
                val bitmapSize = value.countBitmap.byteCount
                return if (bitmapSize == 0) 1 else bitmapSize
            }

            override fun entryRemoved(evicted: Boolean, key: String, old: Value, new: Value?) {
                logger?.d(MODULE) {
                    "removed. ${old.countBitmap}. ${size.formatFileSize()}"
                }
                old.countBitmap.setIsCached(false, MODULE)
            }
        }
    private val getCount = AtomicInteger()
    private val hitCount = AtomicInteger()

    override var logger: Logger? = null
    override val size: Long
        get() = cache.size().toLong()

    override fun put(key: String, value: Value): Boolean {
        val countBitmap = value.countBitmap
        val bitmap = countBitmap.bitmap ?: return false
//        cache.snapshot().values.forEach {
//            if (it.bitmap === bitmap) {
//                throw IllegalArgumentException("Same Bitmap, different CountBitmap. ${countBitmap.info}")
//            }
//        }
        if (cache[key] != null) {
            logger?.w(MODULE, "put. exist. $countBitmap")
            return false
        }
        if (bitmap.allocationByteCountCompat >= maxSize * 0.7f) {
            logger?.d(MODULE) {
                val bitmapSize = bitmap.allocationByteCountCompat.formatFileSize()
                val maxSize = maxSize.formatFileSize()
                "put. reject. Bitmap too big: ${bitmapSize}, maxSize is $maxSize, $countBitmap"
            }
            return false
        }

        countBitmap.setIsCached(true, MODULE)
        cache.put(key, value)
        logger?.d(MODULE) {
            "put. successful. ${size.formatFileSize()}. $countBitmap"
        }
        return true
    }

    override fun remove(key: String): Value? = cache.remove(key)

    override fun get(key: String): Value? {
        val value = cache[key]?.takeIf {
            val recycled = it.countBitmap.isRecycled
            if (recycled) {
                cache.remove(key)
            }
            !recycled
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
                "get. hit($hitRatio%). ${value.countBitmap}}"
            } else {
                "get. miss($hitRatio%). $key"
            }
        }
        return value
    }


    override fun exist(key: String): Boolean {
        val value = cache[key]?.takeIf {
            val recycled = it.countBitmap.isRecycled
            if (recycled) {
                cache.remove(key)
            }
            !recycled
        }
        return value != null
    }


    override fun trim(level: Int) {
        val oldSize = size
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            cache.evictAll()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.maxSize() / 2)
        }
        logger?.d(MODULE) {
            val releasedSize = oldSize - size
            "trim. level '${getTrimLevelName(level)}', released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
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