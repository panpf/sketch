package com.github.panpf.sketch.cache

import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.text.format.Formatter
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.SLog.Companion.dmf
import com.github.panpf.sketch.SLog.Companion.isLoggable
import com.github.panpf.sketch.SLog.Companion.wm
import com.github.panpf.sketch.SLog.Companion.wmf
import com.github.panpf.sketch.util.SketchUtils.Companion.getTrimLevelName
import com.github.panpf.sketch.util.SketchUtils.Companion.toHexString
import com.github.panpf.sketch.util.recycle.AttributeStrategy
import com.github.panpf.sketch.util.recycle.LruPoolStrategy
import com.github.panpf.sketch.util.recycle.SizeConfigStrategy
import java.util.*

/**
 * 创建根据最少使用规则释放缓存的 [Bitmap] 复用池，使用默认的 [Bitmap] 匹配策略和 [Bitmap.Config] 白名单
 *
 * @param maxSize 最大容量
 */
class LruBitmapPool @JvmOverloads constructor(
    context: Context,
    maxSize: Int,
    private val strategy: LruPoolStrategy = defaultStrategy,
    private val allowedConfigs: Set<Bitmap.Config?> = defaultAllowedConfigs
) : BitmapPool {

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
    private val appContext: Context = context.applicationContext

    @get:Synchronized
    override var isClosed = false
        private set
    override var isDisabled = false
        set(value) {
            if (field != value) {
                field = value
                wmf(MODULE, "setDisabled. %s", value)
            }
        }

    /**
     * 创建根据最少使用规则释放缓存的 [Bitmap] 复用池，使用默认的 [Bitmap] 匹配策略
     *
     * @param maxSize        最大容量
     * @param allowedConfigs [Bitmap.Config] 白名单
     */
    constructor(context: Context, maxSize: Int, allowedConfigs: Set<Bitmap.Config?>) : this(
        context,
        maxSize,
        defaultStrategy,
        allowedConfigs
    )

    @Synchronized
    override fun put(bitmap: Bitmap): Boolean {
        if (isClosed) {
            return false
        }
        if (isDisabled) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE,
                    "Disabled. Unable put, bitmap=%s,%s",
                    strategy.logBitmap(bitmap),
                    toHexString(bitmap)!!
                )
            }
            return false
        }
        if (
            bitmap.isRecycled
            || !bitmap.isMutable
            || strategy.getSize(bitmap) > maxSize
            || !allowedConfigs.contains(bitmap.config)
        ) {
            wmf(
                MODULE,
                "Reject bitmap from pool, bitmap: %s, is recycled: %s, is mutable: %s, is allowed config: %s, %s",
                strategy.logBitmap(bitmap),
                bitmap.isRecycled,
                bitmap.isMutable,
                allowedConfigs.contains(bitmap.config),
                toHexString(bitmap)!!
            )
            return false
        }
        val size = strategy.getSize(bitmap)
        strategy.put(bitmap)
        tracker.add(bitmap)
        puts++
        this.size += size
        if (isLoggable(SLog.DEBUG)) {
            dmf(
                MODULE,
                "Put bitmap in pool=%s,%s",
                strategy.logBitmap(bitmap),
                toHexString(bitmap)!!
            )
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
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE,
                    "Disabled. Unable get, bitmap=%s,%s",
                    strategy.logBitmap(width, height, config)
                )
            }
            return null
        }

        // Config will be null for non public config types, which can lead to transformations naively passing in
        // null as the requested config here. See issue #194.
        val result = strategy[width, height, config]
        if (result == null) {
            if (isLoggable(SLog.DEBUG)) {
                dmf(MODULE, "Missing bitmap=%s", strategy.logBitmap(width, height, config))
            }
            misses++
        } else {
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE,
                    "Get bitmap=%s,%s",
                    strategy.logBitmap(width, height, config),
                    toHexString(result)!!
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
            if (isLoggable(SLog.DEBUG)) {
                val elements = Exception().stackTrace
                val element = if (elements.size > 1) elements[1] else elements[0]
                dmf(
                    MODULE, "Make bitmap. info:%dx%d,%s,%s - %s.%s:%d",
                    result.width, result.height, result.config, toHexString(result)!!,
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
        val size = size.toLong()
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            trimToSize(0)
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            trimToSize(maxSize / 2)
        }
        val releasedSize = Formatter.formatFileSize(appContext, size - size)
        wmf(MODULE, "trimMemory. level=%s, released: %s", getTrimLevelName(level), releasedSize)
    }

    @Synchronized
    override fun clear() {
        wmf(MODULE, "clear. before size %s", Formatter.formatFileSize(appContext, size.toLong()))
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
                wm(MODULE, "Size mismatch, resetting")
                dumpUnchecked()
                this.size = 0
                return
            }
            if (isLoggable(SLog.DEBUG)) {
                dmf(
                    MODULE,
                    "Evicting bitmap=%s,%s",
                    strategy.logBitmap(removed),
                    toHexString(removed)!!
                )
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
        if (isLoggable(SLog.DEBUG)) {
            dmf(
                MODULE,
                "Hits=%d, misses=%d, puts=%d, evictions=%d, currentSize=%d, maxSize=%d, Strategy=%s",
                hits,
                misses,
                puts,
                evictions,
                size,
                maxSize,
                strategy
            )
        }
    }

    override fun toString(): String {
        return String.format(
            "%s(maxSize=%s,strategy=%s,allowedConfigs=%s)",
            MODULE,
            Formatter.formatFileSize(appContext, maxSize.toLong()),
            strategy.key,
            allowedConfigs.toString()
        )
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

    companion object {
        private val DEFAULT_CONFIG = Bitmap.Config.ARGB_8888
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
}