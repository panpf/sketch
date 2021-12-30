package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.internal.ImageResult

data class DisplayResult(
    val drawable: Drawable,
    val info: ImageInfo,
    override val from: DataFrom
) : ImageResult