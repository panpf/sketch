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
package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.decode.ExifOrientation
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.format

fun createInSampledTransformed(inSampleSize: Int): String = "InSampledTransformed($inSampleSize)"

fun isInSampledTransformed(transformed: String): Boolean =
    transformed.startsWith("InSampledTransformed(")

fun List<String>.getInSampledTransformed(): String? =
    find { isInSampledTransformed(it) }


fun createSubsamplingTransformed(rect: Rect): String {
    return "SubsamplingTransformed(${rect.left},${rect.top},${rect.right},${rect.bottom})"
}

fun isSubsamplingTransformed(transformed: String): Boolean =
    transformed.startsWith("SubsamplingTransformed(")

fun List<String>.getSubsamplingTransformed(): String? =
    find { isSubsamplingTransformed(it) }


// TODO remove
fun createExifOrientationTransformed(exifOrientation: Int): String =
    "ExifOrientationTransformed(${ExifOrientation.name(exifOrientation)})"

// TODO remove
fun isExifOrientationTransformed(transformed: String): Boolean =
    transformed.startsWith("ExifOrientationTransformed(")

// TODO remove
fun List<String>.getExifOrientationTransformed(): String? =
    find { isExifOrientationTransformed(it) }


fun createResizeTransformed(resize: Resize): String =
    "ResizeTransformed(${resize.size},${resize.precision},${resize.scale})"

fun isResizeTransformed(transformed: String): Boolean =
    transformed.startsWith("ResizeTransformed(")

fun List<String>.getResizeTransformed(): String? =
    find { isResizeTransformed(it) }


fun createScaledTransformed(scale: Float): String =
    "ScaledTransformed(${scale.format(2)})"

fun isScaledTransformed(transformed: String): Boolean =
    transformed.startsWith("ScaledTransformed(")

fun List<String>.getScaledTransformed(): String? =
    find { isScaledTransformed(it) }