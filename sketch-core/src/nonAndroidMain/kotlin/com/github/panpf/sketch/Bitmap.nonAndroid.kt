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

/**
 * Bitmap, which is a typealias of [org.jetbrains.skia.Bitmap]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testBitmap
 */
actual typealias Bitmap = org.jetbrains.skia.Bitmap

/**
 * Get the width of the bitmap
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testWidth
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual val Bitmap.width: Int
    get() = this.width

/**
 * Get the height of the bitmap
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testHeight
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual val Bitmap.height: Int
    get() = this.height

/**
 * Returns the minimum number of bytes that can be used to store this bitmap's pixels.
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testByteCount
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testByteCount
 */
actual val Bitmap.byteCount: Long
    get() = (rowBytes * height).toLong()

/**
 * Returns the size of the allocated memory used to store this bitmap's pixels..
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testAllocationByteCount
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testAllocationByteCount
 */
actual val Bitmap.allocationByteCount: Long
    get() = (rowBytes * height).toLong()