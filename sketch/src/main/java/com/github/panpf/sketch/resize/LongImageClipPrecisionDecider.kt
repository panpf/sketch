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

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
fun longImageClipPrecision(
    precision: Precision,
    longImageDecider: LongImageDecider = DefaultLongImageDecider()
): LongImageClipPrecisionDecider =
    LongImageClipPrecisionDecider(precision, longImageDecider)

/**
 * The long image uses the specified precision, use the '[Precision.LESS_PIXELS]' for others.
 *
 * Note: The precision parameter can only be [Precision.EXACTLY] or [Precision.SAME_ASPECT_RATIO].
 */
class LongImageClipPrecisionDecider constructor(
    val precision: Precision = Precision.SAME_ASPECT_RATIO,
    val longImageDecider: LongImageDecider = DefaultLongImageDecider(),
) : PrecisionDecider {

    init {
        require(precision == Precision.EXACTLY || precision == Precision.SAME_ASPECT_RATIO) {
            "precision must be EXACTLY or SAME_ASPECT_RATIO"
        }
    }

    override val key: String by lazy { "LongImageClip($precision,${longImageDecider.key})" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        val isLongImage = longImageDecider
            .isLongImage(imageWidth, imageHeight, resizeWidth, resizeHeight)
        return if (isLongImage) precision else Precision.LESS_PIXELS
    }

    override fun toString(): String {
        return "LongImageClipPrecisionDecider(precision=$precision, longImageDecider=$longImageDecider)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LongImageClipPrecisionDecider) return false
        if (precision != other.precision) return false
        if (longImageDecider != other.longImageDecider) return false
        return true
    }

    override fun hashCode(): Int {
        var result = precision.hashCode()
        result = 31 * result + longImageDecider.hashCode()
        return result
    }
}