package com.github.panpf.sketch.util.pool

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import com.github.panpf.sketch.util.allocationByteCountCompat
import java.util.Locale

/**
 * A strategy for reusing bitmaps that requires any returned bitmap's dimensions to exactly match those request.
 */
class AttributeStrategy : LruPoolStrategy {

    private val keyPool = KeyPool()
    private val groupedMap = GroupedLinkedMap<Key, Bitmap>()

    override fun put(bitmap: Bitmap) {
        val key = keyPool[bitmap.width, bitmap.height, bitmap.config]
        groupedMap.put(key, bitmap)
    }

    override fun get(width: Int, height: Int, config: Config): Bitmap? {
        val key = keyPool[width, height, config]
        return groupedMap[key]
    }

    override fun exist(width: Int, height: Int, config: Config): Boolean {
        val key = keyPool[width, height, config]
        return groupedMap.exist(key)
    }

    override fun removeLast(): Bitmap? {
        return groupedMap.removeLast()
    }

    override fun logBitmap(bitmap: Bitmap): String {
        return getBitmapString(bitmap)
    }

    override fun logBitmap(width: Int, height: Int, config: Config): String {
        return getBitmapString(width, height, config)
    }

    override fun getSize(bitmap: Bitmap): Int {
        return bitmap.allocationByteCountCompat
    }

    override fun toString(): String {
        return "AttributeStrategy($groupedMap)"
    }

    private class KeyPool : BaseKeyPool<Key?>() {
        operator fun get(width: Int, height: Int, config: Config?): Key {
            val result = get()
            result!!.init(width, height, config)
            return result
        }

        override fun create(): Key {
            return Key(this)
        }
    }

    private class Key(private val pool: KeyPool) : Poolable {
        private var width = 0
        private var height = 0
        private var config: Config? = null
        fun init(width: Int, height: Int, config: Config?) {
            this.width = width
            this.height = height
            this.config = config
        }

        override fun equals(other: Any?): Boolean {
            if (other is Key) {
                return width == other.width && height == other.height && config == other.config
            }
            return false
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + if (config != null) config.hashCode() else 0
            return result
        }

        override fun toString(): String {
            return getBitmapString(width, height, config)
        }

        override fun offer() {
            pool.offer(this)
        }
    }

    companion object {
        private fun getBitmapString(bitmap: Bitmap): String {
            return getBitmapString(bitmap.width, bitmap.height, bitmap.config)
        }

        private fun getBitmapString(width: Int, height: Int, config: Config?): String {
            return String.format(Locale.getDefault(), "[%dx%d](%s)", width, height, config)
        }
    }
}