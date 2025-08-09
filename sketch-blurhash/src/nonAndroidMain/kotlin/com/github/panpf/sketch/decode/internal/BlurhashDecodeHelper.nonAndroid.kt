package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.createBitmap
import org.jetbrains.skia.ColorType

actual fun createBlurhashBitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height, ColorType.RGBA_8888)
}