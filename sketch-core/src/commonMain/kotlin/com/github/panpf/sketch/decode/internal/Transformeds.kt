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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.format

/**
 * Create a sample transform record
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testInSampledTransformed
 */
fun createInSampledTransformed(inSampleSize: Int): String = "InSampledTransformed($inSampleSize)"

/**
 * Returns true if the given transform record is a sample transform
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testInSampledTransformed
 */
fun isInSampledTransformed(transformed: String): Boolean =
    transformed.startsWith("InSampledTransformed(")

/**
 * Find the sample conversion record from the transform record list
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testInSampledTransformed
 */
fun List<String>.getInSampledTransformed(): String? =
    find { isInSampledTransformed(it) }


/**
 * Create a subsampling transform record
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testSubsamplingTransformed
 */
fun createSubsamplingTransformed(rect: Rect): String {
    return "SubsamplingTransformed(${rect.left},${rect.top},${rect.right},${rect.bottom})"
}

/**
 * Returns true if the given transform record is a subsampling transform
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testSubsamplingTransformed
 */
fun isSubsamplingTransformed(transformed: String): Boolean =
    transformed.startsWith("SubsamplingTransformed(")

/**
 * Find the subsampling conversion record from the transform record list
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testSubsamplingTransformed
 */
fun List<String>.getSubsamplingTransformed(): String? =
    find { isSubsamplingTransformed(it) }


/**
 * Create a resize transform record
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testResizeTransformed
 */
fun createResizeTransformed(resize: Resize): String =
    "ResizeTransformed(${resize.size},${resize.precision},${resize.scale})"

/**
 * Returns true if the given transform record is a resize transform
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testResizeTransformed
 */
fun isResizeTransformed(transformed: String): Boolean =
    transformed.startsWith("ResizeTransformed(")

/**
 * Find the resize conversion record from the transform record list
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testResizeTransformed
 */
fun List<String>.getResizeTransformed(): String? =
    find { isResizeTransformed(it) }


/**
 * Create a scale transform record
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testScaledTransformed
 */
fun createScaledTransformed(scale: Float): String =
    "ScaledTransformed(${scale.format(2)})"

/**
 * Returns true if the given transform record is a scale transform
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testScaledTransformed
 */
fun isScaledTransformed(transformed: String): Boolean =
    transformed.startsWith("ScaledTransformed(")

/**
 * Find the scale conversion record from the transform record list
 *
 * @see com.github.panpf.sketch.core.common.test.decode.internal.TransformedsTest.testScaledTransformed
 */
fun List<String>.getScaledTransformed(): String? =
    find { isScaledTransformed(it) }