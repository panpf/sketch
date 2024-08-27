package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
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