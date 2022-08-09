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
package com.github.panpf.sketch.resize

import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.util.Size

fun fixedScale(precision: Scale): FixedScaleDecider = FixedScaleDecider(precision)

/**
 * Always return specified precision
 */
data class FixedScaleDecider(private val scale: Scale) : ScaleDecider {

    override val key: String by lazy { "Fixed($scale)" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        return scale
    }

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): FixedScaleDecider {
        return FixedScaleDecider(exifOrientationHelper.addToScale(scale, imageSize))
    }

    override fun toString(): String {
        return "FixedScaleDecider($scale)"
    }
}