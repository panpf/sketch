package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.util.copyWith
import org.jetbrains.skia.ColorType
import org.jetbrains.skiko.toBufferedImage

actual fun Bitmap.toPreviewBitmap(): Any {
    return when (colorType) {
        ColorType.RGB_888X, ColorType.BGRA_8888 -> this
        ColorType.RGB_565 -> this.copyWith(colorInfo.withColorType(ColorType.RGB_888X))
        else -> this.copyWith(colorInfo.withColorType(ColorType.BGRA_8888))
    }.toBufferedImage()
}