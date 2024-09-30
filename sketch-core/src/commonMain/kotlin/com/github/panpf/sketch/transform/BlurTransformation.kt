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
import com.github.panpf.sketch.annotation.IntRange
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.background
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.hasAlphaPixels
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.mutableCopy

/**
 * Bitmap blur transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.BlurTransformationTest
 */
data class BlurTransformation constructor(
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
    override fun transform(requestContext: RequestContext, input: Image): TransformResult? {
        val inputBitmap = input.asOrNull<BitmapImage>()?.bitmap ?: return null
        val backgroundBitmap = if (hasAlphaBitmapBgColor != null && inputBitmap.hasAlphaPixels())
            inputBitmap.background(hasAlphaBitmapBgColor) else inputBitmap.mutableCopy()
        val blurBitmap = backgroundBitmap.blur(radius, firstReuseSelf = true)
        val maskBitmap = maskColor?.let { blurBitmap.mask(it, firstReuseSelf = true) } ?: blurBitmap
        val transformed = createBlurTransformed(radius, hasAlphaBitmapBgColor, maskColor)
        return TransformResult(image = maskBitmap.asImage(), transformed = transformed)
    }

    override fun toString(): String {
        return "BlurTransformation(radius=$radius, hasAlphaBitmapBgColor=$hasAlphaBitmapBgColor, maskColor=$maskColor)"
    }
}

/**
 * Create a blur transform record
 *
 * @see com.github.panpf.sketch.core.common.test.transform.BlurTransformationTest.testBlurTransformed
 */
fun createBlurTransformed(
    radius: Int, hasAlphaBitmapBgColor: Int?, maskColor: Int?
) = "BlurTransformed($radius,$hasAlphaBitmapBgColor,$maskColor)"

/**
 * Check if the transformed string is a blur transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.BlurTransformationTest.testBlurTransformed
 */
fun isBlurTransformed(transformed: String): Boolean =
    transformed.startsWith("BlurTransformed(")

/**
 * Get the blur transformation record from the transformed record list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.BlurTransformationTest.testBlurTransformed
 */
fun List<String>.getBlurTransformed(): String? =
    find { isBlurTransformed(it) }