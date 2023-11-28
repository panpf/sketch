@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.decode.internal.logString
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.calculateBitmapByteCount
import java.util.Locale
import java.util.NavigableMap
import java.util.TreeMap

/**
 * Keys [Bitmaps][Bitmap] using both [Bitmap.getAllocationByteCount] and
 * the [Bitmap.Config] returned from [Bitmap.getConfig].
 *
 *
 *
 *
 * Using both the config and the byte size allows us to safely re-use a greater variety of
 * [Bitmaps][Bitmap], which increases the hit rate of the pool and therefore the performance
 * of applications. This class works around #301 by only allowing re-use of [Bitmaps][Bitmap]
 * with a matching number of bytes per pixel.
 *
 */
@RequiresApi(VERSION_CODES.KITKAT)
class SizeConfigStrategy : LruPoolStrategy {

    private val keyPool = KeyPool()
    private val groupedMap = GroupedLinkedMap<Key, Bitmap>()
    private val sortedSizes: MutableMap<Config?, NavigableMap<Int, Int>?> = HashMap()

    override fun put(bitmap: Bitmap) {
        val size = bitmap.allocationByteCountCompat
        val key = keyPool[size, bitmap.config]
        groupedMap.put(key, bitmap)
        val sizes = getSizesForConfig(bitmap.config)
        val current = sizes[key.size]
        sizes[key.size] = if (current == null) 1 else current + 1
    }

    override operator fun get(width: Int, height: Int, config: Config): Bitmap? {
        val size = calculateBitmapByteCount(width, height, config)
        val bestKey = findBestKey(size, config)
        var result = groupedMap[bestKey]
        if (result != null) {
            // Decrement must be called before reconfigure.
            decrementBitmapOfSize(bestKey.size, result)
            try {
                result.reconfigure(width, height, config)
            } catch (e: IllegalArgumentException) {
                // Bitmap.cpp Bitmap_reconfigure method may throw "IllegalArgumentException: Bitmap not large enough to support new configuration" exception
                val message = String.format(
                    Locale.getDefault(),
                    "Bitmap reconfigure error. size=%dx%d, config=%s, bitmap=%s",
                    width, height, config, result.logString
                )
                IllegalArgumentException(message, e).printStackTrace()
                put(result)
                result = null
            }
        }
        return result
    }

    override fun exist(width: Int, height: Int, config: Config): Boolean {
        val size = calculateBitmapByteCount(width, height, config)
        val bestKey = findBestKey(size, config)
        return groupedMap.exist(bestKey)
    }

    private fun findBestKey(size: Int, config: Config?): Key {
        var result = keyPool[size, config]
        for (possibleConfig in getInConfigs(config)) {
            val sizesForPossibleConfig = getSizesForConfig(possibleConfig)
            val possibleSize = sizesForPossibleConfig.ceilingKey(size)
            if (possibleSize != null && possibleSize <= size * MAX_SIZE_MULTIPLE) {
                if (possibleSize != size || possibleConfig != config) {
                    keyPool.offer(result)
                    result = keyPool[possibleSize, possibleConfig]
                }
                break
            }
        }
        return result
    }

    override fun removeLast(): Bitmap? {
        val removed = groupedMap.removeLast()
        if (removed != null) {
            val removedSize = removed.allocationByteCountCompat
            decrementBitmapOfSize(removedSize, removed)
        }
        return removed
    }

    private fun decrementBitmapOfSize(size: Int, removed: Bitmap) {
        val config = removed.config
        val sizes = getSizesForConfig(config)
        val current = sizes[size]
        if (current == null) {
            val message = String.format(
                Locale.getDefault(),
                "Tried to decrement empty size, size: %d, removed: %s, this: %s",
                size, logBitmap(removed), this
            )
            throw NullPointerException(message)
        }
        if (current == 1) {
            sizes.remove(size)
        } else {
            sizes[size] = current - 1
        }
    }

    private fun getSizesForConfig(config: Config?): NavigableMap<Int, Int> {
        var sizes = sortedSizes[config]
        if (sizes == null) {
            sizes = TreeMap()
            sortedSizes[config] = sizes
        }
        return sizes
    }

    override fun logBitmap(bitmap: Bitmap): String {
        val size = bitmap.allocationByteCountCompat
        return getBitmapString(size, bitmap.config)
    }

    override fun logBitmap(width: Int, height: Int, config: Config): String {
        val size = calculateBitmapByteCount(width, height, config)
        return getBitmapString(size, config)
    }

    override fun getSize(bitmap: Bitmap): Int {
        return bitmap.allocationByteCountCompat
    }

    override fun toString(): String {
        val sb = StringBuilder()
            .append("SizeConfigStrategy(groupedMap=")
            .append(groupedMap)
            .append(", sortedSizes=(")
        for ((key, value) in sortedSizes) {
            sb.append(key).append('[').append(value).append("], ")
        }
        if (sortedSizes.isNotEmpty()) {
            sb.replace(sb.length - 2, sb.length, "")
        }
        return sb.append("))").toString()
    }

    // Visible for testing.
    internal class KeyPool : BaseKeyPool<Key?>() {
        operator fun get(size: Int, config: Config?): Key {
            val result = get()
            result!!.init(size, config)
            return result
        }

        override fun create(): Key {
            return Key(this)
        }
    }

    // Visible for testing.
    internal class Key(private val pool: KeyPool) : Poolable {

        var size = 0
        private var config: Config? = null

        fun init(size: Int, config: Config?) {
            this.size = size
            this.config = config
        }

        override fun offer() {
            pool.offer(this)
        }

        override fun toString(): String {
            return getBitmapString(size, config)
        }

        override fun equals(other: Any?): Boolean {
            if (other is Key) {
                return size == other.size && config == other.config
            }
            return false
        }

        override fun hashCode(): Int {
            var result = size
            result = 31 * result + if (config != null) config.hashCode() else 0
            return result
        }
    }

    companion object {
        private const val MAX_SIZE_MULTIPLE = 8
        private val ARGB_8888_IN_CONFIGS: Array<Config?>
        private val RGBA_F16_IN_CONFIGS: Array<Config?>

        // We probably could allow ARGB_4444 and RGB_565 to decode into each other, but ARGB_4444 is
        // deprecated and we'd rather be safe.
        private val RGB_565_IN_CONFIGS = arrayOf<Config?>(RGB_565)
        private val ARGB_4444_IN_CONFIGS = arrayOf<Config?>(ARGB_4444)
        private val ALPHA_8_IN_CONFIGS = arrayOf<Config?>(ALPHA_8)

        init {
            // null: The value returned by Bitmaps with the hidden Bitmap config.
            var result = arrayOf(ARGB_8888, null)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                result = result.copyOf(result.size + 1)
                result[result.size - 1] = RGBA_F16
            }
            ARGB_8888_IN_CONFIGS = result
            RGBA_F16_IN_CONFIGS = result
        }

        private fun getBitmapString(size: Int, config: Config?): String {
            return "[$size]($config)"
        }

        private fun getInConfigs(requested: Config?): Array<Config?> {
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                if (RGBA_F16 == requested) { // NOPMD - Avoid short circuiting sdk checks.
                    return RGBA_F16_IN_CONFIGS
                }
            }
            return if (requested == null) {
                arrayOf(null)
            } else when (requested) {
                ARGB_8888 -> ARGB_8888_IN_CONFIGS
                RGB_565 -> RGB_565_IN_CONFIGS
                ARGB_4444 -> ARGB_4444_IN_CONFIGS
                ALPHA_8 -> ALPHA_8_IN_CONFIGS
                else -> arrayOf(requested)
            }
        }
    }
}