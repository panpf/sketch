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

import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.FixedColorType
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.decode.PlatformColorType
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BitmapColorTypeNonAndroidTest {

    @Test
    fun testBitmapColorType() {
        assertEquals(
            expected = FixedColorType(ColorType.BGRA_8888.name),
            actual = BitmapColorType(ColorType.BGRA_8888)
        )
        assertEquals(
            expected = FixedColorType(ColorType.RGB_565.name),
            actual = BitmapColorType(ColorType.RGB_565)
        )
    }

    @Test
    fun testPlatformColorType() {
        val element1 = PlatformColorType(ColorType.RGBA_8888).apply {
            assertEquals(ColorType.RGBA_8888, colorType)
        }
        val element11 = PlatformColorType(ColorType.RGBA_8888)
        val element2 = PlatformColorType(ColorType.RGB_565).apply {
            assertEquals(ColorType.RGB_565, colorType)
        }

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testLowQualityColorType() {
        LowQualityColorType.apply {
            assertEquals("LowQuality", key)
            assertEquals("LowQualityColorType", toString())
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.ARGB_4444),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/webp", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/webp", isOpaque = true)
            )
            assertEquals(
                expected = null,
                actual = getColorType("image/gif", isOpaque = false)
            )
            assertEquals(
                expected = null,
                actual = getColorType("image/gif", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.ARGB_4444),
                actual = getColorType("image/bmp", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/bmp", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.ARGB_4444),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType(null, isOpaque = true)
            )
        }
    }

    @Test
    fun testHighQualityColorType() {
        HighQualityColorType.apply {
            assertEquals("HighQuality", key)
            assertEquals("HighQualityColorType", toString())
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_F16),
                actual = getColorType(null, isOpaque = true)
            )
        }
    }

    @Test
    fun testFixedColorType() {
        FixedColorType("RGBA_8888").apply {
            assertEquals("Fixed(RGBA_8888)", key)
            assertEquals("RGBA_8888", value)
            assertEquals("FixedColorType(RGBA_8888)", toString())
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGBA_8888),
                actual = getColorType(null, isOpaque = true)
            )
        }

        FixedColorType(ColorType.RGB_565).apply {
            assertEquals("Fixed(RGB_565)", key)
            assertEquals("RGB_565", value)
            assertEquals("FixedColorType(RGB_565)", toString())
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(ColorType.RGB_565),
                actual = getColorType(null, isOpaque = true)
            )
        }

        val element1 = FixedColorType(ColorType.RGBA_8888)
        val element11 = FixedColorType(ColorType.RGBA_8888)
        val element2 = FixedColorType(ColorType.ARGB_4444)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}