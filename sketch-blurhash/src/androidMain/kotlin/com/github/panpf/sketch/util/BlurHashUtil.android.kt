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

import android.graphics.ColorSpace
import android.os.Build
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.DecodeConfig

/**
 * Create a [Bitmap] for decoding BlurHash, the [ColorType] is [ColorType.ARGB_8888].
 *
 * @see com.github.panpf.sketch.blurhash.android.test.util.BlurHashUtilAndroidTest.testCreateBlurHashBitmap
 */
actual fun createBlurHashBitmap(width: Int, height: Int, decodeConfig: DecodeConfig?): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createBitmap(
            width = width,
            height = height,
            config = decodeConfig?.colorType ?: ColorType.ARGB_8888,
            hasAlpha = true,
            colorSpace = decodeConfig?.colorSpace ?: ColorSpace.get(ColorSpace.Named.SRGB)
        )
    } else {
        createBitmap(
            width = width,
            height = height,
            config = decodeConfig?.colorType ?: ColorType.ARGB_8888
        )
    }
}