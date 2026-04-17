package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.images.ComposeResImageFiles
import org.jetbrains.skia.ColorType.RGBA_8888
import org.jetbrains.skia.ColorType.RGB_565

actual fun createBitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height)
}

actual fun createARGBBitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height, RGBA_8888)
}

actual fun create565Bitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height, RGB_565)
}

actual suspend fun getMutableBitmap(): Bitmap {
    return ComposeResImageFiles.jpeg.decode().bitmap
}

actual suspend fun getImmutableBitmap(): Bitmap {
    return ComposeResImageFiles.jpeg.decode().bitmap.apply { setImmutable() }
}