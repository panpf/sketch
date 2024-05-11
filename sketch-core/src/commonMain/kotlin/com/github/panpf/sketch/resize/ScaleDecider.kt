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

import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.util.Size

fun ScaleDecider(scale: Scale): ScaleDecider {
    return FixedScaleDecider(scale)
}

/**
 * Determines which scale to use dynamically based on image size and resizing
 * 
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface ScaleDecider {

    val key: String    // TODO Implement the Key interface

    fun get(imageSize: Size, targetSize: Size): Scale
}

/**
 * Always return specified precision
 */
data class FixedScaleDecider(private val scale: Scale) : ScaleDecider {

    override val key: String by lazy { "Fixed($scale)" }

    override fun get(imageSize: Size, targetSize: Size): Scale {
        return scale
    }

    override fun toString(): String {
        return "FixedScaleDecider($scale)"
    }
}


/**
 * Use different Scales for long and non-long images
 */
class LongImageStartCropScaleDecider constructor(
    val longImage: Scale = START_CROP,
    val otherImage: Scale = CENTER_CROP,
    val longImageDecider: LongImageDecider = LongImageDecider(),
) : ScaleDecider {

    override val key: String by lazy { "LongImage($longImage,$otherImage,${longImageDecider.key})" }

    override fun get(imageSize: Size, targetSize: Size): Scale {
        val isLongImage = longImageDecider.isLongImage(imageSize, targetSize)
        return if (isLongImage) longImage else otherImage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageStartCropScaleDecider) return false
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