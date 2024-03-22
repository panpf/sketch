package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.SketchRect
import com.github.panpf.sketch.util.toSkiaRect
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import kotlin.math.ceil


/* ************************************** sampling ********************************************** */

actual fun getMaxBitmapSize(targetSize: Size): Size {
    return Size(targetSize.width * 2, targetSize.height * 2)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapFactory or ImageDecoder
 */
actual fun calculateSampledBitmapSize(
    imageSize: Size,
    sampleSize: Int,
    mimeType: String?
): Size {
    val widthValue = imageSize.width / sampleSize.toDouble()
    val heightValue = imageSize.height / sampleSize.toDouble()
    val width: Int = ceil(widthValue).toInt()
    val height: Int = ceil(heightValue).toInt()
    return Size(width, height)
}

/**
 * Calculate the size of the sampled Bitmap, support for BitmapRegionDecoder
 */
actual fun calculateSampledBitmapSizeForRegion(
    regionSize: Size,
    sampleSize: Int,
    mimeType: String?,
    imageSize: Size?
): Size = calculateSampledBitmapSize(
    imageSize = regionSize,
    sampleSize = sampleSize,
    mimeType = mimeType
)


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