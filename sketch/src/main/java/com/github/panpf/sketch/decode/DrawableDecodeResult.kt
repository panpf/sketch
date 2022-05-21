package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom

/**
 * The result of [DrawableDecoder.decode]
 */
data class DrawableDecodeResult constructor(
    val drawable: Drawable,
    override val imageInfo: ImageInfo,
    override val exifOrientation: Int,
    override val dataFrom: DataFrom,
    override val transformedList: List<Transformed>?
): DecodeResult