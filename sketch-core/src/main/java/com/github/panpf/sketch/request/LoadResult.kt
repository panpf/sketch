package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.request.internal.ImageResult

data class LoadResult(
    val bitmap: Bitmap,
    val info: ImageInfo,
    override val from: DataFrom
) : ImageResult