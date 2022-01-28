package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.ImageData

data class DisplayData constructor(
    val drawable: Drawable,
    val imageInfo: ImageInfo,
    val dataFrom: DataFrom
) : ImageData