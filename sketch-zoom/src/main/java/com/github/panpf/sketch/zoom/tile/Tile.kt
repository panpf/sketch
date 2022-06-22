package com.github.panpf.sketch.zoom.tile

import android.graphics.Bitmap
import android.graphics.Rect
import com.github.panpf.sketch.cache.CountBitmap
import kotlinx.coroutines.Job

class Tile constructor(val srcRect: Rect, val inSampleSize: Int) {

    var countBitmap: CountBitmap? = null
        set(value) {
            field?.setIsDisplayed(false, "Tile")
            field = value
            value?.setIsDisplayed(true, "Tile")
        }
    val bitmap: Bitmap?
        get() = countBitmap?.bitmap
    var loadJob: Job? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tile) return false

        if (srcRect != other.srcRect) return false
        if (inSampleSize != other.inSampleSize) return false
        if (bitmap != other.bitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = srcRect.hashCode()
        result = 31 * result + inSampleSize
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Tile(srcRect=$srcRect, inSampleSize=$inSampleSize, bitmap=${bitmap?.run { "Bitmap(${width}x${height},$config)" }})"
    }
}