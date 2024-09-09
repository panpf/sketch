package com.github.panpf.sketch.core.android.test.util

import android.graphics.Bitmap
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.circleCrop
import com.github.panpf.sketch.util.isImmutable
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.roundedCorners
import com.github.panpf.sketch.util.toShortInfoString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ImagesAndroidTest {

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBlur() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceImage = ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(750x719,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this.bitmap)
            sourceBitmapCorners = corners()
        }

        val blur1BitmapFinger: String
        val blur1BitmapCorners: List<Int>
        val blur1Bitmap =
            sourceImage.blur(radius = 15, hasAlphaBitmapBgColor = null, maskColor = null)
                .asOrThrow<AndroidBitmapImage>().apply {
                    assertEquals(
                        expected = "AndroidBitmap(750x719,ARGB_8888)",
                        actual = bitmap.toShortInfoString()
                    )
                    blur1BitmapFinger = produceFingerPrint(this.bitmap)
                    blur1BitmapCorners = corners()
                }

        val blur2BitmapFinger: String
        val blur2BitmapCorners: List<Int>
        val blur2Bitmap =
            sourceImage.blur(radius = 15, hasAlphaBitmapBgColor = TestColor.GREEN, maskColor = null)
                .asOrThrow<AndroidBitmapImage>().apply {
                    assertEquals(
                        expected = "AndroidBitmap(750x719,ARGB_8888)",
                        actual = bitmap.toShortInfoString()
                    )
                    blur2BitmapFinger = produceFingerPrint(this.bitmap)
                    blur2BitmapCorners = corners()
                }

        val blur3BitmapFinger: String
        val blur3BitmapCorners: List<Int>
        val blur3Bitmap =
            sourceImage.blur(
                radius = 15,
                hasAlphaBitmapBgColor = TestColor.GREEN,
                maskColor = TestColor.RED
            ).asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(750x719,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                blur3BitmapFinger = produceFingerPrint(this.bitmap)
                blur3BitmapCorners = corners()
            }

        assertEquals(expected = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertEquals(expected = listOf(0, 0, 0, 0), actual = blur1BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = blur2BitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = blur3BitmapCorners)

        assertEquals(expected = sourceBitmapCorners, actual = blur1BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = blur2BitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = blur3BitmapCorners)
        assertNotEquals(illegal = blur1BitmapCorners, actual = blur2BitmapCorners)
        assertNotEquals(illegal = blur1BitmapCorners, actual = blur3BitmapCorners)
        assertNotEquals(illegal = blur2BitmapCorners, actual = blur3BitmapCorners)

        assertNotEquals(illegal = sourceBitmapFinger, actual = blur1BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = blur2BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = blur3BitmapFinger)
        assertNotEquals(illegal = blur1BitmapFinger, actual = blur2BitmapFinger)
        assertNotEquals(illegal = blur1BitmapFinger, actual = blur3BitmapFinger)
        assertNotEquals(illegal = blur2BitmapFinger, actual = blur3BitmapFinger)
    }

    @Test
    fun testBlur2() {
        val mutableSourceImage = ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>().let {
            it.copy(it.bitmap.copy(/* config = */ Bitmap.Config.ARGB_8888, /* isMutable = */ true))
        }
        assertFalse(mutableSourceImage.bitmap.isImmutable)

        // mutable, hasAlphaBitmapBgColor null, firstReuseSelf false
        mutableSourceImage.blur(20, hasAlphaBitmapBgColor = null, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = mutableSourceImage.bitmap, actual = this.bitmap)
            }
        // mutable, hasAlphaBitmapBgColor null, firstReuseSelf true
        mutableSourceImage.blur(20, hasAlphaBitmapBgColor = null, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertSame(expected = mutableSourceImage.bitmap, actual = this.bitmap)
            }
        // mutable, hasAlphaBitmapBgColor not null, firstReuseSelf false
        mutableSourceImage.blur(20, hasAlphaBitmapBgColor = TestColor.RED, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = mutableSourceImage.bitmap, actual = this.bitmap)
            }
        // mutable, hasAlphaBitmapBgColor not null, firstReuseSelf true
        mutableSourceImage.blur(20, hasAlphaBitmapBgColor = TestColor.RED, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = mutableSourceImage.bitmap, actual = this.bitmap)
            }

        val immutableSourceImage = ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>()
        assertTrue(immutableSourceImage.bitmap.isImmutable)

        // immutable, hasAlphaBitmapBgColor null, firstReuseSelf false
        immutableSourceImage.blur(20, hasAlphaBitmapBgColor = null, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
        // immutable, hasAlphaBitmapBgColor null, firstReuseSelf true
        immutableSourceImage.blur(20, hasAlphaBitmapBgColor = null, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
        // immutable, hasAlphaBitmapBgColor not null, firstReuseSelf false
        immutableSourceImage.blur(20, hasAlphaBitmapBgColor = TestColor.RED, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
        // immutable, hasAlphaBitmapBgColor not null, firstReuseSelf true
        immutableSourceImage.blur(20, hasAlphaBitmapBgColor = TestColor.RED, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testCircleCrop() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceImage = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this.bitmap)
            sourceBitmapCorners = corners()
        }

        val startCropBitmapFinger: String
        val startCropBitmapCorners: List<Int>
        val startCropBitmap =
            sourceImage.circleCrop(Scale.START_CROP).asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                startCropBitmapFinger = produceFingerPrint(this.bitmap)
                startCropBitmapCorners = corners()
            }

        val centerCropBitmapFinger: String
        val centerCropBitmapCorners: List<Int>
        val centerCropBitmap =
            sourceImage.circleCrop(Scale.CENTER_CROP).asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                centerCropBitmapFinger = produceFingerPrint(this.bitmap)
                centerCropBitmapCorners = corners()
            }

        val endCropBitmapFinger: String
        val endCropBitmapCorners: List<Int>
        val endCropBitmap =
            sourceImage.circleCrop(Scale.END_CROP).asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1291,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                endCropBitmapFinger = produceFingerPrint(this.bitmap)
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

        assertNotEquals(illegal = sourceBitmapFinger, actual = startCropBitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = centerCropBitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = endCropBitmapFinger)
        assertNotEquals(illegal = startCropBitmapFinger, actual = centerCropBitmapFinger)
        assertNotEquals(illegal = startCropBitmapFinger, actual = endCropBitmapFinger)
        assertNotEquals(illegal = centerCropBitmapFinger, actual = endCropBitmapFinger)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMask() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceImage = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this.bitmap)
            sourceBitmapCorners = corners()
        }

        val redMaskBitmapFinger: String
        val redMaskBitmapCorners: List<Int>
        val redMaskBitmap = sourceImage.mask(TestColor.withA(TestColor.RED, a = 100))
            .asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                redMaskBitmapFinger = produceFingerPrint(this.bitmap)
                redMaskBitmapCorners = corners()
            }

        val greenMaskBitmapFinger: String
        val greenMaskBitmapCorners: List<Int>
        val greenMaskBitmap = sourceImage.mask(TestColor.withA(TestColor.GREEN, a = 100))
            .asOrThrow<AndroidBitmapImage>().apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                greenMaskBitmapFinger = produceFingerPrint(this.bitmap)
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
    fun testMask2() {
        val mutableSourceImage = ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>().let {
            it.copy(it.bitmap.copy(/* config = */ Bitmap.Config.ARGB_8888, /* isMutable = */ true))
        }
        assertFalse(mutableSourceImage.bitmap.isImmutable)

        // mutable, hasAlphaBitmapBgColor null, firstReuseSelf false
        mutableSourceImage.mask(TestColor.RED, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = mutableSourceImage.bitmap, actual = this.bitmap)
            }
        // mutable, hasAlphaBitmapBgColor null, firstReuseSelf true
        mutableSourceImage.mask(TestColor.RED, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertSame(expected = mutableSourceImage.bitmap, actual = this.bitmap)
            }

        val immutableSourceImage = ResourceImages.png.decode().asOrThrow<AndroidBitmapImage>()
        assertTrue(immutableSourceImage.bitmap.isImmutable)

        // immutable, hasAlphaBitmapBgColor null, firstReuseSelf false
        immutableSourceImage.mask(TestColor.RED, firstReuseSelf = false)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
        // immutable, hasAlphaBitmapBgColor null, firstReuseSelf true
        immutableSourceImage.mask(TestColor.RED, firstReuseSelf = true)
            .asOrThrow<AndroidBitmapImage>().apply {
                assertNotSame(illegal = immutableSourceImage.bitmap, actual = this.bitmap)
            }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRoundedCorners() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceImage = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            sourceBitmapFinger = produceFingerPrint(this.bitmap)
            sourceBitmapCorners = corners()
        }

        val smallRoundedCorneredBitmapFinger: String
        val smallRoundedCorneredBitmapCorners: List<Int>
        val smallRoundedCorneredBitmap =
            sourceImage.roundedCorners(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
                .asOrThrow<AndroidBitmapImage>().apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                        actual = bitmap.toShortInfoString()
                    )
                    smallRoundedCorneredBitmapFinger = produceFingerPrint(this.bitmap)
                    smallRoundedCorneredBitmapCorners = corners()
                }

        val bigRoundedCorneredBitmapFinger: String
        val bigRoundedCorneredBitmapCorners: List<Int>
        val bigRoundedCorneredBitmap =
            sourceImage.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f))
                .asOrThrow<AndroidBitmapImage>().apply {
                    assertEquals(
                        expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                        actual = bitmap.toShortInfoString()
                    )
                    bigRoundedCorneredBitmapFinger = produceFingerPrint(this.bitmap)
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
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testRotate() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceImage = ResourceImages.jpeg.decode().asOrThrow<AndroidBitmapImage>()
            .apply {
                assertEquals(
                    expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                    actual = bitmap.toShortInfoString()
                )
                sourceBitmapFinger = produceFingerPrint(this.bitmap)
                sourceBitmapCorners = corners()
            }

        val rotate90BitmapFinger: String
        val rotate90BitmapCorners: List<Int>
        val rotate90Bitmap = sourceImage.rotate(90).asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            rotate90BitmapFinger = produceFingerPrint(this.bitmap)
            rotate90BitmapCorners = corners()
        }

        val rotate180BitmapFinger: String
        val rotate180BitmapCorners: List<Int>
        val rotate180Bitmap = sourceImage.rotate(180).asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            rotate180BitmapFinger = produceFingerPrint(this.bitmap)
            rotate180BitmapCorners = corners()
        }

        val rotate270BitmapFinger: String
        val rotate270BitmapCorners: List<Int>
        val rotate270Bitmap = sourceImage.rotate(270).asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1936x1291,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            rotate270BitmapFinger = produceFingerPrint(this.bitmap)
            rotate270BitmapCorners = corners()
        }

        val rotate360BitmapFinger: String
        val rotate360BitmapCorners: List<Int>
        val rotate360Bitmap = sourceImage.rotate(360).asOrThrow<AndroidBitmapImage>().apply {
            assertEquals(
                expected = "AndroidBitmap(1291x1936,ARGB_8888)",
                actual = bitmap.toShortInfoString()
            )
            rotate360BitmapFinger = produceFingerPrint(this.bitmap)
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

        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate90BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate180BitmapFinger)
        assertNotEquals(illegal = sourceBitmapFinger, actual = rotate270BitmapFinger)
        assertEquals(expected = sourceBitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate180BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate270BitmapFinger)
        assertNotEquals(illegal = rotate90BitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate180BitmapFinger, actual = rotate270BitmapFinger)
        assertNotEquals(illegal = rotate180BitmapFinger, actual = rotate360BitmapFinger)
        assertNotEquals(illegal = rotate270BitmapFinger, actual = rotate360BitmapFinger)
    }
}