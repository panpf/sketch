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
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.internal.freeBitmap
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.fastGaussianBlur
import com.github.panpf.sketch.util.safeConfig

class BlurTransformation constructor(
    /** Blur radius */
    @IntRange(from = 0, to = 100)
    val radius: Int = 15,

    /** If the Bitmap has transparent pixels, it will force the Bitmap to add an opaque background color and then blur it */
    @ColorInt
    val hasAlphaBitmapBgColor: Int? = Color.BLACK,

    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    @ColorInt
    val maskColor: Int? = null,
) : Transformation {

    init {
        require(radius in 1..100) {
            "Radius must range from 1 to 100: $radius"
        }
        require(hasAlphaBitmapBgColor == null || Color.alpha(hasAlphaBitmapBgColor) == 255) {
            "hasAlphaBitmapBgColor must be not transparent"
        }
    }

    override val key: String =
        "BlurTransformation(${radius},$hasAlphaBitmapBgColor,$maskColor)"

    override suspend fun transform(
        sketch: Sketch,
        request: ImageRequest,
        input: Bitmap
    ): TransformResult {
        // Transparent pixels cannot be blurred
        val compatAlphaBitmap = if (hasAlphaBitmapBgColor != null && input.hasAlpha()) {
            val bitmap = sketch.bitmapPool.getOrCreate(input.width, input.height, input.safeConfig)
            val canvas = Canvas(bitmap)
            canvas.drawColor(hasAlphaBitmapBgColor)
            canvas.drawBitmap(input, 0f, 0f, null)
            bitmap
        } else {
            input
        }
        if (compatAlphaBitmap !== input) {
            freeBitmap(sketch.bitmapPool, sketch.logger, input, "blurCompatAlpha")
        }

        val outBitmap = fastGaussianBlur(compatAlphaBitmap, radius)
        maskColor?.let {
            Canvas(outBitmap).drawColor(it)
        }
        return TransformResult(
            outBitmap,
            createBlurTransformed(radius, hasAlphaBitmapBgColor, maskColor),
        )
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlurTransformation) return false

        if (radius != other.radius) return false
        if (hasAlphaBitmapBgColor != other.hasAlphaBitmapBgColor) return false
        if (maskColor != other.maskColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = radius
        result = 31 * result + (hasAlphaBitmapBgColor ?: 0)
        result = 31 * result + (maskColor ?: 0)
        return result
    }
}

fun createBlurTransformed(
    radius: Int, hasAlphaBitmapBgColor: Int?, maskColor: Int?
) = "BlurTransformed($radius,$hasAlphaBitmapBgColor,$maskColor)"

fun List<String>.getBlurTransformed(): String? =
    find { it.startsWith("BlurTransformed(") }