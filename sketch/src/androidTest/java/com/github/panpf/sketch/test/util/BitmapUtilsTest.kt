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
package com.github.panpf.sketch.test.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.cache.internal.LruBitmapPool
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.fastGaussianBlur
import com.github.panpf.sketch.util.getBytesPerPixel
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapUtilsTest {

    @Test
    fun testAllocationByteCountCompat() {
        Assert.assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCountCompat
        )

        Assert.assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )

        Assert.assertEquals(
            0,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
    }

    @Test
    fun testSafeConfig() {
        Assert.assertEquals(
            Bitmap.Config.ARGB_8888,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).safeConfig
        )

        Assert.assertEquals(
            Bitmap.Config.RGB_565,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).safeConfig
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testToInfoString() {
        Assert.assertEquals(
            "Bitmap(width=110, height=210, config=ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toInfoString()
        )

        Assert.assertEquals(
            "Bitmap(width=210, height=110, config=RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toInfoString()
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testToShortInfoString() {
        Assert.assertEquals(
            "Bitmap(110x210,ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toShortInfoString()
        )

        Assert.assertEquals(
            "Bitmap(210x110,RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toShortInfoString()
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testGetBytesPerPixel() {
        Assert.assertEquals(4, Bitmap.Config.ARGB_8888.getBytesPerPixel())
        @Suppress("DEPRECATION")
        Assert.assertEquals(2, Bitmap.Config.ARGB_4444.getBytesPerPixel())
        Assert.assertEquals(1, Bitmap.Config.ALPHA_8.getBytesPerPixel())
        Assert.assertEquals(2, Bitmap.Config.RGB_565.getBytesPerPixel())
        Assert.assertEquals(4, null.getBytesPerPixel())
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            Assert.assertEquals(8, Bitmap.Config.RGBA_F16.getBytesPerPixel())
            Assert.assertEquals(4, Bitmap.Config.HARDWARE.getBytesPerPixel())
        }
    }

    @Test
    fun testScaled() {
        val bitmapPool = LruBitmapPool(1024 * 1024 * 100)
        val bitmap = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888).apply {
            Assert.assertEquals("Bitmap(300x200,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(1.5, bitmapPool, false).apply {
            Assert.assertEquals("Bitmap(450x300,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(0.5, bitmapPool, false).apply {
            Assert.assertEquals("Bitmap(150x100,ARGB_8888)", toShortInfoString())
        }
    }

    @Test
    fun testFastGaussianBlur() {
        val context = getTestContext()
        val bitmapPool = LruBitmapPool(1024 * 1024 * 100)
        val bitmap = context.assets.open("sample.jpeg").use {
            BitmapFactory.decodeStream(it)
        }
        fastGaussianBlur(bitmap, 15).apply {
            Assert.assertEquals(bitmap.toShortInfoString(), this.toShortInfoString())
            Assert.assertNotEquals(bitmap.corners(), this.corners())
            Assert.assertNotSame(bitmap, this)
        }

        val scaledBitmap = bitmap.scaled(0.5, bitmapPool, false)
        val scaledBitmapCorners = scaledBitmap.corners()
        fastGaussianBlur(scaledBitmap, 15).apply {
            Assert.assertSame(scaledBitmap, this)
            Assert.assertNotEquals(scaledBitmapCorners, this.corners())
        }
    }
}