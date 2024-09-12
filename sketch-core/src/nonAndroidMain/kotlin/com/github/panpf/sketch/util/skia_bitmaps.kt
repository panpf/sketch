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
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.SkiaImageInfo
import com.github.panpf.sketch.decode.name
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale
import org.jetbrains.skia.BlendMode
import org.jetbrains.skia.BlendMode.SRC_IN
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect
import kotlin.math.ceil
import kotlin.math.min


/**
 * Get a mutable copy of the bitmap, if it is already mutable, return itself
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testMutableCopy
 */
internal fun SkiaBitmap.getMutableCopy(): SkiaBitmap {
    return if (isImmutable) copied() else this
}


/**
 * Returns a log string of this SkiaBitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testToLogString
 */
internal fun SkiaBitmap.toLogString(): String =
    "SkiaBitmap@${toHexString()}(${width}x${height},${colorType},${colorSpace?.name()})"

/**
 * Get an information string suitable for display
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testToLogString
 */
internal fun SkiaBitmap.toInfoString(): String =
    "SkiaBitmap(width=${width}, height=${height}, colorType=${colorType}, colorSpace=${colorSpace?.name()})"

/**
 * Get a short information string suitable for display
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testToLogString
 */
internal fun SkiaBitmap.toShortInfoString(): String =
    "SkiaBitmap(${width}x${height},${colorType},${colorSpace?.name()})"


/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testCopied
 */
internal fun SkiaBitmap.copied(): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap(inputBitmap.imageInfo)
    outBitmap.installPixels(inputBitmap.readPixels())
    return outBitmap
}

/**
 * Reads the pixels of this SkiaBitmap as an intArray.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testReadIntPixels
 */
internal fun SkiaBitmap.readIntPixels(
    x: Int = 0,
    y: Int = 0,
    width: Int = this.width,
    height: Int = this.height
): IntArray? {
    val pixelsConverter = PixelsConverter(this.colorType)
        ?: throw UnsupportedOperationException("Unsupported colorType: ${this.colorType}")
    val imageInfo = SkiaImageInfo(this.colorInfo, width, height)
    val bytePixels = this.readPixels(
        dstInfo = imageInfo,
        dstRowBytes = width * bytesPerPixel,
        srcX = x,
        srcY = y
    ) ?: return null
    return pixelsConverter.bytePixelsToIntPixels(bytePixels)
}

/**
 * Installs the specified intArray as the pixels of this SkiaBitmap.
 */
internal fun SkiaBitmap.installIntPixels(intPixels: IntArray): Boolean {
    val pixelsConverter = PixelsConverter(this.colorType)
        ?: throw UnsupportedOperationException("Unsupported colorType: ${this.colorType}")
    val bytePixels = pixelsConverter.intPixelsToBytePixels(intPixels)
    return installPixels(bytePixels)
}

/**
 * Returns the Color at the specified location.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testGetPixel
 */
fun SkiaBitmap.getPixel(x: Int, y: Int): Int {
    return readIntPixels(x, y, 1, 1)!!.first()
}

/**
 * Returns true if there are transparent pixels
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testHasAlphaPixels
 */
fun SkiaBitmap.hasAlphaPixels(): Boolean {
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


/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap with a background color.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testBackgrounded
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testBackgrounded2
 */
internal fun SkiaBitmap.backgrounded(backgroundColor: Int): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap(inputBitmap.imageInfo)
    val canvas = Canvas(outBitmap)
    canvas.drawRect(
        r = SkiaRect(0f, 0f, outBitmap.width.toFloat(), outBitmap.height.toFloat()),
        paint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
        }
    )
    val sourceImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    } finally {
        sourceImage.close()
    }
    return outBitmap
}

/**
 * Blurs this SkiaBitmap with the specified radius.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testBlur
 */
internal fun SkiaBitmap.blur(radius: Int) {
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels: IntArray = readIntPixels()!!
    fastGaussianBlur(pixels, imageWidth, imageHeight, radius)
    this.installIntPixels(pixels)
}

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap with a circle cropped.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testCircleCropped
 */
internal fun SkiaBitmap.circleCropped(scale: Scale): SkiaBitmap {
    val inputBitmap = this
    val newSize = min(inputBitmap.width, inputBitmap.height)
    var newColorType: ColorType = inputBitmap.colorType
    var newColorAlphaType: ColorAlphaType = inputBitmap.alphaType
    if (inputBitmap.colorType.isAlwaysOpaque) {
        // Circle cropped require support alpha
        newColorType = ColorType.RGBA_8888
    }
    if (newColorAlphaType == ColorAlphaType.UNKNOWN || newColorAlphaType == ColorAlphaType.OPAQUE) {
        newColorAlphaType = ColorAlphaType.PREMUL
    }
    val newImageInfo = SkiaImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = newSize,
        height = newSize
    )
    val outBitmap = SkiaBitmap(newImageInfo)
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
    val skiaImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        val resizeMapping = Resize(Size(newSize, newSize), SAME_ASPECT_RATIO, scale)
            .calculateMapping(Size(inputBitmap.width, inputBitmap.height))
        canvas.drawImageRect(
            image = skiaImage,
            src = resizeMapping.srcRect.toSkiaRect(),
            dst = resizeMapping.dstRect.toSkiaRect(),
            paint = Paint().apply {
                isAntiAlias = true
                blendMode = SRC_IN
            }
        )
    } finally {
        skiaImage.close()
    }
    return outBitmap
}

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap flipped horizontally or vertically.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testFlipped
 */
internal fun SkiaBitmap.flipped(horizontal: Boolean): SkiaBitmap {
    val inputBitmap = this
    val outBitmap = SkiaBitmap(inputBitmap.imageInfo)
    val canvas = Canvas(outBitmap)
    val x = if (horizontal) outBitmap.width.toFloat() else 0f
    val y = if (!horizontal) outBitmap.height.toFloat() else 0f
    canvas.save()
    canvas.translate(x, y)
    canvas.scale(if (horizontal) -1f else 1f, if (!horizontal) -1f else 1f)
    val sourceImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    } finally {
        sourceImage.close()
    }
    canvas.restore()
    return outBitmap
}

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap resized to the specified size.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testMapping
 */
internal fun SkiaBitmap.mapping(mapping: ResizeMapping): SkiaBitmap {
    val inputBitmap = this
    val newWidth = mapping.newSize.width
    val newHeight = mapping.newSize.height
    val newImageInfo = inputBitmap.imageInfo.withWidthHeight(newWidth, newHeight)
    val outBitmap = SkiaBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    val sourceImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImageRect(
            image = sourceImage,
            src = mapping.srcRect.toSkiaRect(),
            dst = mapping.dstRect.toSkiaRect(),
            paint = paint,
        )
    } finally {
        sourceImage.close()
    }
    return outBitmap
}

/**
 * Masks this SkiaBitmap with the specified color.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testMask
 */
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

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap rotated by the specified angle.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testRotated
 */
internal fun SkiaBitmap.rotated(angle: Int): SkiaBitmap {
    val inputBitmap = this
    val inputSize = Size(inputBitmap.width, inputBitmap.height)
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val newSize = calculateRotatedSize(size = inputSize, angle = finalAngle.toDouble())

    // If the Angle is not divisible by 90°, the new image will be oblique, so support transparency so that the oblique part is not black
    var newColorType: ColorType = inputBitmap.colorType
    var newColorAlphaType: ColorAlphaType = inputBitmap.alphaType
    if (finalAngle % 90 != 0 && inputBitmap.colorType.isAlwaysOpaque) {
        // Non-positive angle require support alpha
        newColorType = ColorType.RGBA_8888
    }
    if (newColorAlphaType == ColorAlphaType.UNKNOWN || newColorAlphaType == ColorAlphaType.OPAQUE) {
        newColorAlphaType = ColorAlphaType.PREMUL
    }
    val newImageInfo = SkiaImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = newSize.width,
        height = newSize.height
    )
    val outBitmap = SkiaBitmap(newImageInfo)

    val canvas = Canvas(outBitmap)
    canvas.translate(
        dx = (newSize.width - inputSize.width) / 2.0f,
        dy = (newSize.height - inputSize.height) / 2.0f,
    )
    canvas.rotate(
        deg = finalAngle.toFloat(),
        x = inputSize.width / 2f,
        y = inputSize.height / 2f,
    )
    val skiaImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(skiaImage, 0f, 0f)
    } finally {
        skiaImage.close()
    }
    return outBitmap
}

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap with rounded corners.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testRoundedCornered
 */
internal fun SkiaBitmap.roundedCornered(cornerRadii: FloatArray): SkiaBitmap {
    val inputBitmap = this
    var newColorType: ColorType = inputBitmap.colorType
    var newColorAlphaType: ColorAlphaType = inputBitmap.alphaType
    if (inputBitmap.colorType.isAlwaysOpaque) {
        // Rounded corners require support alpha
        newColorType = ColorType.RGBA_8888
    }
    if (newColorAlphaType == ColorAlphaType.UNKNOWN || newColorAlphaType == ColorAlphaType.OPAQUE) {
        newColorAlphaType = ColorAlphaType.PREMUL
    }
    val newImageInfo = SkiaImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = inputBitmap.width,
        height = inputBitmap.height
    )
    val outBitmap = SkiaBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    canvas.drawRRect(
        r = RRect.makeComplexLTRB(0f, 0f, width.toFloat(), height.toFloat(), cornerRadii),
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
    )
    val sourceImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImageRect(
            image = sourceImage,
            src = SkiaRect.makeWH(inputBitmap.width.toFloat(), inputBitmap.height.toFloat()),
            dst = SkiaRect.makeWH(outBitmap.width.toFloat(), outBitmap.height.toFloat()),
            paint = Paint().apply {
                isAntiAlias = true
                blendMode = SRC_IN
            }
        )
    } finally {
        sourceImage.close()
    }
    return outBitmap
}

/**
 * Returns a new SkiaBitmap that is a copy of this SkiaBitmap scaled by the specified factor.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.SkiaBitmapsTest.testScaled
 */
internal fun SkiaBitmap.scaled(scaleFactor: Float): SkiaBitmap {
    val inputBitmap = this
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newImageInfo = inputBitmap.imageInfo.withWidthHeight(scaledWidth, scaledHeight)
    val outBitmap = SkiaBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    val sourceImage = SkiaImage.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImageRect(
            image = sourceImage,
            src = SkiaRect.makeWH(inputBitmap.width.toFloat(), sourceImage.height.toFloat()),
            dst = SkiaRect.makeWH(outBitmap.width.toFloat(), outBitmap.height.toFloat()),
            paint = paint,
        )
    } finally {
        sourceImage.close()
    }
    return outBitmap
}