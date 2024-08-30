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

package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Key
import com.github.panpf.sketch.util.Size

/**
 * Creates a precision decider using the specified precision
 *
 * @see com.github.panpf.sketch.core.common.test.resize.PrecisionDeciderTest.testCreateFunction
 */
fun PrecisionDecider(precision: Precision): PrecisionDecider {
    return FixedPrecisionDecider(precision)
}

/**
 * Determines which precision to use dynamically based on image size and resizing
 *
 * IMPORTANT: It is necessary to ensure compliance with the consistency principle,
 * that is, the equals() and hashCode() methods of instances created with the same
 * construction parameters return consistent results. This is important in Compose
 */
interface PrecisionDecider : Key {

    fun get(imageSize: Size, targetSize: Size): Precision
}


/**
 * Always return specified precision
 *
 * @see com.github.panpf.sketch.core.common.test.resize.PrecisionDeciderTest
 */
data class FixedPrecisionDecider(private val precision: Precision) : PrecisionDecider {

    override val key: String by lazy { "Fixed($precision)" }

    override fun get(imageSize: Size, targetSize: Size): Precision {
        return precision
    }

    override fun toString(): String {
        return "FixedPrecisionDecider($precision)"
    }
}


/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 *
 * @see com.github.panpf.sketch.core.common.test.resize.PrecisionDeciderTest
 */
class LongImagePrecisionDecider constructor(
    val longImage: Precision = Precision.SAME_ASPECT_RATIO,
    val otherImage: Precision = Precision.LESS_PIXELS,
    val longImageDecider: LongImageDecider = LongImageDecider(),
) : PrecisionDecider {

    override val key: String by lazy { "LongImage($longImage,$otherImage,${longImageDecider.key})" }

    override fun get(imageSize: Size, targetSize: Size): Precision {
        val isLongImage = longImageDecider.isLongImage(imageSize, targetSize)
        return if (isLongImage) longImage else otherImage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as LongImagePrecisionDecider
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
        return "LongImagePrecisionDecider(longImage=$longImage, otherImage=$otherImage, longImageDecider=$longImageDecider)"
    }
}