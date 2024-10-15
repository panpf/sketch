package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.resize.LongImageDecider
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.PrecisionDecider
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.util.Size

fun LongImageDecider.isLongImage(
    imageWidth: Int,
    imageHeight: Int,
    targetWidth: Int,
    targetHeight: Int
): Boolean {
    return isLongImage(
        imageSize = Size(width = imageWidth, height = imageHeight),
        targetSize = Size(width = targetWidth, height = targetHeight)
    )
}

fun PrecisionDecider.get(
    imageWidth: Int,
    imageHeight: Int,
    targetWidth: Int,
    targetHeight: Int
): Precision {
    return get(
        imageSize = Size(width = imageWidth, height = imageHeight),
        targetSize = Size(width = targetWidth, height = targetHeight)
    )
}

fun ScaleDecider.get(
    imageWidth: Int,
    imageHeight: Int,
    targetWidth: Int,
    targetHeight: Int
): Scale {
    return get(
        imageSize = Size(width = imageWidth, height = imageHeight),
        targetSize = Size(width = targetWidth, height = targetHeight)
    )
}