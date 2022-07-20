package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom

/**
 * The result of [DrawableDecoder.decode]
 */
data class DrawableDecodeResult constructor(
    val drawable: Drawable,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom,
    val transformedList: List<String>?
)