package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.source.DataSource
import okio.buffer
import okio.use
import org.jetbrains.skia.Image

actual fun createBitmapImage(width: Int, height: Int): BitmapImage =
    createBitmap(width, height).asImage()

fun DataSource.toSkiaImage(): Image {
    val exifBytes = openSource().buffer().use { it.readByteArray() }
    return Image.makeFromEncoded(exifBytes)
}