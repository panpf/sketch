package com.github.panpf.zoomimage.sketch

import android.graphics.Bitmap
import com.github.panpf.zoomimage.sketch.internal.toHexString
import com.github.panpf.zoomimage.subsampling.AndroidCacheTileBitmap


class SketchTileBitmap constructor(
    override val key: String,
    override val bitmap: Bitmap,
    private val caller: String,
) : AndroidCacheTileBitmap {

    override val width: Int = bitmap.width

    override val height: Int = bitmap.height

    override val byteCount: Int = bitmap.byteCount

    override val isRecycled: Boolean
        get() = bitmap.isRecycled

    override fun recycle() {
        bitmap.recycle()
    }

    override fun setIsDisplayed(displayed: Boolean) {
//        if (cacheValue is CountBitmapValue) {
//            cacheValue.countBitmap.setIsDisplayed(displayed, caller)
//        }
    }

    override fun toString(): String {
        return "SketchTileBitmap(size=${width}x$height,config=${bitmap.config},@${bitmap.toHexString()})"
    }
}