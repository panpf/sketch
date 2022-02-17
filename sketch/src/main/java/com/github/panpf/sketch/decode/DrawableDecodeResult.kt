package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom

data class DrawableDecodeResult constructor(
    val drawable: Drawable,
    val imageInfo: ImageInfo,
    val exifOrientation: Int,
    val dataFrom: DataFrom,
    val transformedList: List<Transformed>?
)