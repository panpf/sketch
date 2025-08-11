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

import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.configOrNull

/**
 * Bitmap, which is a alias of [android.graphics.Bitmap]
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testBitmapTypealias
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


/**
 * Added an alias [ColorType] to Bitmap.Config
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testColorType
 */
actual typealias ColorType = android.graphics.Bitmap.Config

/**
 * Get the [ColorType] of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testColorType
 */
val Bitmap.colorType: ColorType?
    get() = configOrNull

/**
 * Image color type com.github.panpf.sketch.fetch.Blurhash2Util.decodeByte() decodes blurhash string in
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testBlurhashColorType
 */
actual val BLURHASH_COLOR_TYPE: ColorType = ColorType.ARGB_8888

/**
 * Create a new [Bitmap] with the specified width, height, and [ColorType] and allocate memory
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testCreateBitmap
 */
fun createBitmap(
    width: Int,
    height: Int,
    colorType: ColorType = ColorType.ARGB_8888
): Bitmap = Bitmap.createBitmap(width, height, colorType)

/**
 * Create a new [Bitmap] with the specified width, height, and [ColorType] and allocate memory
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testCreateBitmap
 */
@RequiresApi(Build.VERSION_CODES.O)
fun createBitmap(
    width: Int,
    height: Int,
    config: ColorType = ColorType.ARGB_8888,
    hasAlpha: Boolean,
    colorSpace: ColorSpace
): Bitmap = Bitmap.createBitmap(width, height, config, hasAlpha, colorSpace)

/**
 * Create a blank Bitmap based on the width, height, transparency, and color type of the original Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testCreateEmptyBitmapWith
 */
fun Bitmap.createEmptyBitmapWith(
    width: Int = this.width,
    height: Int = this.height,
    colorType: ColorType = this.colorType ?: ColorType.ARGB_8888,
    hasAlpha: Boolean = this.hasAlpha(),
): Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    Bitmap.createBitmap(
        /* width = */ width,
        /* height = */ height,
        /* config = */ colorType,
        /* hasAlpha = */ hasAlpha,
        /* colorSpace = */ this.colorSpace ?: ColorSpace.get(ColorSpace.Named.SRGB)
    )
} else {
    Bitmap.createBitmap(
        /* width = */ width,
        /* height = */ height,
        /* config = */ colorType,
    )
}

/**
 * Create a blank Bitmap based on the width, height, transparency, and color type of the original Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.BitmapAndroidTest.testCreateEmptyBitmapWith
 */
fun Bitmap.createEmptyBitmapWith(
    size: Size = this.size,
    colorType: ColorType = this.colorType ?: ColorType.ARGB_8888,
    hasAlpha: Boolean = this.hasAlpha(),
): Bitmap = createEmptyBitmapWith(size.width, size.height, colorType, hasAlpha)
