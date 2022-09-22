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
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.pool.AttributeStrategy
import com.github.panpf.sketch.util.pool.LruPoolStrategy
import com.github.panpf.sketch.util.pool.SizeConfigStrategy
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

/**
 * Release the cached [Bitmap] reuse pool according to the least-used rule
 */
class LruBitmapPool constructor(
    override val maxSize: Long,
    val allowedConfigs: Set<Bitmap.Config?> =
        Bitmap.Config.values().run {
            if (Build.VERSION.SDK_INT >= 19) {
                listOf(null).plus(this).toSet()
            } else {
                this.toSet()
            }
        }
) : BitmapPool {

    companion object {
        private const val MODULE = "LruBitmapPool"
    }

    private var _size: Long = 0L
    private var hits = 0
    private var misses = 0
    private var puts = 0
    private var evictions = 0
    private val strategy: LruPoolStrategy = if (Build.VERSION.SDK_INT >= 19) {
        SizeConfigStrategy()
    } else {
        AttributeStrategy()
    }
    private val getCount = AtomicInteger()
    private val hitCount = AtomicInteger()

    override var logger: Logger? = null
    override val size: Long
        get() = _size

    override fun put(bitmap: Bitmap, caller: String?): Boolean {
        val bitmapKey = "${strategy.logBitmap(bitmap)}}"
        if (bitmap.isRecycled) {
            logger?.w(MODULE, "put. reject. Recycled. $caller. ${bitmap.logString}. $bitmapKey")
            return false
        }
        if (!bitmap.isMutable) {
            logger?.w(MODULE, "put. reject. Immutable. $caller. ${bitmap.logString}. $bitmapKey")
            return false
        }
        val bitmapSize = strategy.getSize(bitmap).toLong()
        if (bitmapSize > maxSize * 0.7f) {
            logger?.w(MODULE) {
                "put. reject. Too big ${bitmapSize.formatFileSize()}, maxSize ${maxSize.formatFileSize()}. $caller. ${bitmap.logString}. $bitmapKey"
            }
            return false
        }
        if (!allowedConfigs.contains(bitmap.config)) {
            logger?.w(MODULE) {
                "put. reject. Disallowed config ${bitmap.config}. $caller. ${bitmap.logString}. $bitmapKey"
            }
            return false
        }

        synchronized(this) {
            trimToSize(maxSize - bitmapSize, "$caller:putBefore")
            strategy.put(bitmap)
            puts++
            this._size += bitmapSize
            logger?.d(MODULE) {
                "put. successful. ${bitmap.logString}. size ${bitmapSize.formatFileSize()}, pool size ${size.formatFileSize()}. $caller. $bitmapKey"
            }
        }
        return true
    }

    override fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        return synchronized(this) {
            strategy[width, height, config].apply {
                val getCount = getCount.addAndGet(1)
                val hitCount = if (this != null) {
                    hitCount.addAndGet(1)
                } else {
                    hitCount.get()
                }
                if (getCount == Int.MAX_VALUE || hitCount == Int.MAX_VALUE) {
                    this@LruBitmapPool.getCount.set(0)
                    this@LruBitmapPool.hitCount.set(0)
                }
                if (this == null) {
                    misses++
                } else {
                    hits++
                    _size -= strategy.getSize(this)
                    this.setHasAlpha(true)
                }

                logger?.d(MODULE) {
                    val hitRatio = ((hitCount.toFloat() / getCount).format(2) * 100).roundToInt()
                    val bitmapKey = strategy.logBitmap(width, height, config)
                    if (this != null) {
                        "get. hit($hitRatio%). ${this.logString}. ${size.formatFileSize()}. $bitmapKey"
                    } else {
                        "get. miss($hitRatio%). ${size.formatFileSize()}. $bitmapKey"
                    }
                }
            }
        }
    }

    override fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap? =
        getDirty(width, height, config)?.apply {
            eraseColor(Color.TRANSPARENT)
        }

    override fun getOrCreate(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        return get(width, height, config) ?: Bitmap.createBitmap(width, height, config).apply {
            logger?.d(MODULE) {
                "getOrCreate. new. ${this.logString}. ${strategy.logBitmap(width, height, config)}"
            }
        }
    }

    override fun exist(width: Int, height: Int, config: Bitmap.Config): Boolean {
        return strategy.exist(width, height, config)
    }

    override fun trim(level: Int) {
        synchronized(this) {
            val oldSize = this.size
            if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
                trimToSize(0, "trim")
            } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                trimToSize(maxSize / 2, "trim")
            }
            val releasedSize = (oldSize - size)
            logger?.w(MODULE) {
                "trim. level '${getTrimLevelName(level)}', released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
            }
        }
    }

    override fun clear() {
        synchronized(this) {
            val oldSize = size
            trimToSize(0, "clear")
            logger?.w(MODULE, "clear. cleared ${oldSize.formatFileSize()}")
        }
    }

    private fun trimToSize(size: Long, caller: String) {
        synchronized(this) {
            while (this.size > size) {
                val removed = strategy.removeLast()
                if (removed == null) {
                    this._size = 0
                } else {
                    this._size -= strategy.getSize(removed)
                    removed.recycle()
                    evictions++
                    logger?.d(MODULE) {
                        "trimToSize. Recycle bitmap. $caller. ${removed.logString}"
                    }
                }
            }
        }
    }

    override fun toString(): String {
        val strategy =
            if (strategy is SizeConfigStrategy) "SizeConfigStrategy" else "AttributeStrategy"
        val configs = allowedConfigs.joinToString(prefix = "[", postfix = "]", separator = ",")
        return "${MODULE}(maxSize=${maxSize.formatFileSize()},strategy=${strategy},allowedConfigs=${configs})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LruBitmapPool) return false
        if (maxSize != other.maxSize) return false
        if (allowedConfigs != other.allowedConfigs) return false
        return true
    }

    override fun hashCode(): Int {
        var result = maxSize.hashCode()
        result = 31 * result + allowedConfigs.hashCode()
        return result
    }
}