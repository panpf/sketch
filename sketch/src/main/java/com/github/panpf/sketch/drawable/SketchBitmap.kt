package com.github.panpf.sketch.drawable

import android.graphics.Bitmap
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.util.SketchUtils

abstract class SketchBitmap protected constructor(
    bitmap: Bitmap,
    key: String,
    uri: String,
    attrs: ImageAttrs
) {
    val key: String
    val uri: String
    var bitmap: Bitmap?
        protected set
    val attrs: ImageAttrs
    val byteCount: Int
        get() = SketchUtils.getByteCount(bitmap)
    val bitmapConfig: Bitmap.Config?
        get() = if (bitmap != null) bitmap!!.config else null

    abstract val info: String

    init {
        require(!(bitmap.isRecycled)) { "bitmap is null or recycled" }
        this.bitmap = bitmap
        this.key = key
        this.uri = uri
        this.attrs = attrs
    }
}