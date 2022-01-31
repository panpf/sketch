package com.github.panpf.sketch.cache.internal

import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.calculateSamplingSize
import com.github.panpf.sketch.util.calculateSamplingSizeForRegion
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.recycle.AttributeStrategy
import com.github.panpf.sketch.util.recycle.LruPoolStrategy
import com.github.panpf.sketch.util.recycle.SizeConfigStrategy
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.trimLevelName

/**
 * Release the cached [Bitmap] reuse pool according to the least-used rule
 */
class LruBitmapPool constructor(
    private val logger: Logger,
    override val maxSize: Long,
    private val strategy: LruPoolStrategy =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SizeConfigStrategy()
        } else {
            AttributeStrategy()
        },
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

    override val size: Long
        get() = _size

    override fun put(bitmap: Bitmap): Boolean {
        if (bitmap.isRecycled) {
            logger.w(MODULE, "Reject put bitmap in pool. Recycled, ${bitmap.toHexString()}")
            return false
        }
        if (!bitmap.isMutable) {
            logger.w(MODULE, "Reject put bitmap in pool. Immutable, ${bitmap.toHexString()}")
            return false
        }
        val bitmapSize = strategy.getSize(bitmap).toLong()
        if (bitmapSize > maxSize) {
            logger.w(
                MODULE,
                "Reject put bitmap in pool. Too big ${bitmapSize.formatFileSize()}, maxSize ${maxSize.formatFileSize()}, ${bitmap.toHexString()}"
            )
            return false
        }
        if (!allowedConfigs.contains(bitmap.config)) {
            logger.w(
                MODULE,
                "Reject put bitmap to pool. Disallowed config '${bitmap.config}', ${bitmap.toHexString()}"
            )
            return false
        }

        synchronized(this) {
            strategy.put(bitmap)
            puts++
            this._size += bitmapSize
            evict()
            logger.d(MODULE) {
                "Put bitmap in pool. ${strategy.logBitmap(bitmap)}, ${bitmap.toHexString()}, size ${size.formatFileSize()}"
            }
        }
        return true
    }

    override fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        return synchronized(this) {
            strategy[width, height, config].apply {
                if (this == null) {
                    misses++
                    logger.d(MODULE) {
                        "Missing bitmap. ${strategy.logBitmap(width, height, config)}"
                    }
                } else {
                    hits++
                    _size -= strategy.getSize(this)
                    this.setHasAlpha(true)
                    logger.d(MODULE) {
                        val bitmapInfo = strategy.logBitmap(width, height, config)
                        "Get bitmap. $bitmapInfo, ${this.toHexString()}, size ${size.formatFileSize()}"
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
            logger.d(MODULE) {
                "Create bitmap. ${strategy.logBitmap(this)}, ${this.toHexString()}"
            }
        }
    }

    private fun evict() {
        trimToSize(maxSize)
    }

    override fun trim(level: Int) {
        synchronized(this) {
            val oldSize = this.size
            if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
                trimToSize(0)
            } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
                trimToSize(maxSize / 2)
            }
            val releasedSize = (oldSize - size)
            logger.w(
                MODULE,
                "trim. level '${trimLevelName(level)}', released ${releasedSize.formatFileSize()}, size ${size.formatFileSize()}"
            )
        }
    }

    override fun clear() {
        synchronized(this) {
            val oldSize = size
            trimToSize(0)
            logger.w(MODULE, "clear. cleared ${oldSize.formatFileSize()}")
        }
    }

    @Synchronized
    private fun trimToSize(size: Long) {
        synchronized(this) {
            while (this.size > size) {
                val removed = strategy.removeLast()
                if (removed == null) {
                    this._size = 0
                } else {
                    this._size -= strategy.getSize(removed)
                    removed.recycle()
                    evictions++
                    logger.d(MODULE) {
                        "Evicting bitmap. ${strategy.logBitmap(removed)}, ${removed.toHexString()}"
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

    override fun setInBitmapForBitmapFactory(
        options: BitmapFactory.Options, imageWidth: Int, imageHeight: Int, imageMimeType: String?,
    ): Boolean {
        if (imageWidth == 0 || imageHeight == 0) {
            logger.e(MODULE, "outWidth or ourHeight is 0")
            return false
        }
        if (imageMimeType?.isNotEmpty() != true) {
            logger.e(MODULE, "outMimeType is empty")
            return false
        }

        var inSampleSize = options.inSampleSize.coerceAtLeast(1)
        val inBitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var finalWidth = calculateSamplingSize(imageWidth, inSampleSize)
            var finalHeight = calculateSamplingSize(imageHeight, inSampleSize)
            while (finalWidth <= 0 || finalHeight <= 0) {
                inSampleSize /= 2
                if (inSampleSize == 0) {
                    finalWidth = imageWidth
                    finalHeight = imageHeight
                } else {
                    finalWidth = calculateSamplingSize(imageWidth, inSampleSize)
                    finalHeight = calculateSamplingSize(imageHeight, inSampleSize)
                }
            }
            this[finalWidth, finalHeight, options.inPreferredConfig]
        } else if (inSampleSize == 1) {
            val imageType = ImageFormat.valueOfMimeType(imageMimeType)
            if (imageType == ImageFormat.JPEG || imageType == ImageFormat.PNG) {
                this[imageWidth, imageHeight, options.inPreferredConfig]
            } else {
                null
            }
        } else {
            null
        }
        if (inSampleSize != options.inSampleSize) {
            options.inSampleSize = inSampleSize
        }
        options.inBitmap = inBitmap
        options.inMutable = true
        if (inBitmap != null) {
            logger.d(MODULE) {
                "setInBitmapForBitmapFactory. options=%dx%d,%s,%d. inBitmap=%s,%s".format(
                    imageWidth,
                    imageHeight,
                    options.inPreferredConfig,
                    inSampleSize,
                    inBitmap.toHexString(),
                    inBitmap.byteCountCompat.toLong().formatFileSize()
                )
            }
        }
        return inBitmap != null
    }

    override fun setInBitmapForRegionDecoder(
        options: BitmapFactory.Options, imageWidth: Int, imageHeight: Int,
    ): Boolean {
        if (imageWidth == 0 || imageHeight == 0) {
            logger.e(MODULE, "outWidth or ourHeight is 0")
            return false
        }

        var inSampleSize = options.inSampleSize.coerceAtLeast(1)
        var finalWidth = calculateSamplingSizeForRegion(imageWidth, inSampleSize)
        var finalHeight = calculateSamplingSizeForRegion(imageHeight, inSampleSize)
        while (finalWidth <= 0 || finalHeight <= 0) {
            inSampleSize /= 2
            if (inSampleSize == 0) {
                finalWidth = imageWidth
                finalHeight = imageHeight
            } else {
                finalWidth = calculateSamplingSizeForRegion(imageWidth, inSampleSize)
                finalHeight = calculateSamplingSizeForRegion(imageHeight, inSampleSize)
            }
        }

        // Since BitmapRegionDecoder does not support inMutable, it creates its own Bitmap
        val inBitmap = this[finalWidth, finalHeight, options.inPreferredConfig]
            ?: Bitmap.createBitmap(finalWidth, finalHeight, options.inPreferredConfig)
        if (inSampleSize != options.inSampleSize) {
            options.inSampleSize = inSampleSize
        }
        options.inBitmap = inBitmap
        logger.d(MODULE) {
            "setInBitmapForRegionDecoder. options=%dx%d,%s,%d. inBitmap=%s,%s".format(
                finalWidth,
                finalHeight,
                options.inPreferredConfig,
                inSampleSize,
                inBitmap.toHexString(),
                inBitmap.byteCountCompat.toLong().formatFileSize()
            )
        }
        return inBitmap != null
    }

    override fun free(bitmap: Bitmap?): Boolean {
        if (bitmap == null || bitmap.isRecycled) return false

        val success = put(bitmap)
        if (success) {
            logger.d(MODULE) {
                "Put to bitmap pool. ${bitmap.width}x${bitmap.height},${bitmap.config},${bitmap.toHexString()}"
            }
        } else {
            bitmap.recycle()
            logger.d(MODULE) {
                "Recycle bitmap. ${bitmap.width}x${bitmap.height},${bitmap.config},${bitmap.toHexString()}"
            }
        }
        return success
    }
}