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
 * Bitmap, which is a typealias of [android.graphics.Bitmap]
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testBitmap
 */
actual typealias Bitmap = android.graphics.Bitmap

/**
 * Get the width of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testWidth
 */
@Suppress("ConflictingExtensionProperty")
actual val Bitmap.width: Int
    get() = this.width

/**
 * Get the height of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testHeight
 */
@Suppress("ConflictingExtensionProperty")
actual val Bitmap.height: Int
    get() = this.height

/**
 * Returns the minimum number of bytes that can be used to store this bitmap's pixels.
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testByteCount
 */
@Suppress("ConflictingExtensionProperty")
actual val Bitmap.byteCount: Long
    get() = this.byteCount.toLong()

/**
 * Returns true if the bitmap is mutable
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testIsMutable
 */
@Suppress("ConflictingExtensionProperty")
actual val Bitmap.isMutable: Boolean
    get() = this.isMutable

/**
 * Returns true if the bitmap is immutable
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testIsImmutable
 */
actual val Bitmap.isImmutable: Boolean
    get() = !this.isMutable