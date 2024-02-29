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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.resize.internal.ResizeMapping
import kotlin.math.ceil

internal val Bitmap.isImmutable: Boolean
    get() = !isMutable

internal val Bitmap.allocationByteCountCompat: Int
    get() = when {
        VERSION.SDK_INT >= VERSION_CODES.KITKAT -> this.allocationByteCount
        else -> this.byteCount
    }

@Suppress("USELESS_ELVIS")
internal val Bitmap.safeConfig: Bitmap.Config
    get() = config ?: Bitmap.Config.ARGB_8888

@Suppress("USELESS_ELVIS")
internal val Bitmap.configOrNull: Bitmap.Config?
    get() = config ?: null

internal fun Bitmap.toInfoString(): String =
    "Bitmap(width=${width}, height=${height}, config=$configOrNull)"

internal fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$configOrNull)"

/**
 * Gets the number of bytes occupied by a single pixel in a specified configuration
 */
internal fun Bitmap.Config?.getBytesPerPixel(): Int {
    // A bitmap by decoding a gif has null "config" in certain environments.
    val config = this ?: Bitmap.Config.ARGB_8888
    @Suppress("DEPRECATION")
    return when {
        config == Bitmap.Config.ALPHA_8 -> 1
        config == Bitmap.Config.RGB_565 || config == Bitmap.Config.ARGB_4444 -> 2
        config == Bitmap.Config.ARGB_8888 -> 4
        VERSION.SDK_INT >= VERSION_CODES.O && config == Bitmap.Config.RGBA_F16 -> 8
        else -> 4
    }
}

/**
 * The number of bytes required for calculation based on width, height, and configuration
 */
fun calculateBitmapByteCount(width: Int, height: Int, config: Bitmap.Config?): Int {
    return width * height * config.getBytesPerPixel()
}

internal fun Bitmap.Config.isAndSupportHardware(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.O && this == Bitmap.Config.HARDWARE

internal fun Bitmap.scale(scaleFactor: Float): Bitmap {
    val config = this.safeConfig
    val scaledWidth = ceil(width * scaleFactor).toInt()
    val scaledHeight = ceil(height * scaleFactor).toInt()
    val newBitmap = Bitmap.createBitmap(
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

internal fun Bitmap.mapping(mapping: ResizeMapping): Bitmap {
    val inputBitmap = this
    val config = inputBitmap.safeConfig
    val outBitmap = Bitmap.createBitmap(
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

internal fun Bitmap.backgrounded(backgroundColor: Int): Bitmap {
    val inputBitmap = this
    val bitmap = Bitmap.createBitmap(
        /* width = */ inputBitmap.width,
        /* height = */ inputBitmap.height,
        /* config = */ inputBitmap.safeConfig,
    )
    val canvas = Canvas(bitmap)
    canvas.drawColor(backgroundColor)
    canvas.drawBitmap(inputBitmap, 0f, 0f, null)
    return bitmap
}

internal fun Bitmap.mask(maskColor: Int) {
    require(isMutable) { "Bitmap is immutable" }
    val inputBitmap = this
    val canvas = Canvas(inputBitmap)
    val paint = Paint()
    paint.color = maskColor
    paint.xfermode = null
    val saveCount = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        canvas.saveLayer(
            /* left = */ 0f,
            /* top = */ 0f,
            /* right = */ inputBitmap.width.toFloat(),
            /* bottom = */ inputBitmap.height.toFloat(),
            /* paint = */ paint
        )
    } else {
        @Suppress("DEPRECATION")
        canvas.saveLayer(
            /* left = */ 0f,
            /* top = */ 0f,
            /* right = */ inputBitmap.width.toFloat(),
            /* bottom = */ inputBitmap.height.toFloat(),
            /* paint = */ paint,
            /* saveFlags = */ Canvas.ALL_SAVE_FLAG
        )
    }
    canvas.drawBitmap(inputBitmap, 0f, 0f, null)
    paint.xfermode = PorterDuffXfermode(SRC_IN)
    canvas.drawRect(0f, 0f, inputBitmap.width.toFloat(), inputBitmap.height.toFloat(), paint)
    canvas.restoreToCount(saveCount)
}

internal fun Bitmap.blur(radius: Int): Bitmap {
    val inBitmap = this
    val outBitmap: Bitmap = if (inBitmap.isMutable) {
        inBitmap
    } else {
        inBitmap.copy(inBitmap.safeConfig, true)
    }
    val width = outBitmap.width
    val height = outBitmap.height
    val pixels = IntArray(width * height)
    outBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    fastGaussianBlur(pixels = pixels, width = width, height = height, radius = radius)
    outBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return outBitmap
}