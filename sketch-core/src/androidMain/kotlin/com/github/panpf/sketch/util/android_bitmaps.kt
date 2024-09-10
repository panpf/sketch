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
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.AndroidBitmapConfig
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.ResizeMapping
import com.github.panpf.sketch.resize.Scale
import kotlin.math.ceil
import kotlin.math.min

/**
 * Check if the current Bitmap configuration is HARDWARE
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testIsHardware
 */
internal fun AndroidBitmapConfig.isHardware(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.O && this == AndroidBitmapConfig.HARDWARE

/**
 * Gets the number of bytes occupied by a single pixel in a specified configuration
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testGetBytesPerPixel
 */
internal fun AndroidBitmapConfig?.getBytesPerPixel(): Int {
    // A bitmap by decoding a gif has null "config" in certain environments.
    val config = this ?: AndroidBitmapConfig.ARGB_8888
    @Suppress("DEPRECATION")
    return when {
        config == AndroidBitmapConfig.ALPHA_8 -> 1
        config == AndroidBitmapConfig.RGB_565 || config == AndroidBitmapConfig.ARGB_4444 -> 2
        config == AndroidBitmapConfig.ARGB_8888 -> 4
        VERSION.SDK_INT >= VERSION_CODES.O && config == AndroidBitmapConfig.RGBA_F16 -> 8
        else -> 4
    }
}


/**
 * The number of bytes required for calculation based on width, height, and configuration
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testAllocationByteCountCompat
 */
@Suppress("ObsoleteSdkInt")
internal val AndroidBitmap.allocationByteCountCompat: Int
    get() = when {
        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> this.allocationByteCount
        else -> this.byteCount
    }

/**
 * Get the configuration of the bitmap, if it is null, return null
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testConfigOrNull
 */
@Suppress("USELESS_ELVIS")
internal val AndroidBitmap.configOrNull: AndroidBitmapConfig?
    get() = config ?: null

/**
 * Whether the bitmap is immutable
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testIsImmutable
 */
internal val AndroidBitmap.isImmutable: Boolean
    get() = !isMutable

/**
 * Get the configuration of the bitmap, if it is null, return [AndroidBitmapConfig].ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testSafeConfig
 */
@Suppress("USELESS_ELVIS")
internal val AndroidBitmap.safeConfig: AndroidBitmapConfig
    get() = config ?: AndroidBitmapConfig.ARGB_8888

/**
 * Get a mutable copy of the bitmap, if it is already mutable, return itself
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testMutableCopy
 */
internal fun AndroidBitmap.getMutableCopy(): AndroidBitmap {
    return if (isMutable) this else copy(safeConfig, true)
}


/**
 * Get the string applicable to the log
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testToLogString
 */
internal fun AndroidBitmap.toLogString(): String =
    "AndroidBitmap@${toHexString()}(${width}x${height},$configOrNull)"

/**
 * Get an information string suitable for display
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testToInfoString
 */
internal fun AndroidBitmap.toInfoString(): String =
    "AndroidBitmap(width=${width}, height=${height}, config=$configOrNull)"

/**
 * Get a short information string suitable for display
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testToShortInfoString
 */
internal fun AndroidBitmap.toShortInfoString(): String =
    "AndroidBitmap(${width}x${height},$configOrNull)"


/**
 * Returns true if there are transparent pixels
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testHasAlphaPixels
 */
fun AndroidBitmap.hasAlphaPixels(): Boolean {
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
 * Add a background color to the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testBackgrounded
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testBackgrounded2
 */
internal fun AndroidBitmap.backgrounded(backgroundColor: Int): AndroidBitmap {
    val inputBitmap = this
    val bitmap = AndroidBitmap.createBitmap(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* config = */ inputBitmap.safeConfig,
    )
    val canvas = Canvas(bitmap)
    canvas.drawColor(backgroundColor)
    canvas.drawBitmap(inputBitmap, 0f, 0f, null)
    return bitmap
}

/**
 * Blur the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testBlur
 */
internal fun AndroidBitmap.blur(radius: Int) {
    require(isMutable) { "AndroidBitmap must be mutable" }
    val imageWidth = this.width
    val imageHeight = this.height
    val pixels = IntArray(imageWidth * imageHeight)
    this.getPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ imageWidth,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ imageWidth,
        /* height = */ imageHeight
    )
    fastGaussianBlur(pixels = pixels, width = imageWidth, height = imageHeight, radius = radius)
    this.setPixels(
        /* pixels = */ pixels,
        /* offset = */ 0,
        /* stride = */ imageWidth,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ imageWidth,
        /* height = */ imageHeight
    )
}

/**
 * Crop the current Bitmap into a circle
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testCircleCropped
 */
internal fun AndroidBitmap.circleCropped(scale: Scale): AndroidBitmap {
    val inputBitmap = this
    val newSize = min(inputBitmap.width, inputBitmap.height)
    val resizeMapping = Resize(Size(newSize, newSize), SAME_ASPECT_RATIO, scale)
        .calculateMapping(Size(inputBitmap.width, inputBitmap.height))
    val outBitmap = AndroidBitmap.createBitmap(
        /* width = */ resizeMapping.newSize.width,
        /* height = */ resizeMapping.newSize.height,
        /* config = */ inputBitmap.safeConfig,
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
    canvas.drawBitmap(
        /* bitmap = */ inputBitmap,
        /* src = */ resizeMapping.srcRect.toAndroidRect(),
        /* dst = */ resizeMapping.dstRect.toAndroidRect(),
        /* paint = */ paint
    )
    return outBitmap
}

/**
 * Resize the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testMapping
 */
internal fun AndroidBitmap.mapping(mapping: ResizeMapping): AndroidBitmap {
    val inputBitmap = this
    val outBitmap = AndroidBitmap.createBitmap(
        /* width = */ mapping.newSize.width,
        /* height = */ mapping.newSize.height,
        /* config = */ inputBitmap.safeConfig,
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
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testMask
 */
internal fun AndroidBitmap.mask(maskColor: Int) {
    require(isMutable) { "AndroidBitmap must be mutable" }
    val canvas = Canvas(this)
    val paint = Paint().apply {
        color = maskColor
        xfermode = PorterDuffXfermode(SRC_ATOP)
    }
    canvas.drawRect(
        /* left = */ 0f,
        /* top = */ 0f,
        /* right = */ this.width.toFloat(),
        /* bottom = */ this.height.toFloat(),
        /* paint = */ paint
    )
}

/**
 * Rotate the current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testRotated
 */
internal fun AndroidBitmap.rotated(angle: Int): AndroidBitmap {
    val finalAngle = (angle % 360).let { if (it < 0) 360 + it else it }
    val inputBitmap = this
    val matrix = Matrix()
    matrix.setRotate(finalAngle.toFloat())
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
    var config = inputBitmap.safeConfig
    if (finalAngle % 90 != 0 && config == AndroidBitmapConfig.RGB_565) {
        config = AndroidBitmapConfig.ARGB_8888
    }
    val outBitmap = AndroidBitmap.createBitmap(
        /* width = */ newWidth,
        /* height = */ newHeight,
        /* config = */ config,
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
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testRoundedCornered
 */
internal fun AndroidBitmap.roundedCornered(radiusArray: FloatArray): AndroidBitmap {
    val inputBitmap = this
    val newBitmap = AndroidBitmap.createBitmap(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* config = */ inputBitmap.safeConfig,
    )
    val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    val canvas = Canvas(newBitmap).apply {
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
    return newBitmap
}

/**
 * Zoom current Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.util.AndroidBitmapsTest.testScaled
 */
internal fun AndroidBitmap.scaled(scaleFactor: Float): AndroidBitmap {
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newBitmap = AndroidBitmap.createBitmap(
        /* width = */ scaledWidth,
        /* height = */ scaledHeight,
        /* config = */ safeConfig,
    )
    val canvas = Canvas(newBitmap)
    val matrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
    }
    canvas.drawBitmap(this, matrix, null)
    return newBitmap
}