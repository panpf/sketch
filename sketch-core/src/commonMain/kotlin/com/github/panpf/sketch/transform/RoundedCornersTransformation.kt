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

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.roundedCorners

/**
 * A [Transformation] that crops the image to fit the target's dimensions and rounds the corners of
 * the image.
 *
 * Please use it with 'precision(Precision.EXACTLY)' because if the original image size and resize size are inconsistent,
 * the final fillet will be scaled when displayed, resulting in inconsistency between the size of the fillet and the expectation
 *
 * If you're using Jetpack Compose, use `Modifier.clip(RoundedCornerShape(radius))` instead of this
 * transformation as it's more efficient.
 *
 * @param radiusArray Array of 8 values, 4 pairs of [X,Y] radii. The corners are ordered top-left, top-right, bottom-right, bottom-left
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RoundedCornersTransformationTest
 */
class RoundedCornersTransformation constructor(val radiusArray: FloatArray) : Transformation {

    /**
     * @param topLeft The radius for the top left corner.
     * @param topRight The radius for the top right corner.
     * @param bottomLeft The radius for the bottom left corner.
     * @param bottomRight The radius for the bottom right corner.
     */
    constructor(
        topLeft: Float = 0f,
        topRight: Float = 0f,
        bottomLeft: Float = 0f,
        bottomRight: Float = 0f,
    ) : this(
        floatArrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft, bottomLeft,
        )
    )

    constructor(allRadius: Float) : this(
        floatArrayOf(
            allRadius, allRadius,
            allRadius, allRadius,
            allRadius, allRadius,
            allRadius, allRadius
        )
    )

    init {
        require(radiusArray.size == 8) {
            "radiusArray size must be 8"
        }
        require(radiusArray.all { it >= 0f }) {
            "All radius must be >= 0"
        }
    }

    override val key: String =
        "RoundedCornersTransformation(${radiusArray.joinToString(separator = ",")})"

    @WorkerThread
    override suspend fun transform(
        requestContext: RequestContext,
        input: Image
    ): TransformResult {
        val out = input.roundedCorners(radiusArray)
        val transformed = createRoundedCornersTransformed(radiusArray)
        return TransformResult(image = out, transformed = transformed)
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RoundedCornersTransformation
        if (!radiusArray.contentEquals(other.radiusArray)) return false
        return true
    }

    override fun hashCode(): Int {
        return radiusArray.contentHashCode()
    }
}

/**
 * Create a rounded corners transform record
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RoundedCornersTransformationTest.testRoundedCornersTransformed
 */
fun createRoundedCornersTransformed(radiusArray: FloatArray) =
    "RoundedCornersTransformed(${radiusArray.contentToString()})"

/**
 * Check whether the transformed string is a rounded corners transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RoundedCornersTransformationTest.testRoundedCornersTransformed
 */
fun isRoundedCornersTransformed(transformed: String): Boolean =
    transformed.startsWith("RoundedCornersTransformed(")

/**
 * Get the rounded corners transformation record from the list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RoundedCornersTransformationTest.testRoundedCornersTransformed
 */
fun List<String>.getRoundedCornersTransformed(): String? =
    find { isRoundedCornersTransformed(it) }