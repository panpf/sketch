package com.github.panpf.sketch.util

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.installIntPixels
import com.github.panpf.sketch.readIntPixels
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.ResizeMapping
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import kotlin.math.ceil

internal fun SkiaBitmap.copied(): SkiaBitmap {
    val sourceBitmap = this
    val newBitmap = SkiaBitmap().apply {
        allocPixels(sourceBitmap.imageInfo)
    }
    newBitmap.installPixels(sourceBitmap.readPixels()!!)
    return newBitmap
}

internal fun SkiaBitmap.hasAlpha(): Boolean {
    val height = this.height
    val width = this.width
    var hasAlpha = false
    for (i in 0 until width) {
        for (j in 0 until height) {
            val pixelAlpha = this.getColor(i, j) shr 24
            if (pixelAlpha in 0..254) {
                hasAlpha = true
                break
            }
        }
    }
    return hasAlpha
}

internal fun SkiaBitmap.scaled(scaleFactor: Float): SkiaBitmap {
    val sourceBitmap = this
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newBitmap = SkiaBitmap().apply {
        allocPixels(ImageInfo(sourceBitmap.colorInfo, width = scaledWidth, height = scaledHeight))
    }
    val canvas = Canvas(newBitmap)
    val sourceImage = Image.makeFromBitmap(sourceBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    canvas.drawImageRect(
        image = sourceImage,
        src = SkiaRect.makeWH(sourceBitmap.width.toFloat(), sourceImage.height.toFloat()),
        dst = SkiaRect.makeWH(newBitmap.width.toFloat(), newBitmap.height.toFloat()),
        paint = paint,
    )
    return newBitmap
}

internal fun SkiaBitmap.mapping(mapping: ResizeMapping): SkiaBitmap {
    val sourceBitmap = this
    val newWidth = mapping.newWidth
    val newHeight = mapping.newHeight
    val newBitmap = SkiaBitmap().apply {
        allocPixels(ImageInfo(sourceBitmap.colorInfo, width = newWidth, height = newHeight))
    }
    val canvas = Canvas(newBitmap)
    val sourceImage = Image.makeFromBitmap(sourceBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    canvas.drawImageRect(
        image = sourceImage,
        src = mapping.srcRect.toSkiaRect(),
        dst = mapping.destRect.toSkiaRect(),
        paint = paint,
    )
    return newBitmap
}

internal fun SkiaBitmap.blur(radius: Int): Boolean {
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels: IntArray = readIntPixels()!!
    fastGaussianBlur(pixels, imageWidth, imageHeight, radius)
    this.installIntPixels(pixels)
    return true
}

internal fun SkiaBitmap.backgrounded(backgroundColor: Int): SkiaBitmap {
    val sourceBitmap = this
    val newBitmap = SkiaBitmap().apply {
        allocPixels(sourceBitmap.imageInfo)
    }
    val canvas = Canvas(newBitmap)
    canvas.drawRect(
        r = SkiaRect(0f, 0f, newBitmap.width.toFloat(), newBitmap.height.toFloat()),
        paint = Paint().apply { color = backgroundColor }
    )
    val sourceImage = Image.makeFromBitmap(sourceBitmap)
    canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    return newBitmap
}

internal fun SkiaBitmap.mask(maskColor: Int) {
    TODO("Not yet implemented")
}

internal fun SkiaBitmap.roundedCornered(cornerRadii: FloatArray): SkiaBitmap {
    TODO("Not yet implemented")
}

internal fun SkiaBitmap.circleCropped(scale: Scale): SkiaBitmap {
    TODO("Not yet implemented")
}

internal fun SkiaBitmap.rotated(angle: Int): SkiaBitmap {
    TODO("Not yet implemented")
}

internal fun SkiaBitmap.flipped(horizontal: Boolean): SkiaBitmap {
    TODO("Not yet implemented")
}