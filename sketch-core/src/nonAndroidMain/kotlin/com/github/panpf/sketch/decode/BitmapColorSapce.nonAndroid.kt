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

import org.jetbrains.skia.ColorSpace

/**
 * Create a [BitmapColorSpace] instance from the specified [colorSpace]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest.testBitmapColorSpace
 */
fun BitmapColorSpace(colorSpace: ColorSpace): BitmapColorSpace = FixedColorSpace(colorSpace)

/**
 * Color space of packaging skia platform
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest.testPlatformColorSpace
 */
actual data class PlatformColorSpace(val colorSpace: ColorSpace)

/**
 * Fixed bitmap space, whatever mimeTye is will return the specified config
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.BitmapColorSpaceNonAndroidTest.testFixedColorSpace
 */
actual data class FixedColorSpace actual constructor(val value: String) : BitmapColorSpace {

    override val key: String = "Fixed($value)"

    constructor(colorSpace: ColorSpace) : this(colorSpace.name())

    actual override fun getColorSpace(mimeType: String?, isOpaque: Boolean): PlatformColorSpace? {
        return PlatformColorSpace(ColorSpace.fromName(value))
    }

    override fun toString(): String = "FixedColorSpace($value)"
}