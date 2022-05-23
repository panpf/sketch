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
package com.github.panpf.sketch.cache.internal

import android.content.ComponentCallbacks2
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.toHexString
import kotlinx.coroutines.sync.Mutex
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * A bitmap memory cache that manages the cache according to a least-used rule
 */
class LruMemoryCache constructor(override val maxSize: Long) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"

        @JvmStatic
        private val editLockLock = Any()
    }

    private val cache: LruCache<String, CountBitmap> = CountBitmapLruCache(maxSize)
    private val editLockMap: MutableMap<String, Mutex> = WeakHashMap()
    private val getCount = AtomicInteger()
    private val hitCount = AtomicInteger()

    override var logger: Logger? = null
    override val size: Long
        get() = cache.size().toLong()

    override fun put(key: String, countBitmap: CountBitmap): Boolean {
        return if (cache[key] == null) {
            cache.put(key, countBitmap)
            logger?.d(MODULE) {
                "put. ${countBitmap.info}. ${size.formatFileSize()}. $key"
            }
            true
        } else {
            logger?.w(MODULE, "Exist. key=$key")
            false
        }
    }

    override fun remove(key: String): CountBitmap? =
        cache.remove(key).apply {
            logger?.d(MODULE) {
                "remove. ${this.info}. ${size.formatFileSize()}. $key"
            }
        }

    override fun get(key: String): CountBitmap? =
        cache[key]?.takeIf {
            (!it.isRecycled).apply {
                if (!this) {
                    cache.remove(key)
                }
            }
        }.apply {
            val getCount1 = getCount.addAndGet(1)
            val hitCount1 = if (this != null) {
                hitCount.addAndGet(1)
            } else {
                hitCount.get()
            }
            if (getCount1 == Int.MAX_VALUE || hitCount1 == Int.MAX_VALUE) {
                getCount.set(0)
                hitCount.set(0)
            }
            logger?.d(MODULE) {
                if (this != null) {
                    val hitRatio = (hitCount1.toFloat() / getCount1).format(2)
                    "get. Hit(${hitRatio}). ${this.info}/${this.bitmap!!.toHexString()}. $key"
                } else {
                    val hitRatio = (hitCount1.toFloat() / getCount1).format(2)
                    "get. NoHit(${hitRatio}). $key"
                }
            }
        }

    override fun exist(key: String): Boolean =
        cache[key]?.takeIf {
            (!it.isRecycled).apply {
                if (!this) {
                    cache.remove(key)
                }
            }
        } != null

    override fun trim(level: Int) {
        val oldSize = size
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            cache.evictAll()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.maxSize() / 2)
        }
        val releasedSize = oldSize - size
        logger?.w(
            MODULE,
            "trim. level '${getTrimLevelName(level)}', released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
        )
    }

    override fun clear() {
        val oldSize = size
        cache.evictAll()
        logger?.w(MODULE, "clear. cleared ${oldSize.formatFileSize()}")
    }

    override fun editLock(key: String): Mutex = synchronized(editLockLock) {
        editLockMap[key] ?: Mutex().apply {
            this@LruMemoryCache.editLockMap[key] = this
        }
    }

    override fun toString(): String = "$MODULE(maxSize=${maxSize.formatFileSize()})"

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


    private class CountBitmapLruCache constructor(maxSize: Long) :
        LruCache<String, CountBitmap>(maxSize.toInt()) {

        override fun put(key: String, countBitmap: CountBitmap): CountBitmap? {
            countBitmap.setIsCached(true, MODULE)
            return super.put(key, countBitmap)
        }

        override fun sizeOf(key: String, countBitmap: CountBitmap): Int {
            val bitmapSize = countBitmap.byteCount
            return if (bitmapSize == 0) 1 else bitmapSize
        }

        override fun entryRemoved(
            evicted: Boolean, key: String, oldCountBitmap: CountBitmap, newCountBitmap: CountBitmap?
        ) {
            oldCountBitmap.setIsCached(false, MODULE)
        }
    }
}