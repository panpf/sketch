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

package com.github.panpf.sketch

import com.github.panpf.sketch.util.Size

/**
 * Bitmap, which is a alias of platform Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testBitmapTypealias
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testBitmapTypealias
 */
expect class Bitmap

/**
 * ColorType, which is a alias of platform ColorType
 */
expect enum class ColorType

/**
 * Get the width of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testWidth
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testWidth
 */
expect val Bitmap.width: Int

/**
 * Get the height of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testHeight
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testHeight
 */
expect val Bitmap.height: Int

/**
 * Get image size
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testSize
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testSize
 */
val Bitmap.size: Size
    get() = Size(width, height)

/**
 * Returns the minimum number of bytes that can be used to store this bitmap's pixels.
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testByteCount
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testByteCount
 */
expect val Bitmap.byteCount: Long

/**
 * Returns true if the bitmap is mutable
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testIsMutable
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testIsMutable
 */
expect val Bitmap.isMutable: Boolean

/**
 * Returns true if the bitmap is immutable
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testIsImmutable
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testIsImmutable
 */
expect val Bitmap.isImmutable: Boolean