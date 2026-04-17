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

import android.graphics.ColorSpace
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.FixedColorSpace
import com.github.panpf.sketch.decode.PlatformColorSpace
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class BitmapColorSpaceAndroidTest {

    @Test
    fun testBitmapColorSpace() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        assertEquals(
            expected = FixedColorSpace(ColorSpace.Named.LINEAR_SRGB.name),
            actual = BitmapColorSpace(ColorSpace.Named.LINEAR_SRGB)
        )
        assertEquals(
            expected = FixedColorSpace(ColorSpace.Named.DISPLAY_P3.name),
            actual = BitmapColorSpace(ColorSpace.Named.DISPLAY_P3)
        )
    }

    @Test
    fun testPlatformColorSpace() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val element1 = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB).apply {
            assertEquals(ColorSpace.get(ColorSpace.Named.LINEAR_SRGB), colorSpace)
        }
        val element11 = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB)
        val element2 = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3).apply {
            assertEquals(ColorSpace.get(ColorSpace.Named.DISPLAY_P3), colorSpace)
        }

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testFixedColorSpace() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        FixedColorSpace(ColorSpace.Named.LINEAR_SRGB.name).apply {
            assertEquals("Fixed(LINEAR_SRGB)", key)
            assertEquals("LINEAR_SRGB", value)
            assertEquals("FixedColorSpace(LINEAR_SRGB)", toString())
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.LINEAR_SRGB),
                actual = getColorSpace(null, isOpaque = true)
            )
        }

        FixedColorSpace(ColorSpace.Named.DISPLAY_P3).apply {
            assertEquals("Fixed(DISPLAY_P3)", key)
            assertEquals("DISPLAY_P3", value)
            assertEquals("FixedColorSpace(DISPLAY_P3)", toString())
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.Named.DISPLAY_P3),
                actual = getColorSpace(null, isOpaque = true)
            )
        }

        val element1 = FixedColorSpace(ColorSpace.Named.LINEAR_SRGB)
        val element11 = FixedColorSpace(ColorSpace.Named.LINEAR_SRGB)
        val element2 = FixedColorSpace(ColorSpace.Named.DISPLAY_P3)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }
}