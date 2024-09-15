package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.util.Size
import org.jetbrains.skia.ColorType.RGBA_8888
import org.jetbrains.skia.ColorType.RGB_565

val SkiaBitmap.size: Size
    get() = Size(width, height)

actual fun createBitmap(width: Int, height: Int): Bitmap {
    return SkiaBitmap(width, height)
}

actual fun createARGBBitmap(width: Int, height: Int): Bitmap {
    return SkiaBitmap(width, height, RGBA_8888)
}

actual fun create565Bitmap(width: Int, height: Int): Bitmap {
    return SkiaBitmap(width, height, RGB_565)
}

actual fun getMutableBitmap(): Bitmap {
    return ResourceImages.jpeg.decode().bitmap
}

actual fun getImmutableBitmap(): Bitmap {
    return ResourceImages.jpeg.decode().bitmap.apply { setImmutable() }
}