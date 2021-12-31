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
import android.content.Context
import android.text.format.Formatter
import com.github.panpf.sketch.drawable.SketchRefBitmap
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.SLog
import com.github.panpf.sketch.util.getTrimLevelName
import kotlinx.coroutines.sync.Mutex
import java.util.WeakHashMap

/**
 * 创建根据最少使用规则释放缓存的内存缓存管理器
 *
 * @param context [Context]
 * @param maxSize 最大容量
 */
class LruMemoryCache(context: Context, maxSize: Int) : MemoryCache {

    companion object {
        private const val MODULE = "LruMemoryCache"
    }

    private val cache: LruCache<String, SketchRefBitmap> = RefBitmapLruCache(maxSize)
    private val appContext: Context = context.applicationContext
    private val editMutexLockMap: MutableMap<String, Mutex> = WeakHashMap()

    @get:Synchronized
    override val size: Long
        get() = if (!isClosed) cache.size().toLong() else 0

    override val maxSize: Long
        get() = cache.maxSize().toLong()

    @get:Synchronized
    override var isClosed = false
        private set

    override var isDisabled = false
        set(value) {
            if (field != value) {
                field = value
                SLog.wmf(MODULE, "setDisabled. %s", value)
            }
        }

    @Synchronized
    override fun put(key: String, refBitmap: SketchRefBitmap) {
        if (isClosed) {
            SLog.emf(MODULE, "Closed. Unable put, key=%s", key)
            return
        }
        if (isDisabled) {
            SLog.wmf(MODULE, "Disabled. Unable put, key=%s", key)
            return
        }
        if (cache[key] != null) {
            SLog.wm(MODULE, String.format("Exist. key=%s", key))
            return
        }
        var oldCacheSize = 0
        if (SLog.isLoggable(SLog.DEBUG)) {
            oldCacheSize = cache.size()
        }
        cache.put(key, refBitmap)
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(
                MODULE, "put. beforeCacheSize=%s. %s. afterCacheSize=%s",
                Formatter.formatFileSize(appContext, oldCacheSize.toLong()),
                refBitmap.requestKey,
                Formatter.formatFileSize(appContext, cache.size().toLong())
            )
        }
    }

    @Synchronized
    override fun get(key: String): SketchRefBitmap? {
        if (isClosed) {
            SLog.emf(MODULE, "Closed. Unable get, key=%s", key)
            return null
        }
        if (isDisabled) {
            SLog.wmf(MODULE, "Disabled. Unable get, key=%s", key)
            return null
        }
        val refBitmap = cache[key]
        return if (refBitmap != null && refBitmap.isRecycled) {
            cache.remove(key)
            null
        } else {
            refBitmap
        }
    }

    @Synchronized
    override fun remove(key: String): SketchRefBitmap? {
        if (isClosed) {
            SLog.emf(MODULE, "Closed. Unable remove, key=%s", key)
            return null
        }
        if (isDisabled) {
            SLog.wmf(MODULE, "Disabled. Unable remove, key=%s", key)
            return null
        }
        val refBitmap = cache.remove(key)
        SLog.dmf(
            MODULE, "remove. memoryCacheSize: %s",
            Formatter.formatFileSize(appContext, cache.size().toLong())
        )
        return refBitmap
    }

    @Synchronized
    override fun trimMemory(level: Int) {
        if (isClosed) {
            return
        }
        val memoryCacheSize = size
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            cache.evictAll()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.maxSize() / 2)
        }
        val releasedSize = memoryCacheSize - size
        SLog.wmf(
            MODULE, "trimMemory. level=%s, released: %s",
            getTrimLevelName(level), Formatter.formatFileSize(appContext, releasedSize)
        )
    }

    @Synchronized
    override fun clear() {
        if (isClosed) {
            SLog.emf(MODULE, "Closed. Unable clear")
            return
        }
        SLog.wmf(
            MODULE,
            "clear. before size: %s",
            Formatter.formatFileSize(appContext, cache.size().toLong())
        )
        cache.evictAll()
    }

    @Synchronized
    override fun close() {
        if (isClosed) {
            SLog.emf(MODULE, "Closed. Unable close")
            return
        }
        isClosed = true
        cache.evictAll()
    }

    override fun toString(): String {
        return String.format(
            "%s(maxSize=%s)",
            MODULE,
            Formatter.formatFileSize(appContext, maxSize)
        )
    }

    @Synchronized
    override fun getOrCreateEditMutexLock(key: String): Mutex {
        return editMutexLockMap[key] ?: Mutex().apply {
            this@LruMemoryCache.editMutexLockMap[key] = this
        }
    }

    private class RefBitmapLruCache constructor(maxSize: Int) :
        LruCache<String, SketchRefBitmap>(maxSize) {

        override fun put(key: String, refBitmap: SketchRefBitmap): SketchRefBitmap? {
            refBitmap.setIsCached("$MODULE:put", true)
            return super.put(key, refBitmap)
        }

        override fun sizeOf(key: String, refBitmap: SketchRefBitmap): Int {
            val bitmapSize = refBitmap.byteCount
            return if (bitmapSize == 0) 1 else bitmapSize
        }

        override fun entryRemoved(
            evicted: Boolean,
            key: String,
            oldRefBitmap: SketchRefBitmap,
            newRefBitmap: SketchRefBitmap?
        ) {
            oldRefBitmap.setIsCached("$MODULE:entryRemoved", false)
        }
    }
}