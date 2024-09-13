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

import com.github.panpf.sketch.util.Key

/**
 * Create a [BitmapColorType] instance from the specified value
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapColorTypeTest.testBitmapColorType
 */
fun BitmapColorType(value: String): BitmapColorType = FixedColorType(value)

/**
 * Bitmap color type, used to provide Bitmap color type configuration when decoding
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest
 */
interface BitmapColorType : Key {

    /**
     * Get the color type based on the image type and whether it is opaque
     */
    fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType?
}

/**
 * Color type of packaging platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testPlatformColorType
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testPlatformColorType
 */
expect class PlatformColorType

/**
 * Low quality bitmap color type. RGB_565 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testLowQualityColorType
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testLowQualityColorType
 */
expect object LowQualityColorType : BitmapColorType {

    override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType?
}

/**
 * High quality bitmap color type. RGBA_F16 is preferred, followed by ARGB_8888
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testHighQualityColorType
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testHighQualityColorType
 */
expect object HighQualityColorType : BitmapColorType {

    override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType?
}

/**
 * Fixed bitmap color type, whatever mimeTye is will return the specified config
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorTypeAndroidTest.testFixedColorType
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorTypeNonAndroidTest.testFixedColorType
 */
expect class FixedColorType(value: String) : BitmapColorType {

    override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType?
}

/**
 * Whether configured for low-quality bitmaps
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapColorTypeTest.testIsLowQuality
 */
val BitmapColorType.isLowQuality: Boolean
    get() = this === LowQualityColorType

/**
 * Whether configured for high-quality bitmaps
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapColorTypeTest.testIsHighQuality
 */
val BitmapColorType.isHighQuality: Boolean
    get() = this === HighQualityColorType

/**
 * Whether configured for fixed bitmaps
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapColorTypeTest.testIsFixed
 */
val BitmapColorType.isFixed: Boolean
    get() = this is FixedColorType

/**
 * Whether configured for dynamic bitmaps
 *
 * @see com.github.panpf.sketch.core.common.test.decode.BitmapColorTypeTest.testIsDynamic
 */
val BitmapColorType.isDynamic: Boolean
    get() = this !is FixedColorType