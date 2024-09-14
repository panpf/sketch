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

import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorType

/**
 * Skia Bitmap
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapTest.testSkiaBitmap
 */
typealias SkiaBitmap = org.jetbrains.skia.Bitmap

/**
 * Create a new [SkiaBitmap] with the specified [SkiaImageInfo] and allocate memory
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapTest.testSkiaBitmap
 */
fun SkiaBitmap(imageInfo: SkiaImageInfo): SkiaBitmap = SkiaBitmap()
    .apply { allocPixels(imageInfo) }

/**
 * Create a new [SkiaBitmap] with the specified width, height, and [ColorInfo] and allocate memory
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.SkiaBitmapTest.testSkiaBitmap
 */
fun SkiaBitmap(
    width: Int,
    height: Int,
    colorType: ColorType = ColorType.N32,
    alphaType: ColorAlphaType = ColorAlphaType.PREMUL
): SkiaBitmap = SkiaBitmap()
    .apply { allocPixels(SkiaImageInfo(width, height, colorType, alphaType)) }