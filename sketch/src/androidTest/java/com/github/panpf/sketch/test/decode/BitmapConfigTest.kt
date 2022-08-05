@file:Suppress("DEPRECATION")

/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.test.decode

import android.graphics.Bitmap.Config.ARGB_4444
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGBA_F16
import android.graphics.Bitmap.Config.RGB_565
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.BitmapConfig
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapConfigTest {

    @Test
    fun testIsLowQuality() {
        Assert.assertTrue(BitmapConfig.LowQuality.isLowQuality)
        Assert.assertFalse(BitmapConfig.HighQuality.isLowQuality)
        Assert.assertFalse(BitmapConfig(RGB_565).isLowQuality)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isLowQuality)
        Assert.assertEquals("BitmapConfig(LowQuality)", BitmapConfig.LowQuality.toString())
    }

    @Test
    fun testIsHighQuality() {
        Assert.assertFalse(BitmapConfig.LowQuality.isHighQuality)
        Assert.assertTrue(BitmapConfig.HighQuality.isHighQuality)
        Assert.assertFalse(BitmapConfig(RGB_565).isHighQuality)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isHighQuality)
    }

    @Test
    fun testIsFixed() {
        Assert.assertFalse(BitmapConfig.LowQuality.isFixed)
        Assert.assertFalse(BitmapConfig.HighQuality.isFixed)
        Assert.assertTrue(BitmapConfig(RGB_565).isFixed)
        Assert.assertTrue(BitmapConfig(ARGB_8888).isFixed)
    }

    @Test
    fun testIsDynamic() {
        Assert.assertTrue(BitmapConfig.LowQuality.isDynamic)
        Assert.assertTrue(BitmapConfig.HighQuality.isDynamic)
        Assert.assertFalse(BitmapConfig(RGB_565).isDynamic)
        Assert.assertFalse(BitmapConfig(ARGB_8888).isDynamic)
    }

    @Test
    fun testToString() {
        Assert.assertEquals("BitmapConfig(LowQuality)", BitmapConfig.LowQuality.toString())
        Assert.assertEquals("BitmapConfig(HighQuality)", BitmapConfig.HighQuality.toString())
        Assert.assertEquals("BitmapConfig(RGB_565)", BitmapConfig(RGB_565).toString())
        Assert.assertEquals("BitmapConfig(ARGB_8888)", BitmapConfig(ARGB_8888).toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BitmapConfig(RGB_565)
        val element11 = BitmapConfig(RGB_565)
        val element2 = BitmapConfig(ARGB_8888)

        Assert.assertNotSame(element1, element11)
        Assert.assertNotSame(element1, element2)
        Assert.assertNotSame(element2, element11)

        Assert.assertEquals(element1, element1)
        Assert.assertEquals(element1, element11)
        Assert.assertNotEquals(element1, element2)
        Assert.assertNotEquals(element2, element11)
        Assert.assertNotEquals(element1, null)
        Assert.assertNotEquals(element1, Any())

        Assert.assertEquals(element1.hashCode(), element1.hashCode())
        Assert.assertEquals(element1.hashCode(), element11.hashCode())
        Assert.assertNotEquals(element1.hashCode(), element2.hashCode())
        Assert.assertNotEquals(element2.hashCode(), element11.hashCode())
    }

    @Test
    fun testKey() {
        BitmapConfig.LowQuality.apply {
            Assert.assertEquals("BitmapConfig(LowQuality)", key)
        }
        BitmapConfig.HighQuality.apply {
            Assert.assertEquals("BitmapConfig(HighQuality)", key)
        }
        BitmapConfig(RGB_565).apply {
            Assert.assertEquals("BitmapConfig(RGB_565)", key)
        }
    }

    @Test
    fun testGetConfig() {
        BitmapConfig.LowQuality.apply {
            Assert.assertEquals(RGB_565, getConfig("image/jpeg"))
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                Assert.assertEquals(ARGB_4444, getConfig("image/png"))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig("image/png"))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                Assert.assertEquals(ARGB_4444, getConfig(null))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig.HighQuality.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Assert.assertEquals(RGBA_F16, getConfig("image/jpeg"))
                Assert.assertEquals(RGBA_F16, getConfig("image/png"))
                Assert.assertEquals(RGBA_F16, getConfig(null))
            } else {
                Assert.assertEquals(ARGB_8888, getConfig("image/jpeg"))
                Assert.assertEquals(ARGB_8888, getConfig("image/png"))
                Assert.assertEquals(ARGB_8888, getConfig(null))
            }
        }

        BitmapConfig(RGB_565).apply {
            Assert.assertEquals(RGB_565, getConfig("image/jpeg"))
            Assert.assertEquals(RGB_565, getConfig("image/png"))
            Assert.assertEquals(RGB_565, getConfig(null))
        }
    }
}