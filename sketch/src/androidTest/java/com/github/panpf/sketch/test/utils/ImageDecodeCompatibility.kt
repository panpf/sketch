package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.Size

data class ImageDecodeCompatibility(
    val assetName: String,
    val size: Size,
    val minAPI: Int,
    val inSampleSizeMinAPI: Int,
    val inBitmapMinAPI: Int,
    val inSampleSizeOnInBitmapMinAPI: Int,
)