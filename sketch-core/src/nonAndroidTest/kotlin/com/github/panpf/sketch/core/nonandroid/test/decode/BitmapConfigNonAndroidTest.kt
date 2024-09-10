@file:Suppress("DEPRECATION")

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

package com.github.panpf.sketch.core.nonandroid.test.decode

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.BitmapConfig.FixedQuality
import com.github.panpf.sketch.decode.toSkiaColorType
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class BitmapConfigNonAndroidTest {

    @Test
    fun testBitmapConfig() {
        assertEquals(
            expected = FixedQuality(ColorType.BGRA_8888.name),
            actual = BitmapConfig(ColorType.BGRA_8888)
        )
        assertEquals(
            expected = FixedQuality(ColorType.RGB_565.name),
            actual = BitmapConfig(ColorType.RGB_565)
        )
    }

    @Test
    fun testToSkiaBitmapConfig() {
        BitmapConfig.HighQuality.apply {
            assertEquals(ColorType.RGBA_F16, toSkiaColorType("image/jpeg", isOpaque = false))
            assertEquals(ColorType.RGBA_F16, toSkiaColorType("image/jpeg", isOpaque = true))
            assertEquals(ColorType.RGBA_F16, toSkiaColorType("image/png", isOpaque = false))
            assertEquals(ColorType.RGBA_F16, toSkiaColorType("image/png", isOpaque = true))
            assertEquals(ColorType.RGBA_F16, toSkiaColorType(null, isOpaque = false))
            assertEquals(ColorType.RGBA_F16, toSkiaColorType(null, isOpaque = true))
        }

        BitmapConfig.LowQuality.apply {
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/jpeg", isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/jpeg", isOpaque = true))
            assertEquals(ColorType.ARGB_4444, toSkiaColorType("image/png", isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/png", isOpaque = true))
            assertEquals(ColorType.ARGB_4444, toSkiaColorType(null, isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType(null, isOpaque = true))
        }

        BitmapConfig(ColorType.RGB_565).apply {
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/jpeg", isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/jpeg", isOpaque = true))
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/png", isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType("image/png", isOpaque = true))
            assertEquals(ColorType.RGB_565, toSkiaColorType(null, isOpaque = false))
            assertEquals(ColorType.RGB_565, toSkiaColorType(null, isOpaque = true))
        }
    }
}