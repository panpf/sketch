package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.SketchException

class InBitmapDecodeException(
    cause: Throwable,
    val imageUri: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageMimeType: String,
    val inSampleSize: Int,
    val inBitmap: Bitmap
) : SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}