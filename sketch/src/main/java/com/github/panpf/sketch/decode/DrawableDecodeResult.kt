package com.github.panpf.sketch.decode

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.DataFrom

data class DrawableDecodeResult(
    val drawable: Drawable,
    val info: ImageInfo,
    val from: DataFrom,
    val transformedList: List<Transformed>? = null
)