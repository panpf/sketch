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

@file:Suppress("RedundantConstructorKeyword")

package com.github.panpf.sketch.transform

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.mask

/**
 * Bitmap mask transformation, which attaches a layer of color to the surface of the bitmap, usually used to darken the bitmap used as the background
 *
 * @see com.github.panpf.sketch.core.common.test.transform.MaskTransformationTest
 */
class MaskTransformation constructor(
    /** Overlay the blurred image with a layer of color, often useful when using images as a background */
    val maskColor: Int
) : Transformation {

    override val key: String = "MaskTransformation($maskColor)"

    @WorkerThread
    override fun transform(
        requestContext: RequestContext,
        input: Image
    ): TransformResult? {
        val inputBitmap = input.asOrNull<BitmapImage>()?.bitmap ?: return null
        val outBitmap = inputBitmap.mask(maskColor, firstReuseSelf = false)
        val transformed = createMaskTransformed(maskColor)
        return TransformResult(image = outBitmap.asImage(), transformed = transformed)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MaskTransformation
        if (maskColor != other.maskColor) return false
        return true
    }

    override fun hashCode(): Int {
        return maskColor
    }

    override fun toString(): String = "MaskTransformation($maskColor)"
}

/**
 * Create a mask transform record
 *
 * @see com.github.panpf.sketch.core.common.test.transform.MaskTransformationTest.testMaskTransformed
 */
fun createMaskTransformed(maskColor: Int) = "MaskTransformed(${maskColor})"

/**
 * Check whether the transformed string is a mask transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.MaskTransformationTest.testMaskTransformed
 */
fun isMaskTransformed(transformed: String): Boolean =
    transformed.startsWith("MaskTransformed(")

/**
 * Get the mask transformation record from the list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.MaskTransformationTest.testMaskTransformed
 */
fun List<String>.getMaskTransformed(): String? =
    find { isMaskTransformed(it) }