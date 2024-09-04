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
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.util.rotate

/**
 * Bitmap Rotation Transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RotateTransformationTest
 */
class RotateTransformation(val degrees: Int) : Transformation {

    override val key: String = "RotateTransformation($degrees)"

    @WorkerThread
    override suspend fun transform(
        requestContext: RequestContext,
        input: Image
    ): TransformResult {
        val out = input.rotate(degrees)
        val transformed = createRotateTransformed(degrees)
        return TransformResult(image = out, transformed = transformed)
    }

    override fun toString(): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RotateTransformation
        if (degrees != other.degrees) return false
        return true
    }

    override fun hashCode(): Int {
        return degrees
    }
}

/**
 * Create a rotate crop transform record
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RotateTransformationTest.testRotateTransformed
 */
fun createRotateTransformed(degrees: Int) =
    "RotateTransformed($degrees)"

/**
 * Check whether the transformed string is a mask transformation
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RotateTransformationTest.testRotateTransformed
 */
fun isRotateTransformed(transformed: String): Boolean =
    transformed.startsWith("RotateTransformed(")

/**
 * Get the rotate transformation string from the list
 *
 * @see com.github.panpf.sketch.core.common.test.transform.RotateTransformationTest.testRotateTransformed
 */
fun List<String>.getRotateTransformed(): String? =
    find { isRotateTransformed(it) }