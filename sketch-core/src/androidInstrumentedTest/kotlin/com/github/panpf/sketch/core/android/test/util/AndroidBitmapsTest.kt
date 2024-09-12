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
import com.github.panpf.sketch.AndroidBitmapConfig
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.decode.BitmapConfig
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.hammingDistance
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.test.utils.shortInfoColorSpaceName
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.allocationByteCountCompat
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.backgrounded
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.circleCropped
import com.github.panpf.sketch.util.configOrNull
import com.github.panpf.sketch.util.getBytesPerPixel
import com.github.panpf.sketch.util.getMutableCopy
import com.github.panpf.sketch.util.hasAlphaPixels
import com.github.panpf.sketch.util.isHardware
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.rotated
import com.github.panpf.sketch.util.roundedCornered
import com.github.panpf.sketch.util.safeConfig
import com.github.panpf.sketch.util.scaled
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
class AndroidBitmapsTest {

    @Test
    fun testIsHardware() {
        assertFalse(actual = AndroidBitmapConfig.ARGB_8888.isHardware())
        assertFalse(actual = AndroidBitmapConfig.RGB_565.isHardware())
        assertFalse(actual = AndroidBitmapConfig.ALPHA_8.isHardware())
        @Suppress("DEPRECATION")
        assertFalse(actual = AndroidBitmapConfig.ARGB_4444.isHardware())
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            assertFalse(actual = AndroidBitmapConfig.RGBA_F16.isHardware())
            assertTrue(actual = AndroidBitmapConfig.HARDWARE.isHardware())
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
    fun testAllocationByteCountCompat() {
        assertEquals(
            expected = 110 * 210 * 4,
            actual = Bitmap.createBitmap(
                110,
                210,
                Bitmap.Config.ARGB_8888
            ).allocationByteCountCompat
        )
        assertEquals(
            expected = 110 * 210 * 2,
            actual = Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565).allocationByteCountCompat
        )
        assertEquals(
            expected = if (VERSION.SDK_INT >= VERSION_CODES.O) 0 else 110 * 210 * 2,
            actual = Bitmap.createBitmap(110, 210, Bitmap.Config.RGB_565)
                .apply { recycle() }
                .allocationByteCountCompat
        )
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
        assertTrue(ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.isImmutable)
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
    fun testMutableCopy() {
        val mutableBitmap = AndroidBitmap(100, 100)
        assertTrue(mutableBitmap.isMutable)
        val copiedMutableBitmap = mutableBitmap.getMutableCopy()
        assertSame(expected = mutableBitmap, actual = copiedMutableBitmap)

        val immutableBitmap = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap
        assertFalse(immutableBitmap.isMutable)
        val copiedImmutableBitmap = immutableBitmap.getMutableCopy()
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
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "AndroidBitmap@${this.toHexString()}(110x210,ARGB_8888,SRGB)"
                } else {
                    "AndroidBitmap@${this.toHexString()}(110x210,ARGB_8888${
                        shortInfoColorSpaceName(
                            "SRGB"
                        )
                    })"
                },
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
                    "AndroidBitmap@${this.toHexString()}(210x110,RGB_565,LINEAR_SRGB)"
                } else {
                    "AndroidBitmap@${this.toHexString()}(210x110,RGB_565)"
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
                    "AndroidBitmap(width=110, height=210, config=ARGB_8888, colorSpace=SRGB)"
                } else {
                    "AndroidBitmap(width=110, height=210, config=ARGB_8888)"
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
                    "AndroidBitmap(width=210, height=110, config=RGB_565, colorSpace=LINEAR_SRGB)"
                } else {
                    "AndroidBitmap(width=210, height=110, config=RGB_565)"
                },
                actual = toInfoString()
            )
        }
    }

    @Test
    fun testToShortInfoString() {
        Bitmap.createBitmap(110, 210, Bitmap.Config.ARGB_8888).apply {
            assertEquals(
                expected = if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    "AndroidBitmap(110x210,ARGB_8888,SRGB)"
                } else {
                    "AndroidBitmap(110x210,ARGB_8888${shortInfoColorSpaceName("SRGB")})"
                },
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
                    "AndroidBitmap(210x110,RGB_565,LINEAR_SRGB)"
                } else {
                    "AndroidBitmap(210x110,RGB_565)"
                },
                actual = toShortInfoString()
            )
        }
    }

    @Test
    fun testGetIntPixels() {
        // TODO test
    }

    @Test
    fun testHasAlphaPixels() {
        assertFalse(
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.hasAlphaPixels()
        )
        assertTrue(
            ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>().bitmap.hasAlphaPixels()
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBackgrounded() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.backgrounded(TestColor.RED).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = produceFingerPrint(this)
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.backgrounded(TestColor.BLUE).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
            ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(750x719,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.backgrounded(TestColor.RED).apply {
            assertEquals(
                expected = "AndroidBitmap(750x719,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = produceFingerPrint(this)
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.backgrounded(TestColor.BLUE).apply {
            assertEquals(
                expected = "AndroidBitmap(750x719,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val smallRadiusBlurBitmapFinger: String
        val smallRadiusBlurBitmapCorners: List<Int>
        val smallRadiusBlurBitmap =
            sourceBitmap.copy(Bitmap.Config.ARGB_8888, true).apply { blur(20) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                smallRadiusBlurBitmapFinger = produceFingerPrint(this)
                smallRadiusBlurBitmapCorners = corners()
            }

        val bigRadiusBlurBitmapFinger: String
        val bigRadiusBlurBitmapCorners: List<Int>
        val bigRadiusBlurBitmap =
            sourceBitmap.copy(Bitmap.Config.ARGB_8888, true).apply { blur(50) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testCircleCropped() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val startCropBitmapFinger: String
        val startCropBitmapCorners: List<Int>
        val startCropBitmap = sourceBitmap.circleCropped(Scale.START_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            startCropBitmapFinger = produceFingerPrint(this)
            startCropBitmapCorners = corners()
        }

        val centerCropBitmapFinger: String
        val centerCropBitmapCorners: List<Int>
        val centerCropBitmap = sourceBitmap.circleCropped(Scale.CENTER_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            centerCropBitmapFinger = produceFingerPrint(this)
            centerCropBitmapCorners = corners()
        }

        val endCropBitmapFinger: String
        val endCropBitmapCorners: List<Int>
        val endCropBitmap = sourceBitmap.circleCropped(Scale.END_CROP).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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


        ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCropped(Scale.CENTER_CROP).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapConfig = BitmapConfig(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapConfig).asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCropped(Scale.CENTER_CROP).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMapping() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                expected = "AndroidBitmap(1291x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                expected = "AndroidBitmap(1291x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                expected = "AndroidBitmap(1936x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
                expected = "AndroidBitmap(1936x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val redMaskBitmapFinger: String
        val redMaskBitmapCorners: List<Int>
        val redMaskBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            .apply { mask(TestColor.withA(TestColor.RED, a = 100)) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                redMaskBitmapFinger = produceFingerPrint(this)
                redMaskBitmapCorners = corners()
            }

        val greenMaskBitmapFinger: String
        val greenMaskBitmapCorners: List<Int>
        val greenMaskBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            .apply { mask(TestColor.withA(TestColor.GREEN, a = 100)) }.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRotated() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val rotate90BitmapFinger: String
        val rotate90BitmapCorners: List<Int>
        val rotate90Bitmap = sourceBitmap.rotated(90).apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            rotate90BitmapFinger = produceFingerPrint(this)
            rotate90BitmapCorners = corners()
        }

        val rotate180BitmapFinger: String
        val rotate180BitmapCorners: List<Int>
        val rotate180Bitmap = sourceBitmap.rotated(180).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            rotate180BitmapFinger = produceFingerPrint(this)
            rotate180BitmapCorners = corners()
        }

        val rotate270BitmapFinger: String
        val rotate270BitmapCorners: List<Int>
        val rotate270Bitmap = sourceBitmap.rotated(270).apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                actual = toShortInfoString()
            )
            rotate270BitmapFinger = produceFingerPrint(this)
            rotate270BitmapCorners = corners()
        }

        val rotate360BitmapFinger: String
        val rotate360BitmapCorners: List<Int>
        val rotate360Bitmap = sourceBitmap.rotated(360).apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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


        ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotated(130).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapConfig = BitmapConfig(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapConfig).asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotated(130).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRoundedCornered() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap =
            ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                    actual = toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this)
                sourceBitmapCorners = corners()
            }

        val smallRoundedCorneredBitmapFinger: String
        val smallRoundedCorneredBitmapCorners: List<Int>
        val smallRoundedCorneredBitmap =
            sourceBitmap.roundedCornered(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
                .apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                        actual = toShortInfoString()
                    )
                    smallRoundedCorneredBitmapFinger = produceFingerPrint(this)
                    smallRoundedCorneredBitmapCorners = corners()
                }

        val bigRoundedCorneredBitmapFinger: String
        val bigRoundedCorneredBitmapCorners: List<Int>
        val bigRoundedCorneredBitmap =
            sourceBitmap.roundedCornered(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f))
                .apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
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


        ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCornered(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapConfig = BitmapConfig(Bitmap.Config.RGB_565)
        ResourceImages.jpeg.decode(bitmapConfig).asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(expected = Bitmap.Config.RGB_565, actual = config)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCornered(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = Bitmap.Config.ARGB_8888, actual = config)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    fun testScaled() {
        val bitmap = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().bitmap.apply {
            assertEquals(
                "AndroidBitmap(1291x1936,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                toShortInfoString()
            )
        }
        bitmap.scaled(1.5f).apply {
            assertEquals(
                "AndroidBitmap(1937x2904,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                toShortInfoString()
            )
        }
        bitmap.scaled(0.5f).apply {
            assertEquals(
                "AndroidBitmap(646x968,ARGB_8888${shortInfoColorSpaceName("SRGB")})",
                toShortInfoString()
            )
        }
    }
}