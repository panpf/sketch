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

package com.github.panpf.sketch.core.android.test.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.getBytesPerPixel
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class AndroidBitmapsTest {

    @Test
    fun testAllocationByteCountCompat() {
        assertEquals(
            110 * 210 * 4,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).allocationByteCountCompat
        )

        assertEquals(
            110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )

        assertEquals(
            if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
    }

    @Test
    fun testConfigOrNull() {
        // TODO test
    }

    @Test
    fun testIsImmutable() {
        // TODO test
    }

    @Test
    fun testSafeConfig() {
        assertEquals(
            Bitmap.Config.ARGB_8888,
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).safeConfig
        )

        assertEquals(
            Bitmap.Config.RGB_565,
            Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).safeConfig
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testGetMutableCopy() {
        // TODO test
    }

    @Test
    fun testToInfoString() {
        assertEquals(
            "AndroidBitmap(width=110, height=210, config=ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toInfoString()
        )

        assertEquals(
            "AndroidBitmap(width=210, height=110, config=RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toInfoString()
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testToShortInfoString() {
        assertEquals(
            "AndroidBitmap(110x210,ARGB_8888)",
            Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).toShortInfoString()
        )

        assertEquals(
            "AndroidBitmap(210x110,RGB_565)",
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565).toShortInfoString()
        )

        // Unable to create Bitmap with null config
    }

    @Test
    fun testToLogString() {
        // TODO test
    }

    @Test
    fun testBackgrounded() {
        // TODO test
    }

    @Test
    fun testBlur() {
        val context = getTestContext()
        val bitmap = context.assets.open(ResourceImages.jpeg.resourceName).use {
            BitmapFactory.decodeStream(it)
        }
        bitmap.copy(Bitmap.Config.ARGB_8888, true).apply { blur(15) }.apply {
            assertEquals(bitmap.toShortInfoString(), this.toShortInfoString())
            assertNotEquals(bitmap.corners(), this.corners())
            assertNotSame(bitmap, this)
        }

        val scaledBitmap = bitmap.scaled(0.5f)
        val scaledBitmapCorners = scaledBitmap.corners()
        scaledBitmap.apply { blur(15) }.apply {
            assertSame(scaledBitmap, this)
            assertNotEquals(scaledBitmapCorners, this.corners())
        }
    }

    @Test
    fun testCircleCropped() {
        // TODO test
    }

    @Test
    fun testMask() {
        // TODO test
    }

    @Test
    fun testRoundedCornered() {
        // TODO test
    }

    @Test
    fun testRotated() {
        // TODO test
    }

    @Test
    fun testMapping() {
        // TODO test
    }

    @Test
    fun testScaled() {
        val bitmap = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888).apply {
            assertEquals("AndroidBitmap(300x200,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(1.5f).apply {
            assertEquals("AndroidBitmap(450x300,ARGB_8888)", toShortInfoString())
        }
        bitmap.scaled(0.5f).apply {
            assertEquals("AndroidBitmap(150x100,ARGB_8888)", toShortInfoString())
        }
    }

    @Test
    fun testIsHardware() {
        // TODO test
    }

    @Test
    fun testGetBytesPerPixel() {
        assertEquals(4, Bitmap.Config.ARGB_8888.getBytesPerPixel())
        @Suppress("DEPRECATION")
        assertEquals(2, Bitmap.Config.ARGB_4444.getBytesPerPixel())
        assertEquals(1, Bitmap.Config.ALPHA_8.getBytesPerPixel())
        assertEquals(2, Bitmap.Config.RGB_565.getBytesPerPixel())
        assertEquals(4, null.getBytesPerPixel())
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertEquals(8, Bitmap.Config.RGBA_F16.getBytesPerPixel())
            assertEquals(4, Bitmap.Config.HARDWARE.getBytesPerPixel())
        }
    }

    @Test
    fun testCalculateBitmapByteCount() {
        // TODO test
    }
}