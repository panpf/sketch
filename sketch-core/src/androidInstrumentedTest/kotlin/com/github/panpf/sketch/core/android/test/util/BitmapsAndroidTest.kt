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
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.hammingDistance
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.background
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.circleCrop
import com.github.panpf.sketch.util.configOrNull
import com.github.panpf.sketch.util.getBytesPerPixel
import com.github.panpf.sketch.util.hasAlphaPixels
import com.github.panpf.sketch.util.isHardware
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.mutableCopyOrSelf
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.roundedCorners
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.safeToSoftware
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toShortInfoString
import org.junit.runner.RunWith
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BitmapsAndroidTest {

    @Test
    fun testIsHardware() {
        assertFalse(actual = ColorType.ARGB_8888.isHardware())
        assertFalse(actual = ColorType.RGB_565.isHardware())
        assertFalse(actual = ColorType.ALPHA_8.isHardware())
        @Suppress("DEPRECATION")
        assertFalse(actual = ColorType.ARGB_4444.isHardware())
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertFalse(actual = ColorType.RGBA_F16.isHardware())
            assertTrue(actual = ColorType.HARDWARE.isHardware())
        }
    }

    @Test
    fun testToSoftware() {
        assertEquals(
            expected = ColorType.ARGB_8888,
            actual = null.safeToSoftware()
        )
        assertEquals(
            expected = ColorType.ARGB_8888,
            actual = ColorType.ARGB_8888.safeToSoftware()
        )
        assertEquals(
            expected = ColorType.RGB_565,
            actual = ColorType.RGB_565.safeToSoftware()
        )
        assertEquals(
            expected = ColorType.ALPHA_8,
            actual = ColorType.ALPHA_8.safeToSoftware()
        )
        @Suppress("DEPRECATION")
        assertEquals(
            expected = ColorType.ARGB_4444,
            actual = ColorType.ARGB_4444.safeToSoftware()
        )
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertEquals(
                expected = ColorType.RGBA_F16,
                actual = ColorType.RGBA_F16.safeToSoftware()
            )
            assertEquals(
                expected = ColorType.ARGB_8888,
                actual = ColorType.HARDWARE.safeToSoftware()
            )
        }
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            assertEquals(
                expected = ColorType.RGBA_1010102,
                actual = ColorType.RGBA_1010102.safeToSoftware()
            )
        }
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
    fun testConfigOrNull() {
        assertEquals(
            expected = Bitmap.Config.ARGB_8888,
            actual = AndroidBitmap(100, 100).configOrNull
        )
        // Unable to create Bitmap with null config
    }

    @Test
    fun testIsImmutable() {
        assertFalse(AndroidBitmap(100, 100).isImmutable)
        assertTrue(ResourceImages.jpeg.decode().bitmap.isImmutable)
    }

    @Test
    fun testSafeConfig() {
        assertEquals(
            expected = Bitmap.Config.ARGB_8888,
            actual = Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).safeConfig
        )
        assertEquals(
            expected = Bitmap.Config.RGB_565,
            actual = Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).safeConfig
        )
        // Unable to create Bitmap with null config
    }

    @Test
    fun testMutableCopyOrSelf() {
        val mutableBitmap = AndroidBitmap(100, 100)
        assertTrue(mutableBitmap.isMutable)
        val copiedMutableBitmap = mutableBitmap.mutableCopyOrSelf()
        assertSame(expected = mutableBitmap, actual = copiedMutableBitmap)

        val immutableBitmap = ResourceImages.jpeg.decode().bitmap
        assertFalse(immutableBitmap.isMutable)
        val copiedImmutableBitmap = immutableBitmap.mutableCopyOrSelf()
        assertNotSame(illegal = immutableBitmap, actual = copiedImmutableBitmap)
    }

    @Test
    fun testSimpleName() {
        // TODO test
    }

    @Test
    fun testToLogString() {
        Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).apply {
            assertEquals(
                expected = "Bitmap@${this.toHexString()}(110x210,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toLogString()
            )
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            Bitmap.createBitmap(
                210,
                110,
                Bitmap.Config.RGB_565,
                true,
                ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
            )
        } else {
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565)
        }.apply {
            assertEquals(
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "Bitmap@${this.toHexString()}(210x110,RGB_565,LINEAR_SRGB)"
                } else {
                    "Bitmap@${this.toHexString()}(210x110,RGB_565)"
                },
                actual = toLogString()
            )
        }
    }

    @Test
    fun testToInfoString() {
        Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).apply {
            assertEquals(
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "Bitmap(width=110, height=210, config=ARGB_8888, colorSpace=SRGB)"
                } else {
                    "Bitmap(width=110, height=210, config=ARGB_8888)"
                },
                actual = toInfoString()
            )
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            Bitmap.createBitmap(
                210,
                110,
                Bitmap.Config.RGB_565,
                true,
                ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
            )
        } else {
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565)
        }.apply {
            assertEquals(
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "Bitmap(width=210, height=110, config=RGB_565, colorSpace=LINEAR_SRGB)"
                } else {
                    "Bitmap(width=210, height=110, config=RGB_565)"
                },
                actual = toInfoString()
            )
        }
    }

    @Test
    fun testToShortInfoString() {
        Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).apply {
            assertEquals(
                expected = "Bitmap(110x210,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            Bitmap.createBitmap(
                210,
                110,
                Bitmap.Config.RGB_565,
                true,
                ColorSpace.get(ColorSpace.Named.LINEAR_SRGB)
            )
        } else {
            Bitmap.createBitmap(210, 110, Bitmap.Config.RGB_565)
        }.apply {
            assertEquals(
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "Bitmap(210x110,RGB_565,LINEAR_SRGB)"
                } else {
                    "Bitmap(210x110,RGB_565)"
                },
                actual = toShortInfoString()
            )
        }
    }

    @Test
    fun testMutableCopy() {
        // TODO test
    }

    @Test
    fun testCopyWith() {
        // TODO test
    }

    @Test
    fun testReadIntPixels() {
        // TODO test
    }

    @Test
    fun testInstallIntPixels() {
        // TODO test
    }

    @Test
    fun testReadIntPixel() {
        // TODO test
    }

    @Test
    fun testHasAlphaPixels() {
        assertFalse(
            ResourceImages.jpeg.decode().bitmap.hasAlphaPixels()
        )
        assertTrue(
            ResourceImages.png.decode().bitmap.hasAlphaPixels()
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBackgrounded() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.background(TestColor.RED).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = produceFingerPrint(this)
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.background(TestColor.BLUE).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            blueBgBitmapFinger = produceFingerPrint(this)
            blueBgBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = redBgBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = blueBgBitmapCorners)

        assertEquals(expected = sourceBitmapCorners, actual = redBgBitmapCorners)
        assertEquals(expected = sourceBitmapCorners, actual = blueBgBitmapCorners)
        assertEquals(expected = redBgBitmapCorners, actual = blueBgBitmapCorners)

        // Fingerprints ignore color, so it's all the same
        assertEquals(expected = sourceBitmapFinger, actual = redBgBitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = blueBgBitmapFinger)
        assertEquals(expected = redBgBitmapFinger, actual = blueBgBitmapFinger)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBackgrounded2() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.png.decode().bitmap.apply {
                assertEquals(
                    expected = "Bitmap(750x719,ARGB_8888${shortInfoColorSpace("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.background(TestColor.RED).apply {
            assertEquals(
                expected = "Bitmap(750x719,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = produceFingerPrint(this)
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.background(TestColor.BLUE).apply {
            assertEquals(
                expected = "Bitmap(750x719,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            blueBgBitmapFinger = produceFingerPrint(this)
            blueBgBitmapCorners = corners()
        }

        assertEquals(expected = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = redBgBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = blueBgBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = redBgBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = blueBgBitmapCorners)
        assertNotEquals(illegal = redBgBitmapCorners, actual = blueBgBitmapCorners)

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, redBgBitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, redBgBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, blueBgBitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, blueBgBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(redBgBitmapFinger, blueBgBitmapFinger) < 5,
            message = hammingDistance(redBgBitmapFinger, blueBgBitmapFinger).toString()
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBlur() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val smallRadiusBlurBitmapFinger: String
        val smallRadiusBlurBitmapCorners: List<Int>
        val smallRadiusBlurBitmap = sourceBitmap.blur(20).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            smallRadiusBlurBitmapFinger = produceFingerPrint(this)
            smallRadiusBlurBitmapCorners = corners()
        }

        val bigRadiusBlurBitmapFinger: String
        val bigRadiusBlurBitmapCorners: List<Int>
        val bigRadiusBlurBitmap = sourceBitmap.blur(50).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            bigRadiusBlurBitmapFinger = produceFingerPrint(this)
            bigRadiusBlurBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = smallRadiusBlurBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = bigRadiusBlurBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = smallRadiusBlurBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = bigRadiusBlurBitmapCorners)
        assertNotEquals(illegal = smallRadiusBlurBitmapCorners, actual = bigRadiusBlurBitmapCorners)

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, smallRadiusBlurBitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, smallRadiusBlurBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, bigRadiusBlurBitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, bigRadiusBlurBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(smallRadiusBlurBitmapFinger, bigRadiusBlurBitmapFinger) < 5,
            message = hammingDistance(
                smallRadiusBlurBitmapFinger,
                bigRadiusBlurBitmapFinger
            ).toString()
        )

        // TODO test firstReuseSelf
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testCircleCrop() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val startCropBitmapFinger: String
        val startCropBitmapCorners: List<Int>
        val startCropBitmap = sourceBitmap.circleCrop(Scale.START_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            startCropBitmapFinger = produceFingerPrint(this)
            startCropBitmapCorners = corners()
        }

        val centerCropBitmapFinger: String
        val centerCropBitmapCorners: List<Int>
        val centerCropBitmap = sourceBitmap.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            centerCropBitmapFinger = produceFingerPrint(this)
            centerCropBitmapCorners = corners()
        }

        val endCropBitmapFinger: String
        val endCropBitmapCorners: List<Int>
        val endCropBitmap = sourceBitmap.circleCrop(Scale.END_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            endCropBitmapFinger = produceFingerPrint(this)
            endCropBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = startCropBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = centerCropBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = endCropBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = startCropBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = centerCropBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = endCropBitmapCorners)
        assertEquals(expected = startCropBitmapCorners, actual = centerCropBitmapCorners)
        assertEquals(expected = startCropBitmapCorners, actual = endCropBitmapCorners)
        assertEquals(expected = centerCropBitmapCorners, actual = endCropBitmapCorners)

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, startCropBitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, startCropBitmapFinger).toString()
        )
        // TODO Should >= 5
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, centerCropBitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, centerCropBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, endCropBitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, endCropBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(startCropBitmapFinger, centerCropBitmapFinger) >= 5,
            message = hammingDistance(startCropBitmapFinger, centerCropBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(startCropBitmapFinger, endCropBitmapFinger) >= 5,
            message = hammingDistance(startCropBitmapFinger, endCropBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(centerCropBitmapFinger, endCropBitmapFinger) >= 5,
            message = hammingDistance(centerCropBitmapFinger, endCropBitmapFinger).toString()
        )


        ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMapping() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val bigSize = sourceBitmap.size.let { max(it.width, it.height) }.let { Size(it, it) }
        val resize1 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.CENTER_CROP)
        val resize1Mapping = resize1.calculateMapping(sourceBitmap.size)
        val resize1BitmapFinger: String
        val resize1BitmapCorners: List<Int>
        val resize1Bitmap = sourceBitmap.mapping(resize1Mapping).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            resize1BitmapFinger = produceFingerPrint(this)
            resize1BitmapCorners = corners()
        }

        val resize2 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.START_CROP)
        val resize2Mapping = resize2.calculateMapping(sourceBitmap.size)
        val resize2BitmapFinger: String
        val resize2BitmapCorners: List<Int>
        val resize2Bitmap = sourceBitmap.mapping(resize2Mapping).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            resize2BitmapFinger = produceFingerPrint(this)
            resize2BitmapCorners = corners()
        }

        val resize3 = Resize(bigSize, Precision.EXACTLY, Scale.CENTER_CROP)
        val resize3Mapping = resize3.calculateMapping(sourceBitmap.size)
        val resize3BitmapFinger: String
        val resize3BitmapCorners: List<Int>
        val resize3Bitmap = sourceBitmap.mapping(resize3Mapping).apply {
            assertEquals(
                expected = "Bitmap(1936x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            resize3BitmapFinger = produceFingerPrint(this)
            resize3BitmapCorners = corners()
        }

        val resize4 = Resize(bigSize, Precision.EXACTLY, Scale.START_CROP)
        val resize4Mapping = resize4.calculateMapping(sourceBitmap.size)
        val resize4BitmapFinger: String
        val resize4BitmapCorners: List<Int>
        val resize4Bitmap = sourceBitmap.mapping(resize4Mapping).apply {
            assertEquals(
                expected = "Bitmap(1936x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            resize4BitmapFinger = produceFingerPrint(this)
            resize4BitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = resize1BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = resize2BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = resize3BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = resize4BitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = resize1BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = resize2BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = resize3BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = resize4BitmapCorners)
        assertNotEquals(illegal = resize1BitmapCorners, actual = resize2BitmapCorners)
        assertEquals(expected = resize1BitmapCorners, actual = resize3BitmapCorners)
        assertNotEquals(illegal = resize1BitmapCorners, actual = resize4BitmapCorners)
        assertNotEquals(illegal = resize2BitmapCorners, actual = resize3BitmapCorners)
        assertEquals(expected = resize2BitmapCorners, actual = resize4BitmapCorners)
        assertNotEquals(illegal = resize3BitmapCorners, actual = resize4BitmapCorners)

        // TODO Should >= 5
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize1BitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, resize1BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize2BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, resize2BitmapFinger).toString()
        )
        // TODO Should >= 5
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize3BitmapFinger) < 5,
            message = hammingDistance(sourceBitmapFinger, resize3BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize4BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, resize4BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize1BitmapFinger, resize2BitmapFinger) >= 5,
            message = hammingDistance(resize1BitmapFinger, resize2BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize1BitmapFinger, resize3BitmapFinger) < 5,
            message = hammingDistance(resize1BitmapFinger, resize3BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize1BitmapFinger, resize4BitmapFinger) >= 5,
            message = hammingDistance(resize1BitmapFinger, resize4BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize2BitmapFinger, resize3BitmapFinger) >= 5,
            message = hammingDistance(resize2BitmapFinger, resize3BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize2BitmapFinger, resize4BitmapFinger) < 5,
            message = hammingDistance(resize2BitmapFinger, resize4BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(resize3BitmapFinger, resize4BitmapFinger) >= 5,
            message = hammingDistance(resize3BitmapFinger, resize4BitmapFinger).toString()
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMask() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val redMaskBitmapFinger: String
        val redMaskBitmapCorners: List<Int>
        val redMaskBitmap = sourceBitmap.mask(TestColor.withA(TestColor.RED, a = 100)).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            redMaskBitmapFinger = produceFingerPrint(this)
            redMaskBitmapCorners = corners()
        }

        val greenMaskBitmapFinger: String
        val greenMaskBitmapCorners: List<Int>
        val greenMaskBitmap = sourceBitmap.mask(TestColor.withA(TestColor.GREEN, a = 100)).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            greenMaskBitmapFinger = produceFingerPrint(this)
            greenMaskBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = redMaskBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = greenMaskBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = redMaskBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = greenMaskBitmapCorners)
        assertNotEquals(illegal = redMaskBitmapCorners, actual = greenMaskBitmapCorners)

        // Fingerprints ignore color, so it's all the same
        assertEquals(expected = sourceBitmapFinger, actual = redMaskBitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = greenMaskBitmapFinger)
        assertEquals(expected = redMaskBitmapFinger, actual = greenMaskBitmapFinger)

        // TODO test firstReuseSelf
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRotate() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val rotate90BitmapFinger: String
        val rotate90BitmapCorners: List<Int>
        val rotate90Bitmap = sourceBitmap.rotate(90).apply {
            assertEquals(
                expected = "Bitmap(1936x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            rotate90BitmapFinger = produceFingerPrint(this)
            rotate90BitmapCorners = corners()
        }

        val rotate180BitmapFinger: String
        val rotate180BitmapCorners: List<Int>
        val rotate180Bitmap = sourceBitmap.rotate(180).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            rotate180BitmapFinger = produceFingerPrint(this)
            rotate180BitmapCorners = corners()
        }

        val rotate270BitmapFinger: String
        val rotate270BitmapCorners: List<Int>
        val rotate270Bitmap = sourceBitmap.rotate(270).apply {
            assertEquals(
                expected = "Bitmap(1936x1291,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            rotate270BitmapFinger = produceFingerPrint(this)
            rotate270BitmapCorners = corners()
        }

        val rotate360BitmapFinger: String
        val rotate360BitmapCorners: List<Int>
        val rotate360Bitmap = sourceBitmap.rotate(360).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            rotate360BitmapFinger = produceFingerPrint(this)
            rotate360BitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate90BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate180BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate270BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = rotate360BitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate90BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate180BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = rotate270BitmapCorners)
        assertEquals(expected = sourceBitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate180BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate270BitmapCorners)
        assertNotEquals(illegal = rotate90BitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate180BitmapCorners, actual = rotate270BitmapCorners)
        assertNotEquals(illegal = rotate180BitmapCorners, actual = rotate360BitmapCorners)
        assertNotEquals(illegal = rotate270BitmapCorners, actual = rotate360BitmapCorners)

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, rotate90BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, rotate90BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, rotate180BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, rotate180BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, rotate270BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, rotate270BitmapFinger).toString()
        )
        assertEquals(expected = sourceBitmapFinger, actual = rotate360BitmapFinger)
        assertTrue(
            actual = hammingDistance(rotate90BitmapFinger, rotate180BitmapFinger) >= 5,
            message = hammingDistance(rotate90BitmapFinger, rotate180BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(rotate90BitmapFinger, rotate270BitmapFinger) >= 5,
            message = hammingDistance(rotate90BitmapFinger, rotate270BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(rotate90BitmapFinger, rotate360BitmapFinger) >= 5,
            message = hammingDistance(rotate90BitmapFinger, rotate360BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(rotate180BitmapFinger, rotate270BitmapFinger) >= 5,
            message = hammingDistance(rotate180BitmapFinger, rotate270BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(rotate180BitmapFinger, rotate360BitmapFinger) >= 5,
            message = hammingDistance(rotate180BitmapFinger, rotate360BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(rotate270BitmapFinger, rotate360BitmapFinger) >= 5,
            message = hammingDistance(rotate270BitmapFinger, rotate360BitmapFinger).toString()
        )


        ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotate(130).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotate(130).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRoundedCorners() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this)
            sourceBitmapCorners = corners()
        }

        val smallRoundedCorneredBitmapFinger: String
        val smallRoundedCorneredBitmapCorners: List<Int>
        val smallRoundedCorneredBitmap =
            sourceBitmap.roundedCorners(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
                .apply {
                    assertEquals(
                        expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                        actual = toShortInfoString()
                    )
                    smallRoundedCorneredBitmapFinger = produceFingerPrint(this)
                    smallRoundedCorneredBitmapCorners = corners()
                }

        val bigRoundedCorneredBitmapFinger: String
        val bigRoundedCorneredBitmapCorners: List<Int>
        val bigRoundedCorneredBitmap =
            sourceBitmap.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f))
                .apply {
                    assertEquals(
                        expected = "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                        actual = toShortInfoString()
                    )
                    bigRoundedCorneredBitmapFinger = produceFingerPrint(this)
                    bigRoundedCorneredBitmapCorners = corners()
                }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = smallRoundedCorneredBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = bigRoundedCorneredBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = smallRoundedCorneredBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = bigRoundedCorneredBitmapCorners)
        assertEquals(
            expected = smallRoundedCorneredBitmapCorners,
            actual = bigRoundedCorneredBitmapCorners
        )

        // TODO It seems like it shouldn't be the same here
        assertEquals(expected = sourceBitmapFinger, actual = smallRoundedCorneredBitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = bigRoundedCorneredBitmapFinger)
        assertEquals(
            expected = smallRoundedCorneredBitmapFinger,
            actual = bigRoundedCorneredBitmapFinger
        )


        ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    fun testScale() {
        val bitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                "Bitmap(1291x1936,ARGB_8888${shortInfoColorSpace("SRGB")})",
                toShortInfoString()
            )
        }
        bitmap.scale(1.5f).apply {
            assertEquals(
                "Bitmap(1937x2904,ARGB_8888${shortInfoColorSpace("SRGB")})",
                toShortInfoString()
            )
        }
        bitmap.scale(0.5f).apply {
            assertEquals(
                "Bitmap(646x968,ARGB_8888${shortInfoColorSpace("SRGB")})",
                toShortInfoString()
            )
        }
    }
}