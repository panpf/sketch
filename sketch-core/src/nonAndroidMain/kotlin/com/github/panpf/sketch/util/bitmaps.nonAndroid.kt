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

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.createBitmap
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
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.RRect
import org.jetbrains.skia.impl.use
import kotlin.math.ceil
import kotlin.math.min


/**
 * Returns a log string of this Bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testToLogString
 */
fun Bitmap.toLogString(): String =
    "Bitmap@${toHexString()}(${width}x${height},${colorType},${colorSpace?.name()})"

/**
 * Get an information string suitable for display
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testToLogString
 */
fun Bitmap.toInfoString(): String =
    "Bitmap(width=${width}, height=${height}, colorType=${colorType}, colorSpace=${colorSpace?.name()})"

/**
 * Get a short information string suitable for display
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testToLogString
 */
fun Bitmap.toShortInfoString(): String =
    "Bitmap(${width}x${height},${colorType},${colorSpace?.name()})"


/**
 * Get a mutable copy of the bitmap
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMutableCopy
 */
actual fun Bitmap.mutableCopy(): Bitmap = copyWith()

/**
 * Get a mutable copy of the bitmap, if it is already mutable, return itself
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMutableCopyOrSelf
 */
actual fun Bitmap.mutableCopyOrSelf(): Bitmap {
    return if (isImmutable) copyWith() else this
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testCopyWith
 */
fun Bitmap.copyWith(colorInfo: ColorInfo = imageInfo.colorInfo): Bitmap {
    val inputBitmap = this
    val outBitmap = createBitmap(inputBitmap.imageInfo.withColorInfo(colorInfo))
    val canvas = Canvas(outBitmap)
    Image.makeFromBitmap(inputBitmap).use { sourceImage ->
        canvas.drawImage(
            image = sourceImage,
            left = 0f,
            top = 0f,
            paint = Paint().apply { isAntiAlias = true },
        )
    }
    return outBitmap
}


/**
 * Returns true if there are transparent pixels
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testHasAlphaPixels
 */
actual fun Bitmap.hasAlphaPixels(): Boolean {
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
 * Read an integer pixel array in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testReadIntPixels
 */
actual fun Bitmap.readIntPixels(
    x: Int, y: Int, width: Int, height: Int
): IntArray {
    val inputBitmap = this
    val rgbaBitmap = if (inputBitmap.colorType == ColorType.RGBA_8888) {
        inputBitmap
    } else {
        createBitmap(inputBitmap.imageInfo.withColorType(ColorType.RGBA_8888)).also { rgbaBitmap ->
            Image.makeFromBitmap(inputBitmap).use { inputImage ->
                Canvas(rgbaBitmap).drawImageRect(
                    image = inputImage,
                    src = org.jetbrains.skia.Rect.makeWH(
                        w = inputBitmap.width.toFloat(),
                        h = inputBitmap.height.toFloat()
                    ),
                    dst = org.jetbrains.skia.Rect.makeWH(
                        w = rgbaBitmap.width.toFloat(),
                        h = rgbaBitmap.height.toFloat()
                    ),
                    paint = Paint().apply {
                        isAntiAlias = true
                    },
                )
            }
        }
    }

    val imageInfo = ImageInfo(rgbaBitmap.colorInfo, width, height)
    val dstRowBytes = width * rgbaBitmap.bytesPerPixel
    val rgbaBytePixels = rgbaBitmap
        .readPixels(dstInfo = imageInfo, dstRowBytes = dstRowBytes, srcX = x, srcY = y)!!

    val argbIntPixels = IntArray(rgbaBytePixels.size / 4)
    for (i in argbIntPixels.indices) {
        val r = rgbaBytePixels[i * 4].toInt() and 0xFF
        val g = rgbaBytePixels[i * 4 + 1].toInt() and 0xFF
        val b = rgbaBytePixels[i * 4 + 2].toInt() and 0xFF
        val a = rgbaBytePixels[i * 4 + 3].toInt() and 0xFF
        argbIntPixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    return argbIntPixels
}

/**
 * Install integer pixels in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testInstallIntPixels
 */
actual fun Bitmap.installIntPixels(intPixels: IntArray) {
    val outBitmap = this

    val rgbaBytePixels = ByteArray(intPixels.size * 4)
    for (i in intPixels.indices) {
        val pixel = intPixels[i]
        val a = pixel shr 24 and 0xFF
        val r = pixel shr 16 and 0xFF
        val g = pixel shr 8 and 0xFF
        val b = pixel and 0xFF
        rgbaBytePixels[i * 4] = r.toByte()
        rgbaBytePixels[i * 4 + 1] = g.toByte()
        rgbaBytePixels[i * 4 + 2] = b.toByte()
        rgbaBytePixels[i * 4 + 3] = a.toByte()
    }
    val rgbaBitmap = createBitmap(outBitmap.imageInfo.withColorType(ColorType.RGBA_8888))
    rgbaBitmap.installPixels(rgbaBytePixels)

    Image.makeFromBitmap(rgbaBitmap).use { rgbaImage ->
        Canvas(outBitmap).drawImageRect(
            image = rgbaImage,
            src = org.jetbrains.skia.Rect.makeWH(
                rgbaBitmap.width.toFloat(),
                rgbaBitmap.height.toFloat()
            ),
            dst = org.jetbrains.skia.Rect.makeWH(
                outBitmap.width.toFloat(),
                outBitmap.height.toFloat()
            ),
            paint = Paint().apply {
                isAntiAlias = true
            },
        )
    }
}

/**
 * Install pixels from a byte array
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testInstallPixels
 */
actual fun Bitmap.installPixels(pixels: ByteArray) {
    this.installPixels(pixels)
}

/**
 * Read pixels from the current Bitmap and return a byte array.
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testInstallPixels
 */
actual fun Bitmap.readPixels(): ByteArray? {
    return this.readPixels()
}

/**
 * Returns the pixel at the specified position in ARGB_8888 format
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testReadIntPixel
 */
actual fun Bitmap.readIntPixel(x: Int, y: Int): Int {
    return readIntPixels(x, y, 1, 1).first()
}


/**
 * Returns a new Bitmap that is a copy of this Bitmap with a background color.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testBackground
 */
actual fun Bitmap.background(color: Int): Bitmap {
    val inputBitmap = this
    val outBitmap = createBitmap(inputBitmap.imageInfo)
    val canvas = Canvas(outBitmap)
    canvas.drawRect(
        r = org.jetbrains.skia.Rect(0f, 0f, outBitmap.width.toFloat(), outBitmap.height.toFloat()),
        paint = Paint().apply {
            isAntiAlias = true
            this.color = color
        }
    )
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    } finally {
        sourceImage.close()
    }
    return outBitmap
}

/**
 * Blurs this Bitmap with the specified radius.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testBlur
 */
actual fun Bitmap.blur(radius: Int, firstReuseSelf: Boolean): Bitmap {
    val inputBitmap = this
    val outBitmap =
        if (firstReuseSelf) inputBitmap.mutableCopyOrSelf() else inputBitmap.mutableCopy()
    val imageWidth = outBitmap.width
    val imageHeight = outBitmap.height
    val intPixels: IntArray = outBitmap.readIntPixels()
    fastGaussianBlur(intPixels, imageWidth, imageHeight, radius)
    outBitmap.installIntPixels(intPixels)
    return outBitmap
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap with a circle cropped.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testCircleCrop
 */
actual fun Bitmap.circleCrop(scale: Scale): Bitmap {
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
    val newImageInfo = ImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = newSize,
        height = newSize
    )
    val outBitmap = createBitmap(newImageInfo)
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
    val skiaImage = Image.makeFromBitmap(inputBitmap)
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
 * Returns a new Bitmap that is a copy of this Bitmap flipped horizontally or vertically.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testFlip
 */
actual fun Bitmap.flip(horizontal: Boolean): Bitmap {
    val inputBitmap = this
    val outBitmap = createBitmap(inputBitmap.imageInfo)
    val canvas = Canvas(outBitmap)
    val x = if (horizontal) outBitmap.width.toFloat() else 0f
    val y = if (!horizontal) outBitmap.height.toFloat() else 0f
    canvas.save()
    canvas.translate(x, y)
    canvas.scale(if (horizontal) -1f else 1f, if (!horizontal) -1f else 1f)
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(image = sourceImage, left = 0f, top = 0f)
    } finally {
        sourceImage.close()
    }
    canvas.restore()
    return outBitmap
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap resized to the specified size.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMapping
 */
actual fun Bitmap.mapping(mapping: ResizeMapping): Bitmap {
    val inputBitmap = this
    val newWidth = mapping.newSize.width
    val newHeight = mapping.newSize.height
    val newImageInfo = inputBitmap.imageInfo.withWidthHeight(newWidth, newHeight)
    val outBitmap = createBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    val sourceImage = Image.makeFromBitmap(inputBitmap)
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
 * Masks this Bitmap with the specified color.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testMask
 */
actual fun Bitmap.mask(maskColor: Int, firstReuseSelf: Boolean): Bitmap {
    val inputBitmap = this
    val outBitmap =
        if (firstReuseSelf) inputBitmap.mutableCopyOrSelf() else inputBitmap.mutableCopy()
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
        color = maskColor
        blendMode = BlendMode.SRC_ATOP
    }
    canvas.drawRect(
        r = org.jetbrains.skia.Rect(0f, 0f, width.toFloat(), height.toFloat()),
        paint = paint
    )
    return outBitmap
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap rotated by the specified angle.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testRotate
 */
actual fun Bitmap.rotate(angle: Int): Bitmap {
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
    val newImageInfo = ImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = newSize.width,
        height = newSize.height
    )
    val outBitmap = createBitmap(newImageInfo)

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
    val skiaImage = Image.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImage(skiaImage, 0f, 0f)
    } finally {
        skiaImage.close()
    }
    return outBitmap
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap with rounded corners.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testRoundedCorners
 */
actual fun Bitmap.roundedCorners(radiusArray: FloatArray): Bitmap {
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
    val newImageInfo = ImageInfo(
        colorInfo = ColorInfo(
            colorType = newColorType,
            alphaType = newColorAlphaType,
            colorSpace = inputBitmap.colorSpace
        ),
        width = inputBitmap.width,
        height = inputBitmap.height
    )
    val outBitmap = createBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    canvas.drawRRect(
        r = RRect.makeComplexLTRB(0f, 0f, width.toFloat(), height.toFloat(), radiusArray),
        paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
        }
    )
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImageRect(
            image = sourceImage,
            src = org.jetbrains.skia.Rect.makeWH(
                inputBitmap.width.toFloat(),
                inputBitmap.height.toFloat()
            ),
            dst = org.jetbrains.skia.Rect.makeWH(
                outBitmap.width.toFloat(),
                outBitmap.height.toFloat()
            ),
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
 * Returns a new Bitmap that is a copy of this Bitmap scaled by the specified factor.
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testScale
 */
actual fun Bitmap.scale(scaleFactor: Float): Bitmap {
    val inputBitmap = this
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newImageInfo = inputBitmap.imageInfo.withWidthHeight(scaledWidth, scaledHeight)
    val outBitmap = createBitmap(newImageInfo)
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        isAntiAlias = true
    }
    val sourceImage = Image.makeFromBitmap(inputBitmap)
    try {
        canvas.drawImageRect(
            image = sourceImage,
            src = org.jetbrains.skia.Rect.makeWH(
                inputBitmap.width.toFloat(),
                sourceImage.height.toFloat()
            ),
            dst = org.jetbrains.skia.Rect.makeWH(
                outBitmap.width.toFloat(),
                outBitmap.height.toFloat()
            ),
            paint = paint,
        )
    } finally {
        sourceImage.close()
    }
    return outBitmap
}

/**
 * Create thumbnails with specified width and height
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.util.BitmapsNonAndroidTest.testThumbnail
 */
actual fun Bitmap.thumbnail(width: Int, height: Int): Bitmap {
    val inputBitmap = this
    val outputBitmap = createBitmap(inputBitmap.imageInfo.withWidthHeight(width, height))
    val canvas = Canvas(outputBitmap)
    Image.makeFromBitmap(inputBitmap).use { skiaImage ->
        canvas.drawImageRect(
            image = skiaImage,
            src = org.jetbrains.skia.Rect(
                0f,
                0f,
                inputBitmap.width.toFloat(),
                inputBitmap.height.toFloat()
            ),
            dst = org.jetbrains.skia.Rect(
                0f,
                0f,
                outputBitmap.width.toFloat(),
                outputBitmap.height.toFloat()
            ),
        )
    }
    return outputBitmap
}

/**
 * Replaces pixel values with color
 */
actual fun Bitmap.erase(color: Int) {
    this.erase(color)
}

/**
 * Copy pixels from another Bitmap to this Bitmap.
 */
actual fun Bitmap.copyPixelsFrom(fromBitmap: Bitmap) {
    require(this.width == fromBitmap.width && this.height == fromBitmap.height) {
        "The width and height of the source Bitmap must be equal to the target Bitmap. " +
                "Source: ${fromBitmap.width}x${fromBitmap.height}, Target: ${this.width}x${this.height}"
    }
    this.installPixels(fromBitmap.readPixels())
}