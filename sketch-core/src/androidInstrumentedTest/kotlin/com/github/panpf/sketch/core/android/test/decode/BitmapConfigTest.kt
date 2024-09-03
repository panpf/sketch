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

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapConfigTest {

    @Test
    fun testBitmapConfig() {
        // TODO test
    }

    @Test
    fun testIsLowQuality() {
        assertTrue(BitmapConfig.LowQuality.isLowQuality)
        assertFalse(BitmapConfig.HighQuality.isLowQuality)
        assertFalse(BitmapConfig(RGB_565).isLowQuality)
        assertFalse(BitmapConfig(ARGB_8888).isLowQuality)
        assertEquals("BitmapConfig(LowQuality)", BitmapConfig.LowQuality.toString())
    }

    @Test
    fun testIsHighQuality() {
        assertFalse(BitmapConfig.LowQuality.isHighQuality)
        assertTrue(BitmapConfig.HighQuality.isHighQuality)
        assertFalse(BitmapConfig(RGB_565).isHighQuality)
        assertFalse(BitmapConfig(ARGB_8888).isHighQuality)
    }

    @Test
    fun testIsFixed() {
        assertFalse(BitmapConfig.LowQuality.isFixed)
        assertFalse(BitmapConfig.HighQuality.isFixed)
        assertTrue(BitmapConfig(RGB_565).isFixed)
        assertTrue(BitmapConfig(ARGB_8888).isFixed)
    }

    @Test
    fun testIsDynamic() {
        assertTrue(BitmapConfig.LowQuality.isDynamic)
        assertTrue(BitmapConfig.HighQuality.isDynamic)
        assertFalse(BitmapConfig(RGB_565).isDynamic)
        assertFalse(BitmapConfig(ARGB_8888).isDynamic)
    }

    @Test
    fun testToString() {
        assertEquals("BitmapConfig(LowQuality)", BitmapConfig.LowQuality.toString())
        assertEquals("BitmapConfig(HighQuality)", BitmapConfig.HighQuality.toString())
        assertEquals("BitmapConfig(RGB_565)", BitmapConfig(RGB_565).toString())
        assertEquals("BitmapConfig(ARGB_8888)", BitmapConfig(ARGB_8888).toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BitmapConfig(RGB_565)
        val element11 = BitmapConfig(RGB_565)
        val element2 = BitmapConfig(ARGB_8888)

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
    fun testKey() {
        BitmapConfig.LowQuality.apply {
            assertEquals("BitmapConfig(LowQuality)", key)
        }
        BitmapConfig.HighQuality.apply {
            assertEquals("BitmapConfig(HighQuality)", key)
        }
        BitmapConfig(RGB_565).apply {
            assertEquals("BitmapConfig(RGB_565)", key)
        }
    }

    @Test
    fun testGetConfig() {
        BitmapConfig.LowQuality.apply {
            assertEquals(RGB_565, getConfig("image/jpeg"))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                assertEquals(ARGB_4444, getConfig("image/png"))
            } else {
                assertEquals(ARGB_8888, getConfig("image/png"))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                assertEquals(ARGB_4444, getConfig(null))
            } else {
                assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig.HighQuality.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assertEquals(RGBA_F16, getConfig("image/jpeg"))
                assertEquals(RGBA_F16, getConfig("image/png"))
                assertEquals(RGBA_F16, getConfig(null))
            } else {
                assertEquals(ARGB_8888, getConfig("image/jpeg"))
                assertEquals(ARGB_8888, getConfig("image/png"))
                assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig(RGB_565).apply {
            assertEquals(RGB_565, getConfig("image/jpeg"))
            assertEquals(RGB_565, getConfig("image/png"))
            assertEquals(RGB_565, getConfig(null))
        }
    }
}