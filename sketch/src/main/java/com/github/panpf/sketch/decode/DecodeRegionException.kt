package com.github.panpf.sketch.decode

import android.graphics.Rect
import com.github.panpf.sketch.SketchException

class DecodeRegionException(
    cause: Throwable,
    val imageUri: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageMimeType: String,
    val srcRect: Rect,
    val inSampleSize: Int
) : SketchException(cause) {

    @get:Synchronized
    override val cause: Throwable
        get() = super.cause!!
}