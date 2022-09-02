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

import android.graphics.Rect
import com.github.panpf.sketch.resize.Resize

fun createInSampledTransformed(inSampleSize: Int): String = "InSampledTransformed($inSampleSize)"

fun List<String>.getInSampledTransformed(): String? =
    find { it.startsWith("InSampledTransformed(") }


fun createSubsamplingTransformed(rect: Rect): String {
    return "SubsamplingTransformed(${rect.left},${rect.top},${rect.right},${rect.bottom})"
}

fun List<String>.getSubsamplingTransformed(): String? =
    find { it.startsWith("SubsamplingTransformed(") }


fun createExifOrientationTransformed(exifOrientation: Int): String =
    "ExifOrientationTransformed(${exifOrientationName(exifOrientation)})"

fun List<String>.getExifOrientationTransformed(): String? =
    find { it.startsWith("ExifOrientationTransformed(") }


fun createResizeTransformed(resize: Resize): String =
    "ResizeTransformed(${resize.width}x${resize.height},${resize.precisionDecider.key},${resize.scaleDecider.key})"

fun List<String>.getResizeTransformed(): String? =
    find { it.startsWith("ResizeTransformed(") }