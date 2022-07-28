package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.util.Size

class ImageDecodeCompatibility(
    val imageAssetName: String,
    val imageSize: Size,
    val minAPI: Int,
    val sampleSizeMinAPI: Int,
    val inBitmapMinAPI: Int,
    val inBitmapAndInSampleSizeMinAPI: Int,
)