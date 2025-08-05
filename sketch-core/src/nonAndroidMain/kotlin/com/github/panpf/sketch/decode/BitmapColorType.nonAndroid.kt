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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.github.panpf.sketch.decode

import com.github.panpf.sketch.decode.internal.ImageFormat
import org.jetbrains.skia.ColorType

/**
 * Build a [BitmapColorType] with the specified [colorType]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testBitmapColorType
 */
fun BitmapColorType(colorType: ColorType): BitmapColorType = FixedColorType(colorType)

/**
 * Color type of packaging skia platform
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testPlatformColorType
 */
actual data class PlatformColorType(actual val colorType: ColorType)

/**
 * Low quality bitmap config. RGB_565 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testLowQualityColorType
 */
actual data object LowQualityColorType : BitmapColorType {

    actual override val key: String = "LowQuality"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        val imageFormat = ImageFormat.parseMimeType(mimeType)
        val colorType = when {
            imageFormat == ImageFormat.JPEG -> ColorType.RGB_565
            imageFormat == ImageFormat.WEBP -> ColorType.RGB_565
            imageFormat == ImageFormat.GIF -> null
            isOpaque -> ColorType.RGB_565
            else -> ColorType.ARGB_4444
        } ?: return null
        return PlatformColorType(colorType)
    }

    actual override fun toString(): String = "LowQualityColorType"
}

/**
 * High quality bitmap config. RGBA_F16 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testHighQualityColorType
 */
actual data object HighQualityColorType : BitmapColorType {

    actual override val key: String = "HighQuality"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        return PlatformColorType(ColorType.RGBA_F16)
    }

    actual override fun toString(): String = "HighQualityColorType"
}

/**
 * Fixed bitmap ColorType, whatever mimeTye is will return the specified config
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testFixedColorType
 */
actual data class FixedColorType actual constructor(val value: String) : BitmapColorType {

    constructor(colorType: ColorType) : this(colorType.name)

    actual override val key: String = "Fixed($value)"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        val colorType = ColorType.valueOf(this.value)
        return PlatformColorType(colorType)
    }

    actual override fun toString(): String = "FixedColorType($value)"
}