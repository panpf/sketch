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

package com.github.panpf.sketch

import android.graphics.ColorSpace
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.panpf.sketch.util.configOrNull

/**
 * Android Bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapTest.testAndroidBitmap
 */
typealias AndroidBitmap = android.graphics.Bitmap

/**
 * Android Bitmap Config
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapTest.testColorType
 */
typealias ColorType = android.graphics.Bitmap.Config

/**
 * Get the [ColorType] of the bitmap
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapTest.testColorType
 */
val Bitmap.colorType: ColorType?
    get() = configOrNull

/**
 * Create a new [AndroidBitmap] with the specified width, height, and [ColorType] and allocate memory
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapTest.testAndroidBitmap
 */
fun AndroidBitmap(
    width: Int,
    height: Int,
    config: ColorType = ColorType.ARGB_8888
): AndroidBitmap = AndroidBitmap.createBitmap(width, height, config)

/**
 * Create a new [AndroidBitmap] with the specified width, height, and [ColorType] and allocate memory
 *
 * @see com.github.panpf.sketch.core.android.test.AndroidBitmapTest.testAndroidBitmap
 */
@RequiresApi(Build.VERSION_CODES.O)
fun AndroidBitmap(
    width: Int,
    height: Int,
    config: ColorType = ColorType.ARGB_8888,
    hasAlpha: Boolean,
    colorSpace: ColorSpace
): AndroidBitmap = AndroidBitmap.createBitmap(width, height, config, hasAlpha, colorSpace)