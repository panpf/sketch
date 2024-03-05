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

import androidx.annotation.ColorInt
import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestContext

internal expect fun maskTransformation(image: Image, maskColor: Int): Image?

/**
 * Bitmap mask transformation, which attaches a layer of color to the surface of the bitmap, usually used to darken the bitmap used as the background
 */
class MaskTransformation(
    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    @ColorInt
    val maskColor: Int
) : Transformation {

    override val key: String = "MaskTransformation($maskColor)"

    override fun toString(): String = key

    @WorkerThread
    override suspend fun transform(
        sketch: Sketch,
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        val out = maskTransformation(input, maskColor) ?: return null
        val transformed = createMaskTransformed(maskColor)
        return TransformResult(image = out, transformed = transformed)
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

fun isMaskTransformed(transformed: String): Boolean =
    transformed.startsWith("MaskTransformed(")

fun List<String>.getMaskTransformed(): String? =
    find { isMaskTransformed(it) }