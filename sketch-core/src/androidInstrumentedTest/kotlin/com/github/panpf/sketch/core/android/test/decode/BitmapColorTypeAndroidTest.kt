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

package com.github.panpf.sketch.core.android.test.decode

import android.graphics.Bitmap
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.FixedColorType
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.decode.PlatformColorType
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class BitmapColorTypeAndroidTest {

    @Test
    fun testBitmapColorType() {
        assertEquals(
            expected = FixedColorType(Bitmap.Config.ARGB_8888.name),
            actual = BitmapColorType(Bitmap.Config.ARGB_8888)
        )
        assertEquals(
            expected = FixedColorType(Bitmap.Config.RGB_565.name),
            actual = BitmapColorType(Bitmap.Config.RGB_565)
        )
    }

    @Test
    fun testPlatformColorType() {
        val element1 = PlatformColorType(Bitmap.Config.ARGB_8888).apply {
            assertEquals(Bitmap.Config.ARGB_8888, colorType)
        }
        val element11 = PlatformColorType(Bitmap.Config.ARGB_8888)
        val element2 = PlatformColorType(Bitmap.Config.RGB_565).apply {
            assertEquals(Bitmap.Config.RGB_565, colorType)
        }

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testLowQualityColorType() {
        LowQualityColorType.apply {
            assertEquals("LowQuality", key)
            assertEquals("LowQualityColorType", toString())
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = null,
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = null,
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType(null, isOpaque = true)
            )
        }
    }

    @Test
    fun testHighQualityColorType() {
        HighQualityColorType.apply {
            assertEquals("HighQuality", key)
            assertEquals("HighQualityColorType", toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType("image/jpeg", isOpaque = false)
                )
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType("image/jpeg", isOpaque = true)
                )
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType("image/png", isOpaque = false)
                )
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType("image/png", isOpaque = true)
                )
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType(null, isOpaque = false)
                )
                assertEquals(
                    expected = PlatformColorType(Bitmap.Config.RGBA_F16),
                    actual = getColorType(null, isOpaque = true)
                )
            } else {
                assertEquals(
                    expected = null,
                    actual = getColorType("image/jpeg", isOpaque = false)
                )
                assertEquals(
                    expected = null,
                    actual = getColorType("image/jpeg", isOpaque = true)
                )
                assertEquals(
                    expected = null,
                    actual = getColorType("image/png", isOpaque = false)
                )
                assertEquals(
                    expected = null,
                    actual = getColorType("image/png", isOpaque = true)
                )
                assertEquals(
                    expected = null,
                    actual = getColorType(null, isOpaque = false)
                )
                assertEquals(
                    expected = null,
                    actual = getColorType(null, isOpaque = true)
                )
            }
        }
    }

    @Test
    fun testFixedColorType() {
        FixedColorType("ARGB_4444").apply {
            assertEquals("Fixed(ARGB_4444)", key)
            assertEquals("ARGB_4444", value)
            assertEquals("FixedColorType(ARGB_4444)", toString())
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.ARGB_4444),
                actual = getColorType(null, isOpaque = true)
            )
        }

        FixedColorType(Bitmap.Config.RGB_565).apply {
            assertEquals("Fixed(RGB_565)", key)
            assertEquals("RGB_565", value)
            assertEquals("FixedColorType(RGB_565)", toString())
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorType(Bitmap.Config.RGB_565),
                actual = getColorType(null, isOpaque = true)
            )
        }

        val element1 = FixedColorType(Bitmap.Config.ARGB_8888)
        val element11 = FixedColorType(Bitmap.Config.ARGB_8888)
        val element2 = FixedColorType(Bitmap.Config.ARGB_4444)

        assertNotSame(element1, element11)
        assertNotSame(element1, element2)
        assertNotSame(element2, element11)

        assertEquals(element1, element1)
        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element2, element11)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element1.hashCode())
        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element2.hashCode(), element11.hashCode())
    }
}