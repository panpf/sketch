package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.request.internal.ImageData
import com.github.panpf.sketch.request.internal.ImageResult

data class LoadData(
    val bitmap: Bitmap,
    val info: ImageInfo,
    val from: DataFrom
): ImageData