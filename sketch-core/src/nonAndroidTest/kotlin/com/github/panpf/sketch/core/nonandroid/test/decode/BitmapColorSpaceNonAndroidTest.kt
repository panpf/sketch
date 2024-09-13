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

import com.github.panpf.sketch.decode.BitmapColorSpace
import com.github.panpf.sketch.decode.FixedColorSpace
import com.github.panpf.sketch.decode.PlatformColorSpace
import com.github.panpf.sketch.decode.name
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class BitmapColorSpaceNonAndroidTest {

    @Test
    fun testBitmapColorSpace() {
        assertEquals(
            expected = FixedColorSpace(ColorSpace.sRGBLinear.name()),
            actual = BitmapColorSpace(ColorSpace.sRGBLinear)
        )
        assertEquals(
            expected = FixedColorSpace(ColorSpace.displayP3.name()),
            actual = BitmapColorSpace(ColorSpace.displayP3)
        )
    }

    @Test
    fun testPlatformColorSpace() {
        val element1 = PlatformColorSpace(ColorSpace.sRGBLinear).apply {
            assertEquals(ColorSpace.sRGBLinear, colorSpace)
        }
        val element11 = PlatformColorSpace(ColorSpace.sRGBLinear)
        val element2 = PlatformColorSpace(ColorSpace.displayP3).apply {
            assertEquals(ColorSpace.displayP3, colorSpace)
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
    fun testFixedColorSpace() {
        FixedColorSpace(ColorSpace.sRGBLinear.name()).apply {
            assertEquals("Fixed(sRGBLinear)", key)
            assertEquals("sRGBLinear", value)
            assertEquals("FixedColorSpace(sRGBLinear)", toString())
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.sRGBLinear),
                actual = getColorSpace(null, isOpaque = true)
            )
        }

        FixedColorSpace(ColorSpace.displayP3).apply {
            assertEquals("Fixed(displayP3)", key)
            assertEquals("displayP3", value)
            assertEquals("FixedColorSpace(displayP3)", toString())
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace("image/jpeg", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace("image/jpeg", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace("image/png", isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace("image/png", isOpaque = true)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace(null, isOpaque = false)
            )
            assertEquals(
                expected = PlatformColorSpace(ColorSpace.displayP3),
                actual = getColorSpace(null, isOpaque = true)
            )
        }

        val element1 = FixedColorSpace(ColorSpace.sRGBLinear)
        val element11 = FixedColorSpace(ColorSpace.sRGBLinear)
        val element2 = FixedColorSpace(ColorSpace.displayP3)

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