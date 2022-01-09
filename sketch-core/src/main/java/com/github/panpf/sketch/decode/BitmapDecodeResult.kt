package com.github.panpf.sketch.decode

import android.graphics.Bitmap
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.ImageInfo

data class BitmapDecodeResult(
    val bitmap: Bitmap,
    val info: ImageInfo,
    val from: DataFrom
)