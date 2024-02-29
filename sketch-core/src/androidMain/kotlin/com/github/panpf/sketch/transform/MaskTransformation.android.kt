package com.github.panpf.sketch.transform

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.safeConfig

internal actual fun maskTransformation(image: Image, maskColor: Int): Image? {
    val inputBitmap = image.asOrNull<BitmapImage>()?.bitmap ?: return null
    val outBitmap = if (inputBitmap.isMutable)
        inputBitmap else inputBitmap.copy(inputBitmap.safeConfig, true)!!
    return outBitmap
        .apply { mask(maskColor) }
        .asSketchImage()
}