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

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.circleCrop
import kotlin.math.min

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 *
 * If you're using Jetpack Compose, use `Modifier.clip(CircleShape)` instead of this transformation
 * as it's more efficient.
 *
 * @param scale Specify which part of the original image to keep. If null, use ImageRequest.scaleDecider
 * @see com.github.panpf.sketch.core.common.test.transform.CircleCropTransformationTest
 */
class CircleCropTransformation(val scale: Scale? = null) : Transformation {

    override val key: String = "CircleCropTransformation($scale)"

    @WorkerThread
    override fun transform(
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        val inputBitmap = input.asOrNull<BitmapImage>()?.bitmap ?: return null
        val newSize = min(input.width, input.height)
        val scale = scale ?: requestContext.request.scaleDecider.get(
            imageSize = Size(input.width, input.height),
            targetSize = Size(newSize, newSize)
        )
        val outImage = inputBitmap.circleCrop(scale)
        val transformed = createCircleCropTransformed(scale)
        return TransformResult(image = outImage.asImage(), transformed = transformed)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CircleCropTransformation
        if (scale != other.scale) return false
        return true
    }

    override fun hashCode(): Int {
        return scale.hashCode()
    }

    override fun toString(): String = "CircleCropTransformation($scale)"
}

/**
 * Create a circle crop transform record
 *
 * @see com.github.panpf.sketch.core.common.test.transform.CircleCropTransformationTest.testCircleCropTransformed
 */
fun createCircleCropTransformed(scale: Scale) =
    "CircleCropTransformed(${scale})"

/**
 * Check if the transformed string is a circle crop transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.CircleCropTransformationTest.testCircleCropTransformed
 */
fun isCircleCropTransformed(transformed: String): Boolean =
    transformed.startsWith("CircleCropTransformed(")

/**
 * Get the circle crop transformation record from the list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.CircleCropTransformationTest.testCircleCropTransformed
 */
fun List<String>.getCircleCropTransformed(): String? =
    find { isCircleCropTransformed(it) }