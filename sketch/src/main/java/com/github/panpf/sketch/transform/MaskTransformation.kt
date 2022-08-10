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
package com.github.panpf.sketch.transform

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffXfermode
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.ColorInt
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.safeConfig

class MaskTransformation(
    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    @ColorInt
    val maskColor: Int
) : Transformation {

    override val key: String = "MaskTransformation($maskColor)"

    override fun toString(): String = key

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val bitmapPool: BitmapPool = sketch.bitmapPool

        val maskBitmap: Bitmap
        var isNewBitmap = false
        if (input.isMutable) {
            maskBitmap = input
        } else {
            maskBitmap = bitmapPool.getOrCreate(input.width, input.height, input.safeConfig)
            isNewBitmap = true
        }

        val canvas = Canvas(maskBitmap)

        if (isNewBitmap) {
            canvas.drawBitmap(input, 0f, 0f, null)
        }

        val paint = Paint()
        paint.color = maskColor
        paint.xfermode = null

        val saveCount = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(
                0f, 0f, input.width.toFloat(), input.height.toFloat(), paint
            )
        } else {
            @Suppress("DEPRECATION")
            canvas.saveLayer(
                0f, 0f, input.width.toFloat(), input.height.toFloat(), paint, Canvas.ALL_SAVE_FLAG
            )
        }

        canvas.drawBitmap(input, 0f, 0f, null)

        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawRect(0f, 0f, input.width.toFloat(), input.height.toFloat(), paint)

        canvas.restoreToCount(saveCount)

        return TransformResult(maskBitmap, createMaskTransformed(maskColor))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MaskTransformation) return false

        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        return maskColor
    }
}

fun createMaskTransformed(@ColorInt maskColor: Int) =
    "MaskTransformed(${maskColor})"

fun List<String>.getMaskTransformed(): String? =
    find { it.startsWith("MaskTransformed(") }