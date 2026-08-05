package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import org.jetbrains.skia.ColorType

actual fun Bitmap.toPreviewBitmap(): Any = this

actual val defaultColorType: ColorType
    get() = ColorType.RGBA_8888