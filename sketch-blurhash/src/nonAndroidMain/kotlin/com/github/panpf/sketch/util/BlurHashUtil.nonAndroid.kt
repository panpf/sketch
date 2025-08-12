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

package com.github.panpf.sketch.util

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.createBitmap
import org.jetbrains.skia.ColorType

/**
 * Create a [Bitmap] for decoding BlurHash, the [ColorType] is [ColorType.RGBA_8888].
 *
 * @see com.github.panpf.sketch.blurhash.nonandroid.test.util.BlurHashUtilNonAndroidTest.testCreateBlurHashBitmap
 */
actual fun createBlurHashBitmap(width: Int, height: Int): Bitmap {
    return createBitmap(width, height, ColorType.RGBA_8888)
}