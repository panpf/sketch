package com.github.panpf.sketch.transform

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.mask

internal actual fun maskTransformation(image: Image, maskColor: Int): Image {
    require(image is SkiaBitmapImage) {
        "Only SkiaBitmapImage is supported: ${image::class}"
    }
    val inputBitmap = image.bitmap
    val outBitmap: SkiaBitmap = inputBitmap.apply { mask(maskColor) }
    return outBitmap.asSketchImage()
}