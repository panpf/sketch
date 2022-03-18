package com.github.panpf.sketch.zoom.tile

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.cache.CountBitmap
import kotlinx.coroutines.Job

class Tile constructor(val srcRect: Rect, val inSampleSize: Int) {

    var countBitmap: CountBitmap? = null
        set(value) {
            field?.setIsDisplayed("Tile", false)
            field = value
            value?.setIsDisplayed("Tile", true)
        }
    val bitmap: Bitmap?
        get() = countBitmap?.bitmap
    var loadJob: Job? = null

    // 用来取消解码任务，开始解码这个碎片的时候会获取当时的key
    // 然后在解码过程的各个环节都会检验key是否已经失效
    // 因此如果想取消解码这个碎片，只需刷新key即可
    private val keyCounter = KeyCounter()

    fun isExpired(key: Int): Boolean {
        return keyCounter.key != key
    }

    fun refreshKey() {
        keyCounter.refresh()
    }

    val key: Int
        get() = keyCounter.key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tile

        if (srcRect != other.srcRect) return false
        if (inSampleSize != other.inSampleSize) return false
        if (bitmap != other.bitmap) return false
        if (keyCounter != other.keyCounter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = srcRect.hashCode()
        result = 31 * result + inSampleSize
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        result = 31 * result + keyCounter.hashCode()
        return result
    }

    override fun toString(): String {
        return "Tile(srcRect=$srcRect, inSampleSize=$inSampleSize, key=${keyCounter.key}, bitmap=${bitmap})"
    }
}