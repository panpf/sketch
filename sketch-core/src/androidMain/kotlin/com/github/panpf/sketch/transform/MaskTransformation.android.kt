package com.github.panpf.sketch.transform

import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.getMutableCopy
import com.github.panpf.sketch.util.mask

internal actual fun maskTransformation(image: Image, maskColor: Int): Image {
    require(image is AndroidBitmapImage) {
        "Only AndroidBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap = inputBitmap.getMutableCopy().apply { mask(maskColor) }
    return outBitmap.asSketchImage()
}