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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.media.ThumbnailUtils
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.createEmptyBitmapWith
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale
import kotlin.math.ceil
import kotlin.math.min

/**
 * Check if the current Bitmap configuration is HARDWARE
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testIsHardware
 */
fun ColorType.isHardware(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.O && this == ColorType.HARDWARE

/**
 * Gets the safe mutable bitmap configuration, returns ARGB_8888 if it is HARDWARE, otherwise returns itself
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testSafeToSoftware
 */
fun ColorType?.safeToSoftware(): ColorType =
    if (this == null || (VERSION.SDK_INT >= VERSION_CODES.O && this == ColorType.HARDWARE)) ColorType.ARGB_8888 else this

/**
 * Gets the number of bytes occupied by a single pixel in a specified configuration
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBytesPerPixel
 */
val ColorType.bytesPerPixel: Int
    get() {
        @Suppress("DEPRECATION")
        return when {
            this == ColorType.ALPHA_8 -> 1
            this == ColorType.RGB_565 || this == ColorType.ARGB_4444 -> 2
            this == ColorType.ARGB_8888 -> 4
            VERSION.SDK_INT >= VERSION_CODES.O && this == ColorType.RGBA_F16 -> 8
            else -> 4
        }
    }

/**
 * Get the simple name of the color space
 */
val ColorSpace.simpleName: String
    @RequiresApi(VERSION_CODES.O)
    get() {
        return when {
            this == ColorSpace.get(ColorSpace.Named.SRGB) -> "SRGB"
            this == ColorSpace.get(ColorSpace.Named.LINEAR_SRGB) -> "LINEAR_SRGB"
            this == ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB) -> "EXTENDED_SRGB"
            this == ColorSpace.get(ColorSpace.Named.LINEAR_EXTENDED_SRGB) -> "LINEAR_EXTENDED_SRGB"
            this == ColorSpace.get(ColorSpace.Named.BT709) -> "BT709"
            this == ColorSpace.get(ColorSpace.Named.BT2020) -> "BT2020"
            this == ColorSpace.get(ColorSpace.Named.DCI_P3) -> "DCI_P3"
            this == ColorSpace.get(ColorSpace.Named.DISPLAY_P3) -> "DISPLAY_P3"
            this == ColorSpace.get(ColorSpace.Named.NTSC_1953) -> "NTSC_1953"
            this == ColorSpace.get(ColorSpace.Named.SMPTE_C) -> "SMPTE_C"
            this == ColorSpace.get(ColorSpace.Named.ADOBE_RGB) -> "ADOBE_RGB"
            this == ColorSpace.get(ColorSpace.Named.PRO_PHOTO_RGB) -> "PRO_PHOTO_RGB"
            this == ColorSpace.get(ColorSpace.Named.ACES) -> "ACES"
            this == ColorSpace.get(ColorSpace.Named.ACESCG) -> "ACESCG"
            this == ColorSpace.get(ColorSpace.Named.CIE_XYZ) -> "CIE_XYZ"
            this == ColorSpace.get(ColorSpace.Named.CIE_LAB) -> "CIE_LAB"
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE && this == ColorSpace.get(ColorSpace.Named.BT2020_HLG) -> "BT2020_HLG"
            VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE && this == ColorSpace.get(ColorSpace.Named.BT2020_PQ) -> "BT2020_PQ"
            else -> name
        }
    }


/**
 * Get the configuration of the bitmap, if it is null, return null
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testConfigOrNull
 */
@Suppress("USELESS_ELVIS")
val Bitmap.configOrNull: ColorType?
    get() = config ?: null

/**
 * Get the configuration of the bitmap, if it is null, return [ColorType].ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testSafeConfig
 */
@Suppress("USELESS_ELVIS")
val Bitmap.safeConfig: ColorType
    get() = config ?: ColorType.ARGB_8888


/**
 * Get the string applicable to the log
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testToLogString
 */
fun Bitmap.toLogString(): String =
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        "Bitmap@${toHexString()}(${width}x${height},$configOrNull,${colorSpace?.simpleName})"
    } else {
        "Bitmap@${toHexString()}(${width}x${height},$configOrNull)"
    }

/**
 * Get an information string suitable for display
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testToInfoString
 */
fun Bitmap.toInfoString(): String =
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        "Bitmap(width=${width}, height=${height}, config=$configOrNull, colorSpace=${colorSpace?.simpleName})"
    } else {
        "Bitmap(width=${width}, height=${height}, config=$configOrNull)"
    }

/**
 * Get a short information string suitable for display
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testToShortInfoString
 */
fun Bitmap.toShortInfoString(): String =
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        "Bitmap(${width}x${height},$configOrNull,${colorSpace?.simpleName})"
    } else {
        "Bitmap(${width}x${height},$configOrNull)"
    }


/**
 * Get a mutable copy of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMutableCopy
 */
actual fun Bitmap.mutableCopy(): Bitmap {
    return this.copyWith(config = safeConfig, isMutable = true)
}

/**
 * Get a mutable copy of the bitmap, if it is already mutable, return itself
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMutableCopyOrSelf
 */
actual fun Bitmap.mutableCopyOrSelf(): Bitmap {
    return if (!isMutable) this.copyWith(config = safeConfig, isMutable = true) else this
}

/**
 * Get a copy of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testCopyWith
 */
fun Bitmap.copyWith(
    config: ColorType = safeConfig,
    isMutable: Boolean = isMutable()
): Bitmap {
    return this.copy(/* config = */ config, /* isMutable = */ isMutable)
}


/**
 * Returns true if there are transparent pixels
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testHasAlphaPixels
 */
actual fun Bitmap.hasAlphaPixels(): Boolean {
    val height = this.height
    val width = this.width
    var hasAlpha = false
    for (i in 0 until width) {
        for (j in 0 until height) {
            val pixelAlpha = this.getPixel(i, j) shr 24
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
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testReadIntPixels
 */
actual fun Bitmap.readIntPixels(
    x: Int, y: Int, width: Int, height: Int
): IntArray {
    val pixels = IntArray(width * height)
    getPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ width,
        /* x = */ x,
        /* y = */ y,
        /* width = */ width,
        /* height = */ height
    )
    return pixels
}

/**
 * Install integer pixels in the format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testInstallIntPixels
 */
actual fun Bitmap.installIntPixels(intPixels: IntArray) {
    setPixels(
        /* pixels = */ intPixels,
        /* offset = */ 0,
        /* stride = */ width,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ width,
        /* height = */ height
    )
}

/**
 * Returns the Color at the specified location. Format ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testReadIntPixel
 */
actual fun Bitmap.readIntPixel(x: Int, y: Int): Int = getPixel(x, y)


/**
 * Add a background color to the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBackground
 */
actual fun Bitmap.background(color: Int): Bitmap {
    val inputBitmap = this
    val bitmap = inputBitmap.createEmptyBitmapWith(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* config = */ inputBitmap.safeConfig.safeToSoftware(),
    )
    val canvas = Canvas(bitmap)
    canvas.drawColor(color)
    canvas.drawBitmap(inputBitmap, 0f, 0f, null)
    return bitmap
}

/**
 * Blur the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testBlur
 */
actual fun Bitmap.blur(radius: Int, firstReuseSelf: Boolean): Bitmap {
    val inputBitmap = this
    val outBitmap = if (firstReuseSelf)
        inputBitmap.mutableCopyOrSelf() else inputBitmap.mutableCopy()
    val imageWidth = outBitmap.width
    val imageHeight = outBitmap.height
    val pixels = IntArray(imageWidth * imageHeight)
    outBitmap.getPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ imageWidth,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ imageWidth,
        /* height = */ imageHeight
    )
    fastGaussianBlur(pixels = pixels, width = imageWidth, height = imageHeight, radius = radius)
    outBitmap.setPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ imageWidth,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ imageWidth,
        /* height = */ imageHeight
    )
    return outBitmap
}

/**
 * Crop the current Bitmap into a circle
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testCircleCrop
 */
actual fun Bitmap.circleCrop(scale: Scale): Bitmap {
    val inputBitmap = this
    val newSize = min(inputBitmap.width, inputBitmap.height)
    var newConfig = inputBitmap.safeConfig.safeToSoftware()
    if (newConfig == ColorType.RGB_565) {
        // Circle cropped require support alpha
        newConfig = ColorType.ARGB_8888
    }
    val outBitmap = inputBitmap.createEmptyBitmapWith(
        width = newSize,
        height = newSize,
        colorType = newConfig,
        hasAlpha = true
    )
    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    val canvas = Canvas(outBitmap).apply {
        drawARGB(0, 0, 0, 0)
    }
    canvas.drawCircle(
        /* cx = */ outBitmap.width / 2f,
        /* cy = */ outBitmap.height / 2f,
        /* radius = */ min(outBitmap.width, outBitmap.height) / 2f,
        /* paint = */ paint
    )
    paint.xfermode = PorterDuffXfermode(SRC_IN)
    val resizeMapping = Resize(
        size = Size(newSize, newSize),
        precision = SAME_ASPECT_RATIO,
        scale = scale
    ).calculateMapping(Size(inputBitmap.width, inputBitmap.height))
    canvas.drawBitmap(
        /* bitmap = */ inputBitmap,
        /* src = */ resizeMapping.srcRect.toAndroidRect(),
        /* dst = */ resizeMapping.dstRect.toAndroidRect(),
        /* paint = */ paint
    )
    return outBitmap
}

/**
 * Returns a new Bitmap that is a copy of this Bitmap flip horizontally or vertically.
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testFlip
 */
actual fun Bitmap.flip(horizontal: Boolean): Bitmap {
    val inputBitmap = this
    val matrix = Matrix()
    if (horizontal) {
        matrix.postScale(-1f, 1f)
    } else {
        matrix.postScale(1f, -1f)
    }
    return Bitmap.createBitmap(
        /* source = */ inputBitmap,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* m = */ matrix,
        /* filter = */ true
    )
}

/**
 * Resize the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMapping
 */
actual fun Bitmap.mapping(mapping: ResizeMapping): Bitmap {
    val inputBitmap = this
    val newConfig = inputBitmap.safeConfig.safeToSoftware()
    val outBitmap = inputBitmap.createEmptyBitmapWith(
        size = mapping.newSize,
        colorType = newConfig
    )
    Canvas(outBitmap).drawBitmap(
        /* bitmap = */ inputBitmap,
        /* src = */ mapping.srcRect.toAndroidRect(),
        /* dst = */ mapping.dstRect.toAndroidRect(),
        /* paint = */ null
    )
    return outBitmap
}

/**
 * Mask the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testMask
 */
actual fun Bitmap.mask(maskColor: Int, firstReuseSelf: Boolean): Bitmap {
    val inputBitmap = this
    val outBitmap = if (firstReuseSelf)
        inputBitmap.mutableCopyOrSelf() else inputBitmap.mutableCopy()
    val canvas = Canvas(outBitmap)
    val paint = Paint().apply {
        color = maskColor
        xfermode = PorterDuffXfermode(SRC_ATOP)
    }
    canvas.drawRect(
        /* left = */ 0f,
        /* top = */ 0f,
        /* right = */ outBitmap.width.toFloat(),
        /* bottom = */ outBitmap.height.toFloat(),
        /* paint = */ paint
    )
    return outBitmap
}

/**
 * Rotate the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testRotate
 */
actual fun Bitmap.rotate(angle: Int): Bitmap {
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val inputBitmap = this
    val matrix = Matrix().apply {
        setRotate(finalAngle.toFloat())
    }
    val newRect = RectF(
        /* left = */ 0f,
        /* top = */ 0f,
        /* right = */ inputBitmap.width.toFloat(),
        /* bottom = */ inputBitmap.height.toFloat()
    )
    matrix.mapRect(newRect)
    val newWidth = newRect.width().toInt()
    val newHeight = newRect.height().toInt()

    // If the Angle is not divisible by 90°, the new image will be oblique, so support transparency so that the oblique part is not black
    var newConfig = inputBitmap.safeConfig.safeToSoftware()
    if (finalAngle % 90 != 0 && newConfig == ColorType.RGB_565) {
        // Non-positive angle require support alpha
        newConfig = ColorType.ARGB_8888
    }
    val outBitmap = inputBitmap.createEmptyBitmapWith(
        width = newWidth,
        height = newHeight,
        colorType = newConfig,
        hasAlpha = true
    )

    matrix.postTranslate(-newRect.left, -newRect.top)
    val canvas = Canvas(outBitmap)
    val paint = Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
    canvas.drawBitmap(inputBitmap, matrix, paint)
    return outBitmap
}

/**
 * Add rounded corners to the current Bitmap
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testRoundedCorners
 */
actual fun Bitmap.roundedCorners(radiusArray: FloatArray): Bitmap {
    val inputBitmap = this
    var newConfig = inputBitmap.safeConfig.safeToSoftware()
    if (newConfig == ColorType.RGB_565) {
        // Rounded corners require support alpha
        newConfig = ColorType.ARGB_8888
    }
    val outBitmap = inputBitmap.createEmptyBitmapWith(
        width = inputBitmap.width,
        height = inputBitmap.height,
        colorType = newConfig,
        hasAlpha = true
    )
    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    val canvas = Canvas(outBitmap).apply {
        drawARGB(0, 0, 0, 0)
    }
    val path = Path().apply {
        val rect = RectF(
            /* left = */ 0f,
            /* top = */ 0f,
            /* right = */ inputBitmap.width.toFloat(),
            /* bottom = */ inputBitmap.height.toFloat()
        )
        addRoundRect(rect, radiusArray, Path.Direction.CW)
    }
    canvas.drawPath(path, paint)

    paint.xfermode = PorterDuffXfermode(SRC_IN)
    val rect = Rect(0, 0, inputBitmap.width, inputBitmap.height)
    canvas.drawBitmap(inputBitmap, rect, rect, paint)
    return outBitmap
}

/**
 * Zoom current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testScale
 */
actual fun Bitmap.scale(scaleFactor: Float): Bitmap {
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newConfig = this.safeConfig.safeToSoftware()
    val outputBitmap = this.createEmptyBitmapWith(
        width = scaledWidth,
        height = scaledHeight,
        colorType = newConfig,
    )
    val canvas = Canvas(outputBitmap)
    val matrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
    }
    canvas.drawBitmap(this, matrix, null)
    return outputBitmap
}

/**
 * Create thumbnails with specified width and height
 *
 * @see com.github.panpf.sketch.core.android.test.util.BitmapsAndroidTest.testThumbnail
 */
actual fun Bitmap.thumbnail(width: Int, height: Int): Bitmap {
    val outputBitmap = ThumbnailUtils.extractThumbnail(this, width, height)
    return outputBitmap
}

/**
 * Replaces pixel values with color
 */
actual fun Bitmap.erase(color: Int) {
    this.eraseColor(color)
}

/**
 * Copy pixels from another Bitmap to this Bitmap.
 */
actual fun Bitmap.copyPixelsFrom(fromBitmap: Bitmap) {
    require(this.width == fromBitmap.width && this.height == fromBitmap.height) {
        "The width and height of the source Bitmap must be equal to the target Bitmap. " +
                "Source: ${fromBitmap.width}x${fromBitmap.height}, Target: ${this.width}x${this.height}"
    }
    val pixels = IntArray(width * height)
    fromBitmap.getPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ width,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ width,
        /* height = */ height
    )
    this.setPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ width,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ width,
        /* height = */ height
    )
}