package com.github.panpf.zoomimage.sketch

import android.graphics.Bitmap
import com.github.panpf.sketch.cache.BitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.zoomimage.sketch.internal.toHexString
import com.github.panpf.zoomimage.subsampling.AndroidCacheTileBitmap


class SketchTileBitmap constructor(
    override val key: String,
    private val cacheValue: MemoryCache.Value,
    private val caller: String,
) : AndroidCacheTileBitmap {

    override val bitmap: Bitmap?
        get() = when (cacheValue) {
            is BitmapImageValue -> cacheValue.image.bitmap
//            is CountBitmapValue -> cacheValue.countBitmap.bitmap
            else -> null
        }

    override val width: Int = bitmap!!.width

    override val height: Int = bitmap!!.height

    override val byteCount: Int = bitmap!!.byteCount

    override val isRecycled: Boolean
        get() = bitmap?.isRecycled ?: true

    private val toString =
        "SketchTileBitmap(size=${width}x${height},config=${bitmap!!.config},@${bitmap!!.toHexString()})"

    override fun recycle() {
        bitmap?.recycle()
    }

    override fun setIsDisplayed(displayed: Boolean) {
//        if (cacheValue is CountBitmapValue) {
//            cacheValue.countBitmap.setIsDisplayed(displayed, caller)
//        }
    }

    override fun toString(): String {
        return toString
    }
}