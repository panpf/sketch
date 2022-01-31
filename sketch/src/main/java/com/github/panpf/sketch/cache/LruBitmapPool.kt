package com.github.panpf.sketch.cache

import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import com.github.panpf.sketch.ImageFormat
import com.github.panpf.sketch.util.Logger
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.calculateSamplingSize
import com.github.panpf.sketch.util.calculateSamplingSizeForRegion
import com.github.panpf.sketch.util.computeByteCount
import com.github.panpf.sketch.util.formatFileSize
import com.github.panpf.sketch.util.recycle.AttributeStrategy
import com.github.panpf.sketch.util.recycle.LruPoolStrategy
import com.github.panpf.sketch.util.recycle.SizeConfigStrategy
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.trimLevelName
import java.util.Collections

/**
 * 创建根据最少使用规则释放缓存的 [Bitmap] 复用池，使用默认的 [Bitmap] 匹配策略和 [Bitmap.Config] 白名单
 *
 * @param maxSize 最大容量
 */
class LruBitmapPool @JvmOverloads constructor(
    val logger: Logger,
    maxSize: Int,
    private val strategy: LruPoolStrategy = defaultStrategy,
    private val allowedConfigs: Set<Config?> = defaultAllowedConfigs
) : BitmapPool {

    companion object {
        private const val MODULE = "LruBitmapPool"
        private val defaultStrategy: LruPoolStrategy
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SizeConfigStrategy()
            } else {
                AttributeStrategy()
            }
        private val defaultAllowedConfigs: Set<Bitmap.Config?>
            get() {
                val configs: MutableSet<Bitmap.Config?> =
                    HashSet(listOf(*Bitmap.Config.values()))
                if (Build.VERSION.SDK_INT >= 19) {
                    configs.add(null)
                }
                return Collections.unmodifiableSet(configs)
            }
    }

    private val initialMaxSize: Int = maxSize
    private val tracker: BitmapTracker = NullBitmapTracker()
    private var maxSizeHolder: Int = maxSize
    override val maxSize: Int
        get() = maxSizeHolder
    override var size = 0
        private set
    private var hits = 0
    private var misses = 0
    private var puts = 0
    private var evictions = 0

    @get:Synchronized
    override var isClosed = false
        private set
    override var isDisabled = false
        set(value) {
            if (field != value) {
                field = value
                logger.w(MODULE, "setDisabled. $value")
            }
        }

    @Synchronized
    override fun put(bitmap: Bitmap): Boolean {
        if (isClosed) {
            return false
        }
        if (isDisabled) {
            logger.w(
                MODULE,
                "Disabled. Unable put, bitmap=${strategy.logBitmap(bitmap)},${bitmap.toHexString()}",
            )
            return false
        }
        if (
            bitmap.isRecycled
            || !bitmap.isMutable
            || strategy.getSize(bitmap) > maxSize
            || !allowedConfigs.contains(bitmap.config)
        ) {
            logger.w(
                MODULE,
                "Reject bitmap from pool, bitmap: %s, is recycled: %s, is mutable: %s, is allowed config: %s, %s".format(
                    strategy.logBitmap(bitmap),
                    bitmap.isRecycled,
                    bitmap.isMutable,
                    allowedConfigs.contains(bitmap.config),
                    bitmap.toHexString()
                )
            )
            return false
        }
        val size = strategy.getSize(bitmap)
        strategy.put(bitmap)
        tracker.add(bitmap)
        puts++
        this.size += size
        logger.d(MODULE) {
            "Put bitmap in pool=%s,%s".format(strategy.logBitmap(bitmap), bitmap.toHexString())
        }
        dump()
        evict()
        return true
    }

    @Synchronized
    override fun getDirty(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        if (isClosed) {
            return null
        }
        if (isDisabled) {
            logger.w(
                MODULE,
                "Disabled. Unable get, bitmap=${strategy.logBitmap(width, height, config)}",
            )
            return null
        }

        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        val result = strategy[width, height, config]
        if (result == null) {
            logger.d(MODULE) {
                "Missing bitmap=%s".format(strategy.logBitmap(width, height, config))
            }
            misses++
        } else {
            logger.d(MODULE) {
                "Get bitmap=%s,%s".format(
                    strategy.logBitmap(width, height, config),
                    result.toHexString()
                )
            }
            hits++
            size -= strategy.getSize(result)
            tracker.remove(result)
            result.setHasAlpha(true)
        }
        dump()
        return result
    }

    @Synchronized
    override fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        val result = getDirty(width, height, config)
        result?.eraseColor(Color.TRANSPARENT)
        return result
    }

    override fun getOrMake(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        var result = get(width, height, config)
        if (result == null) {
            result = Bitmap.createBitmap(width, height, config)
            val elements = Exception().stackTrace
            val element = if (elements.size > 1) elements[1] else elements[0]
            logger.d(MODULE) {
                "Make bitmap. info:%dx%d,%s,%s - %s.%s:%d".format(
                    result.width, result.height, result.config, result.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
        }
        return result!!
    }

    private fun evict() {
        if (isClosed) {
            return
        }
        trimToSize(maxSize)
    }

    @Synchronized
    override fun setSizeMultiplier(sizeMultiplier: Float) {
        if (isClosed) {
            return
        }
        maxSizeHolder = Math.round(initialMaxSize * sizeMultiplier)
        evict()
    }

    @SuppressLint("InlinedApi")
    @Synchronized
    override fun trimMemory(level: Int) {
        val oldSize = this.size.toLong()
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            trimToSize(0)
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            trimToSize(maxSize / 2)
        }
        val newSize = this.size.toLong()
        val releasedSize = (oldSize - newSize).formatFileSize()
        logger.w(
            MODULE,
            "trimMemory. level=%s, released: %s".format(trimLevelName(level), releasedSize)
        )
    }

    @Synchronized
    override fun clear() {
        logger.w(
            MODULE,
            "clear. before size ${size.toLong().formatFileSize()}",
        )
        trimToSize(0)
    }

    @Synchronized
    override fun close() {
        if (isClosed) {
            return
        }
        isClosed = true
        trimToSize(0)
    }

    @Synchronized
    private fun trimToSize(size: Int) {
        while (this.size > size) {
            val removed = strategy.removeLast()
            if (removed == null) {
                logger.w(MODULE, "Size mismatch, resetting")
                dumpUnchecked()
                this.size = 0
                return
            }
            logger.d(MODULE) {
                "Evicting bitmap=%s,%s".format(strategy.logBitmap(removed), removed.toHexString())
            }
            tracker.remove(removed)
            this.size -= strategy.getSize(removed)
            removed.recycle()
            evictions++
            dump()
        }
    }

    private fun dump() {
        dumpUnchecked()
    }

    private fun dumpUnchecked() {
        logger.d(MODULE) {
            "Hits=%d, misses=%d, puts=%d, evictions=%d, currentSize=%d, maxSize=%d, Strategy=%s".format(
                hits, misses, puts, evictions, size, maxSize, strategy
            )
        }
    }

    override fun toString(): String {
        return "%s(maxSize=%s,strategy=%s,allowedConfigs=%s)".format(
            MODULE,
            maxSize.toLong().formatFileSize(),
            if (strategy is SizeConfigStrategy) "SizeConfigStrategy" else "AttributeStrategy",
            allowedConfigs.joinToString(prefix = "[", postfix = "]", separator = ",")
        )
    }



    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapFactory
     *
     * @param options     BitmapFactory.Options 需要用到 inSampleSize 以及 inPreferredConfig 属性
     * @param outWidth    图片原始宽
     * @param outHeight   图片原始高
     * @param outMimeType 图片类型
     * @return true：找到了可复用的 Bitmap
     */
    override fun setInBitmap(
        options: BitmapFactory.Options, outWidth: Int, outHeight: Int, outMimeType: String?,
    ): Boolean {
        if (outWidth == 0 || outHeight == 0) {
            logger.e(MODULE, "outWidth or ourHeight is 0")
            return false
        }
        if (TextUtils.isEmpty(outMimeType)) {
            logger.e(MODULE, "outMimeType is empty")
            return false
        }

        // 使用 inBitmap 时 4.4 以下 inSampleSize 不能为 0，最小也得是 1
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1
        }
        var inSampleSize = options.inSampleSize
        val imageType = ImageFormat.valueOfMimeType(outMimeType)
        var inBitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var finalWidth = calculateSamplingSize(outWidth, inSampleSize)
            var finalHeight = calculateSamplingSize(outHeight, inSampleSize)
            while (finalWidth <= 0 || finalHeight <= 0) {
                inSampleSize /= 2
                if (inSampleSize == 0) {
                    finalWidth = outWidth
                    finalHeight = outHeight
                } else {
                    finalWidth = calculateSamplingSizeForRegion(outWidth, inSampleSize)
                    finalHeight = calculateSamplingSizeForRegion(outHeight, inSampleSize)
                }
            }
            if (inSampleSize != options.inSampleSize) {
                options.inSampleSize = inSampleSize
            }
            inBitmap = this[finalWidth, finalHeight, options.inPreferredConfig]
        } else if (inSampleSize == 1 && (imageType == ImageFormat.JPEG || imageType == ImageFormat.PNG)) {
            inBitmap = this[outWidth, outHeight, options.inPreferredConfig]
        }
        if (inBitmap != null) {
            logger.d(MODULE) {
                "setInBitmapFromPool. options=%dx%d,%s,%d,%d. inBitmap=%s,%d".format(
                    outWidth, outHeight, options.inPreferredConfig, inSampleSize,
                    computeByteCount(outWidth, outHeight, options.inPreferredConfig),
                    Integer.toHexString(inBitmap.hashCode()), inBitmap.byteCountCompat
                )
            }
        }
        options.inBitmap = inBitmap
        options.inMutable = true
        return inBitmap != null
    }

    /**
     * 从 bitmap pool 中取出可复用的 Bitmap 设置到 inBitmap 上，适用于 BitmapRegionDecoder
     *
     * @param options    BitmapFactory.Options 需要用到 options 的 inSampleSize 以及 inPreferredConfig 属性
     * @return true：找到了可复用的 Bitmap
     */
    override fun setInBitmapForRegionDecoder(
        width: Int,
        height: Int,
        options: BitmapFactory.Options
    ): Boolean {
        var inSampleSize = if (options.inSampleSize >= 1) options.inSampleSize else 1
        val config = options.inPreferredConfig
        var finalWidth = calculateSamplingSizeForRegion(width, inSampleSize)
        var finalHeight = calculateSamplingSizeForRegion(height, inSampleSize)
        while (finalWidth <= 0 || finalHeight <= 0) {
            inSampleSize /= 2
            if (inSampleSize == 0) {
                finalWidth = width
                finalHeight = height
            } else {
                finalWidth = calculateSamplingSizeForRegion(width, inSampleSize)
                finalHeight = calculateSamplingSizeForRegion(height, inSampleSize)
            }
        }
        if (inSampleSize != options.inSampleSize) {
            options.inSampleSize = inSampleSize
        }
        var inBitmap = this[finalWidth, finalHeight, config]
        if (inBitmap != null) {
            logger.d(MODULE) {
                "setInBitmapFromPoolForRegionDecoder. options=%dx%d,%s,%d,%d. inBitmap=%s,%d".format(
                    finalWidth, finalHeight, config, inSampleSize,
                    computeByteCount(finalWidth, finalHeight, config),
                    Integer.toHexString(inBitmap.hashCode()), inBitmap!!.byteCountCompat
                )
            }
        } else {
            // 由于 BitmapRegionDecoder 不支持 inMutable 所以就自己创建 Bitmap
            inBitmap = Bitmap.createBitmap(finalWidth, finalHeight, config)
        }
        options.inBitmap = inBitmap
        return inBitmap != null
    }

    /**
     * 回收 bitmap，首先尝试放入 bitmap pool，放不进去就回收
     *
     * @param bitmap     要处理的 bitmap
     * @return true：成功放入 bitmap pool
     */
    override fun freeBitmapToPool(bitmap: Bitmap?): Boolean {
        if (bitmap == null || bitmap.isRecycled) {
            return false
        }
        val success = put(bitmap)
        if (success) {
            logger.d(MODULE) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                "Put to bitmap pool. info:%dx%d,%s,%s - %s.%s:%d".format(
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
        } else {
            logger.d(MODULE) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                "Recycle bitmap. info:%dx%d,%s,%s - %s.%s:%d".format(
                    bitmap.width, bitmap.height, bitmap.config, bitmap.toHexString(),
                    element.className, element.methodName, element.lineNumber
                )
            }
            bitmap.recycle()
        }
        return success
    }

    private interface BitmapTracker {
        fun add(bitmap: Bitmap)
        fun remove(bitmap: Bitmap)
    }

    // Only used for debugging
    private class ThrowingBitmapTracker : BitmapTracker {
        private val bitmaps = Collections.synchronizedSet(HashSet<Bitmap>())
        override fun add(bitmap: Bitmap) {
            check(!bitmaps.contains(bitmap)) {
                ("Can't add already added bitmap: " + bitmap + " [" + bitmap.width
                        + "x" + bitmap.height + "]")
            }
            bitmaps.add(bitmap)
        }

        override fun remove(bitmap: Bitmap) {
            check(bitmaps.contains(bitmap)) { "Cannot remove bitmap not in tracker" }
            bitmaps.remove(bitmap)
        }
    }

    private class NullBitmapTracker : BitmapTracker {
        override fun add(bitmap: Bitmap) {
            // Do nothing.
        }

        override fun remove(bitmap: Bitmap) {
            // Do nothing.
        }
    }
}