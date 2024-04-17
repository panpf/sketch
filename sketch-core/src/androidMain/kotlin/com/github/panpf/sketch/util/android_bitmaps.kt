/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.internal.ResizeMapping
import com.github.panpf.sketch.resize.internal.calculateResizeMapping
import kotlin.math.ceil
import kotlin.math.min

@Suppress("ObsoleteSdkInt")
internal val AndroidBitmap.allocationByteCountCompat: Int
    get() = when {
        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> this.allocationByteCount
        else -> this.byteCount
    }

@Suppress("USELESS_ELVIS")
internal val AndroidBitmap.configOrNull: AndroidBitmapConfig?
    get() = config ?: null

internal val AndroidBitmap.isImmutable: Boolean
    get() = !isMutable

@Suppress("USELESS_ELVIS")
internal val AndroidBitmap.safeConfig: AndroidBitmapConfig
    get() = config ?: AndroidBitmapConfig.ARGB_8888

internal fun AndroidBitmap.getMutableCopy(): AndroidBitmap {
    return if (isMutable) this else copy(safeConfig, true)
}

internal fun AndroidBitmap.toInfoString(): String =
    "AndroidBitmap(width=${width}, height=${height}, config=$configOrNull)"

internal fun AndroidBitmap.toLogString(): String =
    "AndroidBitmap@${toHexString()}(${width}x${height},$configOrNull)"

internal fun AndroidBitmap.toShortInfoString(): String =
    "AndroidBitmap(${width}x${height},$configOrNull)"


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

internal fun AndroidBitmap.circleCropped(scale: Scale): AndroidBitmap {
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
    val config = inputBitmap.safeConfig
    val outBitmap = AndroidBitmap.createBitmap(
        /* width = */ resizeMapping.newWidth,
        /* height = */ resizeMapping.newHeight,
        /* config = */ config,
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
        /* dst = */ resizeMapping.destRect.toAndroidRect(),
        /* paint = */ paint
    )
    return outBitmap
}

internal fun AndroidBitmap.mapping(mapping: ResizeMapping): AndroidBitmap {
    val inputBitmap = this
    val config = inputBitmap.safeConfig
    val outBitmap = AndroidBitmap.createBitmap(
        /* width = */ mapping.newWidth,
        /* height = */ mapping.newHeight,
        /* config = */ config,
    )
    Canvas(outBitmap).drawBitmap(
        /* bitmap = */ inputBitmap,
        /* src = */ mapping.srcRect.toAndroidRect(),
        /* dst = */ mapping.destRect.toAndroidRect(),
        /* paint = */ null
    )
    return outBitmap
}

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
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 */
internal fun AndroidBitmap.roundedCornered(radiusArray: FloatArray): AndroidBitmap {
    val inputBitmap = this
    val config = inputBitmap.safeConfig
    val newBitmap = AndroidBitmap.createBitmap(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* config = */ config,
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
    if (finalAngle % 90 != 0 && config != AndroidBitmapConfig.ARGB_8888) {
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

internal fun AndroidBitmap.scaled(scaleFactor: Float): AndroidBitmap {
    val config = this.safeConfig
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newBitmap = AndroidBitmap.createBitmap(
        /* width = */ scaledWidth,
        /* height = */ scaledHeight,
        /* config = */ config,
    )
    val canvas = Canvas(newBitmap)
    val matrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
    }
    canvas.drawBitmap(this, matrix, null)
    return newBitmap
}


internal fun AndroidBitmapConfig.isAndSupportHardware(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.O && this == AndroidBitmapConfig.HARDWARE

/**
 * Gets the number of bytes occupied by a single pixel in a specified configuration
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
 */
internal fun calculateBitmapByteCount(width: Int, height: Int, config: AndroidBitmapConfig?): Int {
    return width * height * config.getBytesPerPixel()
}