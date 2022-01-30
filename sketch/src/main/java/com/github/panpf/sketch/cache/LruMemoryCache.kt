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

import android.content.ComponentCallbacks2
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.trimLevelName
import kotlinx.coroutines.sync.Mutex
import java.util.WeakHashMap

/**
 * A bitmap memory cache that manages the cache according to a least-used rule
 */
class LruMemoryCache constructor(private val logger: Logger, maxSize: Long) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"

        @JvmStatic
        private val editLockLock = Any()
    }

    private val cache: LruCache<String, CountBitmap> = CountBitmapLruCache(maxSize)
    private val editLockMap: MutableMap<String, Mutex> = WeakHashMap()

    override val size: Long
        get() = cache.size().toLong()
    override val maxSize: Long
        get() = cache.maxSize().toLong()

    override fun put(key: String, countBitmap: CountBitmap) {
        if (cache[key] == null) {
            cache.put(key, countBitmap)
            logger.d(MODULE) {
                val bitmapSize = countBitmap.byteCount.toLong().formatFileSize()
                "put. key '$key', bitmap $bitmapSize, size ${size.formatFileSize()}"
            }
        } else {
            logger.w(MODULE, String.format("Exist. key=$key"))
        }
    }

    override fun remove(key: String): CountBitmap? =
        cache.remove(key).apply {
            logger.d(MODULE) {
                val bitmapSize = this?.byteCount?.toLong()?.formatFileSize()
                "remove. key '$key', bitmap $bitmapSize, size ${size.formatFileSize()}"
            }
        }

    override fun get(key: String): CountBitmap? =
        cache[key]?.takeIf {
            (!it.isRecycled).apply {
                if (!this) {
                    cache.remove(key)
                }
            }
        }

    override fun trim(level: Int) {
        val oldSize = size
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            cache.evictAll()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.maxSize() / 2)
        }
        val newSize = size
        val releasedSize = oldSize - newSize
        logger.w(
            MODULE,
            "trim. level '${trimLevelName(level)}', released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
        )
    }

    override fun clear() {
        val oldSize = size
        cache.evictAll()
        logger.w(MODULE, "clear. cleared ${oldSize.formatFileSize()}")
    }

    override fun editLock(key: String): Mutex = synchronized(editLockLock) {
        editLockMap[key] ?: Mutex().apply {
            this@LruMemoryCache.editLockMap[key] = this
        }
    }

    override fun toString(): String = "${MODULE}(maxSize=${maxSize.formatFileSize()})"

    private class CountBitmapLruCache constructor(maxSize: Long) :
        LruCache<String, CountBitmap>(maxSize.toInt()) {

        override fun put(key: String, countBitmap: CountBitmap): CountBitmap? {
            countBitmap.setIsCached("${MODULE}:put", true)
            return super.put(key, countBitmap)
        }

        override fun sizeOf(key: String, countBitmap: CountBitmap): Int {
            val bitmapSize = countBitmap.byteCount
            return if (bitmapSize == 0) 1 else bitmapSize
        }

        override fun entryRemoved(
            evicted: Boolean, key: String, oldCountBitmap: CountBitmap, newCountBitmap: CountBitmap?
        ) {
            oldCountBitmap.setIsCached("${MODULE}:entryRemoved", false)
        }
    }
}