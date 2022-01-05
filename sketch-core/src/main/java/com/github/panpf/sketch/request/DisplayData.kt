package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.internal.ImageData

data class DisplayData(
    val drawable: Drawable,
    val info: ImageInfo,
    val from: DataFrom
) : ImageData