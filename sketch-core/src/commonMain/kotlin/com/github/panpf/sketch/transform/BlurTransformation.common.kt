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

package com.github.panpf.sketch.transform

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.IntRange
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext

internal expect fun blurTransformation(
    image: Image,
    radius: Int,
    hasAlphaBitmapBgColor: Int?,
    maskColor: Int?
): Image

/**
 * Bitmap blur transformation
 */
class BlurTransformation constructor(
    /** Blur radius */
    @IntRange(from = 0, to = 100)
    val radius: Int = 15,

    /** If the Bitmap has transparent pixels, it will force the Bitmap to add an opaque background color and then blur it */
    val hasAlphaBitmapBgColor: Int? = 0xFF000000L.toInt(),

    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    val maskColor: Int? = null,
) : Transformation {

    init {
        require(radius in 1..100) {
            "Radius must range from 1 to 100: $radius"
        }
        require(hasAlphaBitmapBgColor == null || hasAlphaBitmapBgColor ushr 24 == 255) {
            "hasAlphaBitmapBgColor must be not transparent"
        }
    }

    override val key: String = "BlurTransformation(${radius},$hasAlphaBitmapBgColor,$maskColor)"

    @WorkerThread
    override suspend fun transform(
        sketch: Sketch,
        requestContext: RequestContext,
        input: Image
    ): TransformResult {
        val out = blurTransformation(input, radius, hasAlphaBitmapBgColor, maskColor)
        val transformed = createBlurTransformed(radius, hasAlphaBitmapBgColor, maskColor)
        return TransformResult(image = out, transformed = transformed)
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BlurTransformation
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

fun isBlurTransformed(transformed: String): Boolean =
    transformed.startsWith("BlurTransformed(")

fun List<String>.getBlurTransformed(): String? =
    find { isBlurTransformed(it) }