package com.github.panpf.sketch.transform

import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.mask

internal actual fun maskTransformation(image: Image, maskColor: Int): Image? {
    val inputBufferedImage = image.asOrNull<BufferedImageImage>()?.bufferedImage ?: return null
    val outBufferedImage = inputBufferedImage.apply { mask(maskColor) }
    return outBufferedImage.asSketchImage()
}