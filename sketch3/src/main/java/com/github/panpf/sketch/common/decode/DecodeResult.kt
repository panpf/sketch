package com.github.panpf.sketch.common.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.github.panpf.sketch.load.ImageInfo

/**
 * The result of [Decoder.decode].
 */
data class DecodeResult(
    val bitmap: Bitmap,
    val info: ImageInfo,
    val decodeOptions: BitmapFactory.Options
)
