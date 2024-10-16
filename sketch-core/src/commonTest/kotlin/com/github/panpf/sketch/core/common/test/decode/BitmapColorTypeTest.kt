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

import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.FixedColorType
import com.github.panpf.sketch.decode.HighQualityColorType
import com.github.panpf.sketch.decode.LowQualityColorType
import com.github.panpf.sketch.decode.isDynamic
import com.github.panpf.sketch.decode.isFixed
import com.github.panpf.sketch.decode.isHighQuality
import com.github.panpf.sketch.decode.isLowQuality
import com.github.panpf.sketch.test.utils.FakeBitmapColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapColorTypeTest {

    @Test
    fun testBitmapColorType() {
        assertEquals(
            FixedColorType("LowQuality"),
            BitmapColorType("LowQuality")
        )
        assertEquals(
            FixedColorType("HighQuality"),
            BitmapColorType("HighQuality")
        )
        assertEquals(
            FixedColorType("ARGB_8888"),
            BitmapColorType("ARGB_8888")
        )
    }

    @Test
    fun testIsLowQuality() {
        assertTrue(LowQualityColorType.isLowQuality)
        assertFalse(HighQualityColorType.isLowQuality)
        assertFalse(BitmapColorType("ARGB_8888").isLowQuality)
    }

    @Test
    fun testIsHighQuality() {
        assertFalse(LowQualityColorType.isHighQuality)
        assertTrue(HighQualityColorType.isHighQuality)
        assertFalse(BitmapColorType("ARGB_8888").isHighQuality)
    }

    @Test
    fun testIsFixed() {
        assertFalse(LowQualityColorType.isFixed)
        assertFalse(HighQualityColorType.isFixed)
        assertTrue(BitmapColorType("ARGB_8888").isFixed)
    }

    @Test
    fun testIsDynamic() {
        assertTrue(LowQualityColorType.isDynamic)
        assertTrue(HighQualityColorType.isDynamic)
        assertFalse(BitmapColorType("ARGB_8888").isDynamic)
        assertFalse(FakeBitmapColorType.isDynamic)
    }
}