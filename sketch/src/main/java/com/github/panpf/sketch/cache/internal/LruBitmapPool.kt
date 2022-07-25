package com.github.panpf.sketch.cache.internal

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.internal.ImageFormat
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.decode.internal.samplingSize
import com.github.panpf.sketch.decode.internal.samplingSizeForRegion
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.format
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.getTrimLevelName
import com.github.panpf.sketch.util.isAndSupportHardware
import com.github.panpf.sketch.util.pool.AttributeStrategy
import com.github.panpf.sketch.util.pool.LruPoolStrategy
import com.github.panpf.sketch.util.pool.SizeConfigStrategy
import com.github.panpf.sketch.util.toHexString
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
        val bitmapLogString = "${strategy.logBitmap(bitmap)}@${toHexString()}"
        if (bitmap.isRecycled) {
            logger?.w(MODULE, "put. reject. Recycled. $caller. $bitmapLogString")
            return false
        }
        if (!bitmap.isMutable) {
            logger?.w(MODULE, "put. reject. Immutable. $caller. $bitmapLogString")
            return false
        }
        val bitmapSize = strategy.getSize(bitmap).toLong()
        if (bitmapSize > maxSize * 0.7f) {
            logger?.w(MODULE) {
                "put. reject. Too big ${bitmapSize.formatFileSize()}, maxSize ${maxSize.formatFileSize()}. $caller. $bitmapLogString"
            }
            return false
        }
        if (!allowedConfigs.contains(bitmap.config)) {
            logger?.w(MODULE) {
                "put. reject. Disallowed config ${bitmap.config}. $caller. $bitmapLogString"
            }
            return false
        }

        synchronized(this) {
            trimToSize(maxSize - bitmapSize, "$caller:putBefore")
            strategy.put(bitmap)
            puts++
            this._size += bitmapSize
            logger?.d(MODULE) {
                "put. successful. bitmap size ${bitmapSize.formatFileSize()}, pool size ${size.formatFileSize()}. $caller. $bitmapLogString"
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
                    val logString = strategy.logBitmap(width, height, config)
                    if (this != null) {
                        "get. hit($hitRatio%). ${this.logString}. ${size.formatFileSize()}. $logString"
                    } else {
                        "get. miss($hitRatio%). ${size.formatFileSize()}. $logString"
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
                "Create bitmap. ${this.logString}. ${strategy.logBitmap(width, height, config)}"
            }
        }
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

    override fun setInBitmap(
        options: BitmapFactory.Options,
        imageWidth: Int,
        imageHeight: Int,
        imageMimeType: String?,
    ): Boolean {
        if (imageWidth == 0 || imageHeight == 0) {
            logger?.e(MODULE, "setInBitmap. outWidth or ourHeight is 0")
            return false
        }
        if (options.inPreferredConfig?.isAndSupportHardware() == true) {
            logger?.w(
                MODULE,
                "setInBitmap. inPreferredConfig is HARDWARE does not support inBitmap"
            )
            return false
        }

        val inSampleSize = options.inSampleSize.coerceAtLeast(1)
        val finalWidth = samplingSize(imageWidth, inSampleSize)
        val finalHeight = samplingSize(imageHeight, inSampleSize)
        // The following versions of KITKAT only support format is jpeg or png and inSampleSize is 1
        @Suppress("ReplaceGetOrSet")
        val inBitmap: Bitmap? = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                this.get(finalWidth, finalHeight, options.inPreferredConfig)
            }
            // todo 测试是否需要限制 format 和 inSampleSize
            options.inSampleSize <= 1 && ImageFormat.JPEG.mimeType.equals(imageMimeType, true) -> {
                this.get(finalWidth, finalHeight, options.inPreferredConfig)
            }
            options.inSampleSize <= 1 && ImageFormat.PNG.mimeType.equals(imageMimeType, true) -> {
                this.get(finalWidth, finalHeight, options.inPreferredConfig)
            }
            else -> {
                logger?.w(MODULE) {
                    "setInBitmap. The following versions of KITKAT only support format is jpeg or png and inSampleSize is 1. " +
                            "imageMimeType=$imageMimeType, inSampleSize=${options.inSampleSize}"
                }
                null
            }
        }
        if (inBitmap != null) {
            logger?.d(MODULE) {
                "setInBitmap. options=%dx%d,%s,%d. inBitmap=%s,%s".format(
                    finalWidth,
                    finalHeight,
                    options.inPreferredConfig,
                    inSampleSize,
                    inBitmap.toHexString(),
                    inBitmap.allocationByteCountCompat.formatFileSize()
                )
            }
        }

        options.inBitmap = inBitmap
        options.inMutable = true
        return inBitmap != null
    }

    override fun setInBitmapForRegion(
        options: BitmapFactory.Options, imageWidth: Int, imageHeight: Int,
    ): Boolean {
        if (imageWidth == 0 || imageHeight == 0) {
            logger?.e(MODULE, "setInBitmapForRegion. outWidth or ourHeight is 0")
            return false
        }
        if (options.inPreferredConfig?.isAndSupportHardware() == true) {
            logger?.w(
                MODULE,
                "setInBitmapForRegion. inPreferredConfig is HARDWARE does not support inBitmap"
            )
            return false
        }

        val inSampleSize = options.inSampleSize.coerceAtLeast(1)
        val finalWidth = samplingSizeForRegion(imageWidth, inSampleSize)
        val finalHeight = samplingSizeForRegion(imageHeight, inSampleSize)
        // BitmapRegionDecoder does not support inMutable, so creates Bitmap
        @Suppress("ReplaceGetOrSet")
        val inBitmap = this.get(finalWidth, finalHeight, options.inPreferredConfig)
            ?: Bitmap.createBitmap(finalWidth, finalHeight, options.inPreferredConfig)!!
        logger?.d(MODULE) {
            "setInBitmapForRegion. options=%dx%d,%s,%d. inBitmap=%s,%s".format(
                finalWidth,
                finalHeight,
                options.inPreferredConfig,
                inSampleSize,
                inBitmap.toHexString(),
                inBitmap.allocationByteCountCompat.formatFileSize()
            )
        }

        options.inBitmap = inBitmap
        return true
    }

    override fun free(bitmap: Bitmap?, caller: String?): Boolean {
        if (bitmap == null || bitmap.isRecycled) return false

        val bitmapLogString = "${strategy.logBitmap(bitmap)}@${toHexString()}"
        val success = put(bitmap, caller)
        if (success) {
            logger?.d(MODULE) {
                "free. successful. $caller. $bitmapLogString"
            }
        } else {
            bitmap.recycle()
            logger?.w(MODULE) {
                "free. failed. execute recycle. $caller. $bitmapLogString"
            }
        }
        return success
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