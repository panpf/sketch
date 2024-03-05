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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import java.lang.Integer.min

internal expect fun circleCropTransformation(image: Image, scale: Scale): Image?

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 *
 * If you're using Jetpack Compose, use `Modifier.clip(CircleShape)` instead of this transformation
 * as it's more efficient.
 *
 * @param scale Specify which part of the original image to keep. If null, use ImageRequest.scaleDecider
 */
class CircleCropTransformation(val scale: Scale? = null) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    @WorkerThread
    override suspend fun transform(
        sketch: Sketch,
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        val newSize = min(input.width, input.height)
        val scale = scale ?: requestContext.request.scaleDecider.get(
            imageSize = Size(input.width, input.height),
            targetSize = Size(newSize, newSize)
        )
        val out = circleCropTransformation(input, scale) ?: return null
        val transformed = createCircleCropTransformed(scale)
        return TransformResult(image = out, transformed = transformed)
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