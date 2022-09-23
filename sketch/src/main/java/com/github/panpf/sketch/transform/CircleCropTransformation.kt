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
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.getOrCreate
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.SAME_ASPECT_RATIO
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.calculateResizeMapping
import com.github.panpf.sketch.util.safeConfig
import java.lang.Integer.min

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 *
 * If you're using Jetpack Compose, use `Modifier.clip(CircleShape)` instead of this transformation
 * as it's more efficient.
 *
 * @param scale Specify which part of the original image to keep. If null, use ImageRequest.resizeScaleDecider
 */
class CircleCropTransformation constructor(val scale: Scale? = null) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        val newSize = min(input.width, input.height)
        val scale = if (scale != null) {
            scale
        } else {
            val resize = request.resize
            val scaleDecider = resize?.scaleDecider ?: request.resizeScaleDecider
            scaleDecider.get(
                imageWidth = input.width,
                imageHeight = input.height,
                resizeWidth = newSize,
                resizeHeight = newSize
            )
        }
        val resizeMapping = calculateResizeMapping(
            input.width, input.height, newSize, newSize, SAME_ASPECT_RATIO, scale
        )
        val config = input.safeConfig
        val outBitmap = sketch.bitmapPool.getOrCreate(
            width = resizeMapping.newWidth,
            height = resizeMapping.newHeight,
            config = config,
            disallowReuseBitmap = request.disallowReuseBitmap,
            caller = "CircleCropTransformation"
        )
        val paint = Paint().apply {
            isAntiAlias = true
            color = -0x10000
        }
        val canvas = Canvas(outBitmap).apply {
            drawARGB(0, 0, 0, 0)
        }
        canvas.drawCircle(
            resizeMapping.newWidth / 2f,
            resizeMapping.newHeight / 2f,
            min(resizeMapping.newWidth, resizeMapping.newHeight) / 2f,
            paint
        )
        paint.xfermode = PorterDuffXfermode(SRC_IN)
        canvas.drawBitmap(input, resizeMapping.srcRect, resizeMapping.destRect, paint)
        return TransformResult(outBitmap, createCircleCropTransformed(scale))
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircleCropTransformation) return false

        if (scale != other.scale) return false

        return true
    }

    override fun hashCode(): Int {
        return scale.hashCode()
    }
}

fun createCircleCropTransformed(scale: Scale) =
    "CircleCropTransformed(${scale})"

fun isCircleCropTransformed(transformed: String): Boolean =
    transformed.startsWith("CircleCropTransformed(")

fun List<String>.getCircleCropTransformed(): String? =
    find { isCircleCropTransformed(it) }