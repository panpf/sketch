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
 * Create a [BitmapColorSpace] instance from the specified value
 */
fun BitmapColorSpace(value: String): BitmapColorSpace = FixedColorSpace(value)

/**
 * Bitmap color space, used to provide Bitmap color space configuration when decoding
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorSpaceAndroidTest
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest
 */
interface BitmapColorSpace : Key {

    /**
     * Get the color space based on the image type and whether it is opaque
     */
    fun getColorSpace(mimeType: String?, isOpaque: Boolean): PlatformColorSpace?
}

/**
 * Color space of packaging platform
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorSpaceAndroidTest.testPlatformColorSpace
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest.testPlatformColorSpace
 */
expect class PlatformColorSpace

/**
 * Fixed bitmap color space, whatever mimeTye is will return the specified config
 *
 * @see com.github.panpf.sketch.core.android.test.decode.BitmapColorSpaceAndroidTest.testFixedColorSpace
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest.testFixedColorSpace
 */
expect class FixedColorSpace(value: String) : BitmapColorSpace {

    override fun getColorSpace(mimeType: String?, isOpaque: Boolean): PlatformColorSpace?
}