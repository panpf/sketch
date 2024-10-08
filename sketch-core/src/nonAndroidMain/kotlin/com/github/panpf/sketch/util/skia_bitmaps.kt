/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.util

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import org.jetbrains.skia.BlendMode
import org.jetbrains.skia.BlendMode.SRC_IN
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect
import kotlin.math.ceil
import kotlin.math.min

internal fun SkiaBitmap.copied(): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap().apply {
        allocPixels(inputBitmap.imageInfo)
    }
    outBitmap.installPixels(inputBitmap.readPixels()!!)
    return outBitmap
}

fun SkiaBitmap.hasAlpha(): Boolean {
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

internal fun SkiaBitmap.installIntPixels(intArray: IntArray): Boolean {
    val bytePixels = convertToByteColorPixels(intArray, imageInfo.colorType)
    return installPixels(bytePixels)
}

internal fun SkiaBitmap.readIntPixels(): IntArray? {
    val bytePixels = readPixels() ?: return null
    return convertToIntColorPixels(bytePixels, imageInfo.colorType)
}

internal fun SkiaBitmap.toLogString(): String {
    return "SkiaBitmap@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorType})"
}


internal fun SkiaBitmap.backgrounded(backgroundColor: Int): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap().apply {
        allocPixels(inputBitmap.imageInfo)
    }
    val canvas = Canvas(outBitmap)
    canvas.drawRect(
        r = SkiaRect(0f, 0f, outBitmap.width.toFloat(), outBitmap.height.toFloat()),
        paint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
        }
    )
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    return outBitmap
}

internal fun SkiaBitmap.blur(radius: Int) {
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels: IntArray = readIntPixels()!!
    fastGaussianBlur(pixels, imageWidth, imageHeight, radius)
    this.installIntPixels(pixels)
}

internal fun SkiaBitmap.circleCropped(scale: Scale): SkiaBitmap {
    val inputBitmap = this
    val newSize = min(inputBitmap.width, inputBitmap.height)
    val resizeMapping = calculateResizeMapping(
        imageWidth = inputBitmap.width,
        imageHeight = inputBitmap.height,
        resizeWidth = newSize,
        resizeHeight = newSize,
        precision = SAME_ASPECT_RATIO,
        scale = scale
    )!!
    val outBitmap = SkiaBitmap().apply {
        val inputImageInfo = inputBitmap.imageInfo
        allocPixels(inputImageInfo.withWidthHeight(width = newSize, height = newSize))
    }
    val canvas = Canvas(outBitmap)
    canvas.drawCircle(
        x = outBitmap.width / 2f,
        y = outBitmap.height / 2f,
        radius = min(outBitmap.width, outBitmap.height) / 2f,
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
    )
    canvas.drawImageRect(
        image = Image.makeFromBitmap(inputBitmap),
        src = resizeMapping.srcRect.toSkiaRect(),
        dst = resizeMapping.destRect.toSkiaRect(),
        paint = Paint().apply {
            isAntiAlias = true
            blendMode = SRC_IN
        }
    )
    return outBitmap
}

internal fun SkiaBitmap.flipped(horizontal: Boolean): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap().apply {
        allocPixels(inputBitmap.imageInfo)
    }
    val canvas = Canvas(outBitmap)
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    val x = if (horizontal) outBitmap.width.toFloat() else 0f
    val y = if (!horizontal) outBitmap.height.toFloat() else 0f
    canvas.save()
    canvas.translate(x, y)
    canvas.scale(if (horizontal) -1f else 1f, if (!horizontal) -1f else 1f)
    canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    canvas.restore()
    return outBitmap
}

internal fun SkiaBitmap.mapping(mapping: ResizeMapping): SkiaBitmap {
    val inputBitmap = this
    val newWidth = mapping.newWidth
    val newHeight = mapping.newHeight
    val outBitmap = SkiaBitmap().apply {
        val inputImageInfo = inputBitmap.imageInfo
        allocPixels(inputImageInfo.withWidthHeight(width = newWidth, height = newHeight))
    }
    val canvas = Canvas(outBitmap)
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    canvas.drawImageRect(
        image = sourceImage,
        src = mapping.srcRect.toSkiaRect(),
        dst = mapping.destRect.toSkiaRect(),
        paint = paint,
    )
    return outBitmap
}

internal fun SkiaBitmap.mask(maskColor: Int) {
    val canvas = Canvas(this)
    val paint = Paint().apply {
        isAntiAlias = true
        color = maskColor
        blendMode = BlendMode.SRC_ATOP
    }
    canvas.drawRect(
        r = SkiaRect(0f, 0f, width.toFloat(), height.toFloat()),
        paint = paint
    )
}

internal fun SkiaBitmap.roundedCornered(cornerRadii: FloatArray): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap().apply {
        allocPixels(inputBitmap.imageInfo)
    }
    val canvas = Canvas(outBitmap)
    canvas.drawRRect(
        r = RRect.makeComplexLTRB(0f, 0f, width.toFloat(), height.toFloat(), cornerRadii),
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
    )
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    canvas.drawImageRect(
        image = sourceImage,
        src = SkiaRect.makeWH(inputBitmap.width.toFloat(), inputBitmap.height.toFloat()),
        dst = SkiaRect.makeWH(outBitmap.width.toFloat(), outBitmap.height.toFloat()),
        paint = Paint().apply {
            isAntiAlias = true
            blendMode = SRC_IN
        }
    )
    return outBitmap
}

internal fun SkiaBitmap.rotated(angle: Int): SkiaBitmap {
    val inputBitmap = this
    val inputSize = Size(inputBitmap.width, inputBitmap.height)
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val outSize = calculateRotatedSize(size = inputSize, angle = finalAngle.toDouble())
    val outBitmap = SkiaBitmap().apply {
        val inputImageInfo = inputBitmap.imageInfo
        allocPixels(inputImageInfo.withWidthHeight(width = outSize.width, height = outSize.height))
    }
    val canvas = Canvas(outBitmap)
    canvas.translate(
        dx = (outSize.width - inputSize.width) / 2.0f,
        dy = (outSize.height - inputSize.height) / 2.0f,
    )
    canvas.rotate(
        deg = finalAngle.toFloat(),
        x = inputSize.width / 2f,
        y = inputSize.height / 2f,
    )
    canvas.drawImage(Image.makeFromBitmap(inputBitmap), 0f, 0f)
    return outBitmap
}

internal fun SkiaBitmap.scaled(scaleFactor: Float): SkiaBitmap {
    val inputBitmap = this
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val outBitmap = SkiaBitmap().apply {
        val inputImageInfo = inputBitmap.imageInfo
        allocPixels(inputImageInfo.withWidthHeight(width = scaledWidth, height = scaledHeight))
    }
    val canvas = Canvas(outBitmap)
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    canvas.drawImageRect(
        image = sourceImage,
        src = SkiaRect.makeWH(inputBitmap.width.toFloat(), sourceImage.height.toFloat()),
        dst = SkiaRect.makeWH(outBitmap.width.toFloat(), outBitmap.height.toFloat()),
        paint = paint,
    )
    return outBitmap
}

/**
 * Returns the Color at the specified location.
 */
fun SkiaBitmap.getPixel(x: Int, y: Int): Int {
    val bitmap = this
    val stride = 1
    val bytesPerPixel = 4
    val colorInfo = bitmap.colorInfo
    val imageInfo = ImageInfo(colorInfo, 1, 1)
    val bytes = bitmap.readPixels(imageInfo, stride * bytesPerPixel, x, y)!!
    val intColorPixels = convertToIntColorPixels(bytes, colorInfo.colorType)
    return intColorPixels.first()
}

private fun convertToIntColorPixels(byteArray: ByteArray, colorType: ColorType): IntArray {
    return when (colorType) {
        ColorType.ALPHA_8 -> {
            val intArray = IntArray(byteArray.size)
            for (i in intArray.indices) {
                val a = byteArray[i].toInt() and 0xFF
                intArray[i] = a shl 24
            }
            intArray
        }

        ColorType.RGB_565 -> {
            val intArray = IntArray(byteArray.size / 2)
            for (i in intArray.indices) {
                val r = byteArray[i * 2].toInt() and 0xF8
                val g = byteArray[i * 2 + 1].toInt() and 0xFC
                val b = byteArray[i * 2 + 2].toInt() and 0xF8
                intArray[i] = (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        ColorType.RGB_888X, ColorType.RGB_101010X -> {
            val intArray = IntArray(byteArray.size / 3)
            for (i in intArray.indices) {
                val r = byteArray[i * 3].toInt() and 0xFF
                val g = byteArray[i * 3 + 1].toInt() and 0xFF
                val b = byteArray[i * 3 + 2].toInt() and 0xFF
                intArray[i] = (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        ColorType.ARGB_4444, ColorType.RGBA_8888, ColorType.BGRA_8888, ColorType.RGBA_1010102 -> {
            val intArray = IntArray(byteArray.size / 4)
            for (i in intArray.indices) {
                val r = byteArray[i * 4].toInt() and 0xFF
                val g = byteArray[i * 4 + 1].toInt() and 0xFF
                val b = byteArray[i * 4 + 2].toInt() and 0xFF
                val a = byteArray[i * 4 + 3].toInt() and 0xFF
                intArray[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
            intArray
        }

        else -> throw IllegalArgumentException("Unsupported color type: $colorType")
    }
}

private fun convertToByteColorPixels(intArray: IntArray, colorType: ColorType): ByteArray {
    return when (colorType) {
        ColorType.ALPHA_8 -> {
            val byteArray = ByteArray(intArray.size)
            for (i in intArray.indices) {
                val a = intArray[i] shr 24
                byteArray[i] = a.toByte()
            }
            byteArray
        }

        ColorType.RGB_565 -> {
            val byteArray = ByteArray(intArray.size * 2)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xF8
                val g = (intArray[i] shr 8) and 0xFC
                val b = intArray[i] and 0xF8
                byteArray[i * 2] = r.toByte()
                byteArray[i * 2 + 1] = g.toByte()
                byteArray[i * 2 + 2] = b.toByte()
            }
            byteArray
        }

        ColorType.RGB_888X, ColorType.RGB_101010X -> {
            val byteArray = ByteArray(intArray.size * 3)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xFF
                val g = (intArray[i] shr 8) and 0xFF
                val b = intArray[i] and 0xFF
                byteArray[i * 3] = r.toByte()
                byteArray[i * 3 + 1] = g.toByte()
                byteArray[i * 3 + 2] = b.toByte()
            }
            byteArray
        }

        ColorType.ARGB_4444, ColorType.RGBA_8888, ColorType.BGRA_8888, ColorType.RGBA_1010102 -> {
            val byteArray = ByteArray(intArray.size * 4)
            for (i in intArray.indices) {
                val r = (intArray[i] shr 16) and 0xFF
                val g = (intArray[i] shr 8) and 0xFF
                val b = intArray[i] and 0xFF
                val a = intArray[i] shr 24
                byteArray[i * 4] = r.toByte()
                byteArray[i * 4 + 1] = g.toByte()
                byteArray[i * 4 + 2] = b.toByte()
                byteArray[i * 4 + 3] = a.toByte()
            }
            byteArray
        }

        else -> throw IllegalArgumentException("Unsupported color type: $colorType")
    }
}
