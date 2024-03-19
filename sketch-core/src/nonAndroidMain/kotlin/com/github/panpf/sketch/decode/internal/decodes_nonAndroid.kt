package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.toSkiaRect
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect

internal fun detectImageMimeType(bytes: ByteArray): String? {
    require(bytes.size >= 12) { "Length at least 12" }
    return when {
        bytes.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte())) -> "image/jpeg"
        bytes.startsWith(byteArrayOf(0x89.toByte(), 0x50.toByte())) -> "image/png"
        bytes.startsWith(byteArrayOf(0x47.toByte(), 0x49.toByte())) -> "image/gif"
        bytes.startsWith(byteArrayOf(0x42.toByte(), 0x4D.toByte())) -> "image/bmp"
        bytes.startsWith(byteArrayOf(0x00.toByte(), 0x00.toByte())) -> "image/webp"
        bytes.startsWith(
            byteArrayOf(0x49.toByte(), 0x49.toByte(), 0x2A.toByte(), 0x00.toByte())
        ) -> "image/tiff"

        bytes.startsWith(
            byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x01.toByte(), 0x00.toByte())
        ) -> "image/x-icon"

        bytes.sliceArray(4..7).contentEquals("heic".toByteArray()) -> "image/heic"
        bytes.sliceArray(4..7).contentEquals("heix".toByteArray()) -> "image/heic"
        bytes.sliceArray(4..7).contentEquals("mif1".toByteArray()) -> "image/heif"
        bytes.sliceArray(4..7).contentEquals("msf1".toByteArray()) -> "image/heif"
        else -> null
    }
}

internal fun ByteArray.startsWith(other: ByteArray): Boolean {
    if (other.size > this.size) return false
    return this.sliceArray(other.indices).contentEquals(other)
}

internal fun Image.decode(sampleSize: Int): SkiaBitmap {
    val bitmapSize = calculateSampledBitmapSize(Size(width, height), sampleSize)
    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapSize.width, bitmapSize.height)
    }
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = Rect.makeWH(width.toFloat(), height.toFloat()),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}

internal fun Image.decodeRegion(srcRect: SketchRect, sampleSize: Int): SkiaBitmap {
    val bitmapSize = calculateSampledBitmapSize(Size(srcRect.width(), srcRect.height()), sampleSize)
    val bitmap = Bitmap().apply {
        allocN32Pixels(bitmapSize.width, bitmapSize.height)
    }
    val canvas = Canvas(bitmap)
    canvas.drawImageRect(
        image = this,
        src = srcRect.toSkiaRect(),
        dst = Rect.makeWH(bitmapSize.width.toFloat(), bitmapSize.height.toFloat())
    )
    return bitmap
}