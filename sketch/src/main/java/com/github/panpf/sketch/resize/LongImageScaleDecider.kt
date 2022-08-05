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

fun longImageScale(
    longImage: Scale,
    other: Scale,
    longImageDecider: LongImageDecider = DefaultLongImageDecider()
): LongImageScaleDecider = LongImageScaleDecider(longImage, other, longImageDecider)

class LongImageScaleDecider constructor(
    val longImage: Scale,
    val otherImage: Scale,
    val longImageDecider: LongImageDecider = DefaultLongImageDecider(),
) : ScaleDecider {

    override fun addExifOrientation(
        exifOrientationHelper: ExifOrientationHelper,
        imageSize: Size
    ): LongImageScaleDecider = LongImageScaleDecider(
        longImage = exifOrientationHelper.addToScale(longImage, imageSize),
        otherImage = exifOrientationHelper.addToScale(otherImage, imageSize),
        longImageDecider = longImageDecider,
    )

    override val key: String by lazy { "LongImage($longImage,$otherImage),${longImageDecider.key})" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Scale {
        val isLongImage = longImageDecider
            .isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight)
        return if (isLongImage) longImage else otherImage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageScaleDecider) return false
        if (longImage != other.longImage) return false
        if (otherImage != other.otherImage) return false
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        var result = longImage.hashCode()
        result = 31 * result + otherImage.hashCode()
        result = 31 * result + longImageDecider.hashCode()
        return result
    }

    override fun toString(): String {
        return "LongImageScaleDecider(longImage=$longImage, otherImage=$otherImage, longImageDecider=$longImageDecider)"
    }
}