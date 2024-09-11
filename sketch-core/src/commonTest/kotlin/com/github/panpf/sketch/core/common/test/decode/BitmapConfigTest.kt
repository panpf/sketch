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

package com.github.panpf.sketch.core.common.test.decode

import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.decode.isDynamic
import com.github.panpf.sketch.decode.isFixed
import com.github.panpf.sketch.decode.isHighQuality
import com.github.panpf.sketch.decode.isLowQuality
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class BitmapConfigTest {

    @Test
    fun testBitmapConfig() {
        assertEquals(
            BitmapConfig.LowQuality,
            BitmapConfig("LowQuality")
        )
        assertEquals(
            BitmapConfig.HighQuality,
            BitmapConfig("HighQuality")
        )
        assertEquals(
            BitmapConfig.FixedQuality("ARGB_8888"),
            BitmapConfig("ARGB_8888")
        )
    }

    @Test
    fun testIsLowQuality() {
        assertTrue(BitmapConfig.LowQuality.isLowQuality)
        assertFalse(BitmapConfig.HighQuality.isLowQuality)
        assertFalse(BitmapConfig("ARGB_8888").isLowQuality)
    }

    @Test
    fun testIsHighQuality() {
        assertFalse(BitmapConfig.LowQuality.isHighQuality)
        assertTrue(BitmapConfig.HighQuality.isHighQuality)
        assertFalse(BitmapConfig("ARGB_8888").isHighQuality)
    }

    @Test
    fun testIsFixed() {
        assertFalse(BitmapConfig.LowQuality.isFixed)
        assertFalse(BitmapConfig.HighQuality.isFixed)
        assertTrue(BitmapConfig("ARGB_8888").isFixed)
    }

    @Test
    fun testIsDynamic() {
        assertTrue(BitmapConfig.LowQuality.isDynamic)
        assertTrue(BitmapConfig.HighQuality.isDynamic)
        assertFalse(BitmapConfig("ARGB_8888").isDynamic)
    }

    @Test
    fun testLowQuality() {
        BitmapConfig.LowQuality.apply {
            assertEquals("LowQuality", key)
            assertEquals("LowQuality", value)
            assertEquals("LowQuality", toString())
        }
    }

    @Test
    fun testHighQuality() {
        BitmapConfig.HighQuality.apply {
            assertEquals("HighQuality", key)
            assertEquals("HighQuality", value)
            assertEquals("HighQuality", toString())
        }
    }

    @Test
    fun testFixedQuality() {
        BitmapConfig.FixedQuality("ARGB_8888").apply {
            assertEquals("FixedQuality(ARGB_8888)", key)
            assertEquals("ARGB_8888", value)
            assertEquals("FixedQuality(ARGB_8888)", toString())
        }

        val element1 = BitmapConfig.FixedQuality("ARGB_8888")
        val element11 = BitmapConfig.FixedQuality("ARGB_8888")
        val element2 = BitmapConfig.FixedQuality("ARGB_4444")

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