package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import androidx.core.graphics.get
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.AndroidBitmapImageValue
import com.github.panpf.sketch.cache.MemoryCache

actual fun createImage(width: Int, height: Int): Image {
    return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).asSketchImage()
}

actual fun createCacheValue(image: Image, extras: Map<String, Any?>): MemoryCache.Value {
    return AndroidBitmapImageValue(image = image as AndroidBitmapImage, extras = extras)
}

actual fun Image.hasAlpha(): Boolean = (this as AndroidBitmapImage).bitmap.hasAlpha()

/**
 * Returns the Color at the specified location.
 */
actual fun Image.getPixel(x: Int, y: Int): Int {
    return (this as AndroidBitmapImage).bitmap.get(x, y)
}