package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.util.readIntPixel

expect fun createBitmapImage(width: Int, height: Int): BitmapImage

fun createCacheValue(image: BitmapImage, extras: Map<String, Any?>): MemoryCache.Value {
    return ImageCacheValue(image = image, extras = extras)
}

fun Image.getBitmapOrThrow(): com.github.panpf.sketch.Bitmap = when (this) {
    is BitmapImage -> bitmap
    else -> throw IllegalArgumentException("Unable to get Drawable from Image '$this'")
}

val Image.cornerA: Int
    get() = (this as BitmapImage).bitmap.readIntPixel(0, 0)
val Image.cornerB: Int
    get() = (this as BitmapImage).bitmap.readIntPixel(width - 1, 0)
val Image.cornerC: Int
    get() = (this as BitmapImage).bitmap.readIntPixel(width - 1, height - 1)
val Image.cornerD: Int
    get() = (this as BitmapImage).bitmap.readIntPixel(0, height - 1)

fun Image.corners(block: Image.() -> List<Int>): List<Int> {
    return block(this)
}

fun Image.corners(): List<Int> = listOf(cornerA, cornerB, cornerC, cornerD)