package com.github.panpf.sketch.transform

import com.github.panpf.sketch.BufferedImageImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.backgrounded
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.hasAlpha
import com.github.panpf.sketch.util.mask


actual fun blurTransformation(
    image: Image,
    radius: Int,
    hasAlphaBitmapBgColor: Int?,
    maskColor: Int?
): Image? {
    val inputBitmap = image.asOrNull<BufferedImageImage>()?.bufferedImage ?: return null
    // Transparent pixels cannot be blurred
    val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlpha()) {
        inputBitmap.backgrounded(hasAlphaBitmapBgColor)
    } else {
        inputBitmap
    }

    val blurImage = compatAlphaBitmap
        .apply { blur(radius) }
    val maskImage = blurImage
        .apply { if (maskColor != null) mask(maskColor) }
    return maskImage.asSketchImage()
}