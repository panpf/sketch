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

import android.os.Build
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.internal.ImageFormat

/**
 * Build a [BitmapColorType] with the specified [colorType]
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testBitmapColorType
 */
fun BitmapColorType(colorType: ColorType): BitmapColorType = FixedColorType(colorType)

/**
 * Color type of packaging android platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testPlatformColorType
 */
actual data class PlatformColorType(actual val colorType: ColorType)

/**
 * Low quality bitmap config. RGB_565 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testLowQualityColorType
 */
actual data object LowQualityColorType : BitmapColorType {

    actual override val key: String = "LowQuality"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        return if (ImageFormat.parseMimeType(mimeType) == ImageFormat.JPEG || isOpaque) {
            PlatformColorType(ColorType.RGB_565)
        } else {
            null
        }
    }

    actual override fun toString(): String = "LowQualityColorType"
}

/**
 * High quality bitmap config. RGBA_F16 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testHighQualityColorType
 */
actual data object HighQualityColorType : BitmapColorType {

    actual override val key: String = "HighQuality"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PlatformColorType(ColorType.RGBA_F16)
        } else {
            null
        }
    }

    actual override fun toString(): String = "HighQualityColorType"
}

/**
 * Fixed bitmap config, whatever mimeTye is will return the specified config
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testFixedColorType
 */
actual data class FixedColorType actual constructor(val value: String) : BitmapColorType {

    constructor(colorType: ColorType) : this(colorType.name)

    actual override val key: String = "Fixed($value)"

    actual override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? {
        val config = ColorType.valueOf(this.value)
        return PlatformColorType(config)
    }

    actual override fun toString(): String = "FixedColorType($value)"
}