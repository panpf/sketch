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

import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/**
 * Bitmap, which is a alias of [org.jetbrains.skia.Bitmap]
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testBitmapTypealias
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
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testByteCount
 */
actual val Bitmap.byteCount: Long
    get() = (rowBytes * height).toLong()

/**
 * Returns true if the bitmap is mutable
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testIsMutable
 */
actual val Bitmap.isMutable: Boolean
    get() = !this.isImmutable

/**
 * Returns true if the bitmap is immutable
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testIsImmutable
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual val Bitmap.isImmutable: Boolean
    get() = this.isImmutable

/**
 * Create a new [Bitmap] with the specified [ImageInfo] and allocate memory
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testCreateBitmap
 */
fun createBitmap(imageInfo: ImageInfo): Bitmap = Bitmap()
    .apply { allocPixels(imageInfo) }

/**
 * Create a new [Bitmap] with the specified width, height, and [ColorInfo] and allocate memory
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.BitmapNonAndroidTest.testCreateBitmap
 */
fun createBitmap(
    width: Int,
    height: Int,
    colorType: ColorType = ColorType.N32,
    alphaType: ColorAlphaType = ColorAlphaType.PREMUL,
    colorSpace: ColorSpace = ColorSpace.sRGB,
): Bitmap = Bitmap()
    .apply { allocPixels(ImageInfo(width, height, colorType, alphaType, colorSpace)) }