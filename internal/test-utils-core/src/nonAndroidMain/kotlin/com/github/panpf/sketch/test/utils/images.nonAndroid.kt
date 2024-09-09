package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.SkiaBitmapImageValue
import com.github.panpf.sketch.util.getPixel
import com.github.panpf.sketch.util.hasAlphaPixels

actual fun createImage(width: Int, height: Int): Image {
    return SkiaBitmap().apply {
        allocN32Pixels(width, height)
    }.asSketchImage()
}

actual fun createCacheValue(image: Image, extras: Map<String, Any?>): MemoryCache.Value {
    return SkiaBitmapImageValue(image = image as SkiaBitmapImage, extras = extras)
}

actual fun Image.hasAlphaPixels(): Boolean = (this as SkiaBitmapImage).bitmap.hasAlphaPixels()

/**
 * Returns the Color at the specified location.
 */
actual fun Image.getPixel(x: Int, y: Int): Int {
    return (this as SkiaBitmapImage).bitmap.getPixel(x, y)
}