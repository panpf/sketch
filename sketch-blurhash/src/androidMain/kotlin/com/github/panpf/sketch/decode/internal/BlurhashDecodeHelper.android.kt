package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.createBitmap

actual fun createBlurhashBitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height, ColorType.ARGB_8888)
}