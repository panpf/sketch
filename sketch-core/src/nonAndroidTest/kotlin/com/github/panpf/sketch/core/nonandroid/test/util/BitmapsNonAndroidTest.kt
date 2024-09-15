package com.github.panpf.sketch.core.nonandroid.test.util

import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.Offset
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.hammingDistance
import com.github.panpf.sketch.test.utils.produceFingerPrint
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.background
import com.github.panpf.sketch.util.blur
import com.github.panpf.sketch.util.circleCrop
import com.github.panpf.sketch.util.copyWith
import com.github.panpf.sketch.util.flip
import com.github.panpf.sketch.util.hasAlphaPixels
import com.github.panpf.sketch.util.mapping
import com.github.panpf.sketch.util.mask
import com.github.panpf.sketch.util.mutableCopy
import com.github.panpf.sketch.util.mutableCopyOrSelf
import com.github.panpf.sketch.util.readIntPixel
import com.github.panpf.sketch.util.readIntPixels
import com.github.panpf.sketch.util.rotate
import com.github.panpf.sketch.util.roundedCorners
import com.github.panpf.sketch.util.scale
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toShortInfoString
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BitmapsNonAndroidTest {

    @Test
    fun testToLogString() {
        val jpegBitmap = ResourceImages.jpeg.decode().bitmap
        assertEquals(
            expected = "Bitmap@${jpegBitmap.toHexString()}(1291x1936,RGBA_8888,sRGB)",
            actual = jpegBitmap.toLogString()
        )
        val pngBitmap = ResourceImages.png.decode().bitmap
        assertEquals(
            expected = "Bitmap@${pngBitmap.toHexString()}(750x719,RGBA_8888,sRGB)",
            actual = pngBitmap.toLogString()
        )
    }

    @Test
    fun testToInfoString() {
        assertEquals(
            expected = "Bitmap(width=1291, height=1936, colorType=RGBA_8888, colorSpace=sRGB)",
            actual = ResourceImages.jpeg.decode().bitmap.toInfoString()
        )
        assertEquals(
            expected = "Bitmap(width=750, height=719, colorType=RGBA_8888, colorSpace=sRGB)",
            actual = ResourceImages.png.decode().bitmap.toInfoString()
        )
    }

    @Test
    fun testToShortInfoString() {
        assertEquals(
            expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
            actual = ResourceImages.jpeg.decode().bitmap.toShortInfoString()
        )
        assertEquals(
            expected = "Bitmap(750x719,RGBA_8888,sRGB)",
            actual = ResourceImages.png.decode().bitmap.toShortInfoString()
        )
    }

    @Test
    fun testMutableCopy() {
        val mutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        val copiedMutableBitmap = mutableBitmap.mutableCopy().apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        assertEquals(expected = mutableBitmap.corners(), actual = copiedMutableBitmap.corners())
        assertEquals(expected = 0, actual = mutableBitmap.similarity(copiedMutableBitmap))

        val immutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            setImmutable()
            assertTrue(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        val copiedImmutableBitmap = immutableBitmap.mutableCopy().apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        assertEquals(expected = immutableBitmap.corners(), actual = copiedImmutableBitmap.corners())
        assertEquals(expected = 0, actual = immutableBitmap.similarity(copiedImmutableBitmap))
    }

    @Test
    fun testMutableCopyOrSelf() {
        val mutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        val copiedMutableBitmap = mutableBitmap.mutableCopyOrSelf().apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        assertEquals(expected = mutableBitmap.corners(), actual = copiedMutableBitmap.corners())
        assertEquals(expected = 0, actual = mutableBitmap.similarity(copiedMutableBitmap))
        assertSame(expected = mutableBitmap, actual = copiedMutableBitmap)

        val immutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            setImmutable()
            assertTrue(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        val copiedImmutableBitmap = immutableBitmap.mutableCopyOrSelf().apply {
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertEquals(expected = ColorSpace.sRGB, actual = colorSpace)
        }
        assertEquals(expected = immutableBitmap.corners(), actual = copiedImmutableBitmap.corners())
        assertEquals(expected = 0, actual = immutableBitmap.similarity(copiedImmutableBitmap))
        assertNotSame(illegal = immutableBitmap, actual = copiedImmutableBitmap)
    }

    @Test
    fun testCopyWith() {
        val mutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        val copiedMutableBitmap = mutableBitmap.copyWith().apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        val copiedWithColorInfoMutableBitmap = mutableBitmap.copyWith(
            mutableBitmap.imageInfo.colorInfo
                .withColorType(ColorType.RGB_565)
                .withColorSpace(ColorSpace.sRGBLinear)
        ).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGB_565,sRGBLinear)",
                actual = toShortInfoString()
            )
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        assertEquals(expected = mutableBitmap.corners(), actual = copiedMutableBitmap.corners())
        assertEquals(expected = 0, actual = mutableBitmap.similarity(copiedMutableBitmap))
        assertNotSame(illegal = mutableBitmap, actual = copiedMutableBitmap)
        assertNotEquals(
            illegal = mutableBitmap.corners(),
            actual = copiedWithColorInfoMutableBitmap.corners()
        )
        assertEquals(
            expected = 1,
            actual = mutableBitmap.similarity(copiedWithColorInfoMutableBitmap)
        )
        assertNotSame(illegal = mutableBitmap, actual = copiedWithColorInfoMutableBitmap)

        val immutableBitmap = ResourceImages.jpeg.decode().bitmap.apply { setImmutable() }.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            assertTrue(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        val copiedImmutableBitmap = immutableBitmap.copyWith().apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        val copiedWithColorInfoImmutableBitmap = immutableBitmap.copyWith(
            immutableBitmap.imageInfo.colorInfo
                .withColorType(ColorType.RGB_565)
                .withColorSpace(ColorSpace.sRGBLinear)
        ).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGB_565,sRGBLinear)",
                actual = toShortInfoString()
            )
            assertFalse(isImmutable)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }
        assertEquals(expected = immutableBitmap.corners(), actual = copiedImmutableBitmap.corners())
        assertEquals(expected = 0, actual = immutableBitmap.similarity(copiedImmutableBitmap))
        assertNotSame(illegal = immutableBitmap, actual = copiedImmutableBitmap)
        assertNotEquals(
            illegal = immutableBitmap.corners(),
            actual = copiedWithColorInfoImmutableBitmap.corners()
        )
        assertEquals(
            expected = 1,
            actual = immutableBitmap.similarity(copiedWithColorInfoImmutableBitmap)
        )
        assertNotSame(illegal = immutableBitmap, actual = copiedWithColorInfoImmutableBitmap)
    }

    @Test
    fun testHasAlphaPixels() {
        assertFalse(
            actual = ResourceImages.jpeg.decode().bitmap.hasAlphaPixels()
        )
        assertTrue(
            actual = ResourceImages.png.decode().bitmap.hasAlphaPixels()
        )
    }

    @Test
    fun testReadIntPixels() {
//        val sourceBitmap = ResourceImages.jpeg.decode2().bitmap.apply {
//            assertEquals(
//                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
//                actual = toShortInfoString()
//            )
//        }
//
//        val bytePixels = sourceBitmap.readPixels()!!
//        assertEquals(
//            expected = sourceBitmap.rowBytes * sourceBitmap.height,
//            actual = bytePixels.size
//        )
//
//        assertEquals(
//            expected = sourceBitmap.width * sourceBitmap.imageInfo.colorType.bytesPerPixel,
//            actual = sourceBitmap.rowBytes
//        )
//
//        val intPixels = sourceBitmap.readIntPixels()!!
//        assertEquals(
//            expected = sourceBitmap.width * sourceBitmap.height,
//            actual = intPixels.size
//        )
        // TODO test all colorTypes
    }

    @Test
    fun testInstallIntPixels() {
        // TODO test
    }

    @Test
    fun testReadIntPixel() {
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
        }
        val intPixels = sourceBitmap.readIntPixels()

        val topLeftPixel = Offset(
            x = (sourceBitmap.width * 0.25f).roundToInt(),
            y = (sourceBitmap.height * 0.25f).roundToInt()
        )
        assertEquals(
            expected = intPixels[((topLeftPixel.y) * sourceBitmap.width) + topLeftPixel.x],
            actual = sourceBitmap.readIntPixel(topLeftPixel.x, topLeftPixel.y)
        )

        val topRightPixel = Offset(
            x = (sourceBitmap.width * 0.75f).roundToInt(),
            y = (sourceBitmap.height * 0.25f).roundToInt()
        )
        assertEquals(
            expected = intPixels[((topRightPixel.y) * sourceBitmap.width) + topRightPixel.x],
            actual = sourceBitmap.readIntPixel(topRightPixel.x, topRightPixel.y)
        )

        val bottomLeftPixel = Offset(
            x = (sourceBitmap.width * 0.25f).roundToInt(),
            y = (sourceBitmap.height * 0.75f).roundToInt()
        )
        assertEquals(
            expected = intPixels[((bottomLeftPixel.y) * sourceBitmap.width) + bottomLeftPixel.x],
            actual = sourceBitmap.readIntPixel(bottomLeftPixel.x, bottomLeftPixel.y)
        )

        val bottomRightPixel = Offset(
            x = (sourceBitmap.width * 0.75f).roundToInt(),
            y = (sourceBitmap.height * 0.75f).roundToInt()
        )
        assertEquals(
            expected = intPixels[((bottomRightPixel.y) * sourceBitmap.width) + bottomRightPixel.x],
            actual = sourceBitmap.readIntPixel(bottomRightPixel.x, bottomRightPixel.y)
        )

        val centerPixel = Offset(
            x = (sourceBitmap.width * 0.5f).roundToInt(),
            y = (sourceBitmap.height * 0.5f).roundToInt()
        )
        assertEquals(
            expected = intPixels[((centerPixel.y) * sourceBitmap.width) + centerPixel.x],
            actual = sourceBitmap.readIntPixel(centerPixel.x, centerPixel.y)
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testBackground() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.background(TestColor.RED).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = this.produceFingerPrint()
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.background(TestColor.BLUE).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            blueBgBitmapFinger = this.produceFingerPrint()
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
    fun testBackground2() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.png.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(750x719,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val redBgBitmapFinger: String
        val redBgBitmapCorners: List<Int>
        val redBgBitmap = sourceBitmap.background(TestColor.RED).apply {
            assertEquals(
                expected = "Bitmap(750x719,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            redBgBitmapFinger = this.produceFingerPrint()
            redBgBitmapCorners = corners()
        }

        val blueBgBitmapFinger: String
        val blueBgBitmapCorners: List<Int>
        val blueBgBitmap = sourceBitmap.background(TestColor.BLUE).apply {
            assertEquals(
                expected = "Bitmap(750x719,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            blueBgBitmapFinger = this.produceFingerPrint()
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
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val smallRadiusBlurBitmapFinger: String
        val smallRadiusBlurBitmapCorners: List<Int>
        val smallRadiusBlurBitmap = sourceBitmap.blur(20).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            smallRadiusBlurBitmapFinger = this.produceFingerPrint()
            smallRadiusBlurBitmapCorners = corners()
        }

        val bigRadiusBlurBitmapFinger: String
        val bigRadiusBlurBitmapCorners: List<Int>
        val bigRadiusBlurBitmap = sourceBitmap.blur(50).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            bigRadiusBlurBitmapFinger = this.produceFingerPrint()
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
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val startCropBitmapFinger: String
        val startCropBitmapCorners: List<Int>
        val startCropBitmap = sourceBitmap.circleCrop(Scale.START_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            startCropBitmapFinger = this.produceFingerPrint()
            startCropBitmapCorners = corners()
        }

        val centerCropBitmapFinger: String
        val centerCropBitmapCorners: List<Int>
        val centerCropBitmap = sourceBitmap.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            centerCropBitmapFinger = this.produceFingerPrint()
            centerCropBitmapCorners = corners()
        }

        val endCropBitmapFinger: String
        val endCropBitmapCorners: List<Int>
        val endCropBitmap = sourceBitmap.circleCrop(Scale.END_CROP).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            endCropBitmapFinger = this.produceFingerPrint()
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
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, centerCropBitmapFinger) >= 5,
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
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(ColorType.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.circleCrop(Scale.CENTER_CROP).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testFlip() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val horFlippedBitmapFinger: String
        val horFlippedBitmapCorners: List<Int>
        val horFlippedBitmap = sourceBitmap.flip(horizontal = true).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            horFlippedBitmapFinger = this.produceFingerPrint()
            horFlippedBitmapCorners = corners()
        }

        val verFlippedBitmapFinger: String
        val verFlippedBitmapCorners: List<Int>
        val verFlippedBitmap = sourceBitmap.flip(horizontal = false).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            verFlippedBitmapFinger = this.produceFingerPrint()
            verFlippedBitmapCorners = corners()
        }

        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = sourceBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = horFlippedBitmapCorners)
        assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = verFlippedBitmapCorners)

        assertNotEquals(illegal = sourceBitmapCorners, actual = horFlippedBitmapCorners)
        assertNotEquals(illegal = sourceBitmapCorners, actual = verFlippedBitmapCorners)
        assertNotEquals(illegal = horFlippedBitmapCorners, actual = verFlippedBitmapCorners)

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, horFlippedBitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, horFlippedBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, verFlippedBitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, verFlippedBitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(horFlippedBitmapFinger, verFlippedBitmapFinger) >= 5,
            message = hammingDistance(horFlippedBitmapFinger, verFlippedBitmapFinger).toString()
        )
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun testMapping() {
        val sourceBitmapFinger: String
        val sourceBitmapCorners: List<Int>
        val sourceBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val bigSize = sourceBitmap.size.let { max(it.width, it.height) }.let { Size(it, it) }
        val resize1 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.CENTER_CROP)
        val resize1Mapping = resize1.calculateMapping(sourceBitmap.size)
        val resize1BitmapFinger: String
        val resize1BitmapCorners: List<Int>
        val resize1Bitmap = sourceBitmap.mapping(resize1Mapping).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            resize1BitmapFinger = this.produceFingerPrint()
            resize1BitmapCorners = corners()
        }

        val resize2 = Resize(bigSize, Precision.SAME_ASPECT_RATIO, Scale.START_CROP)
        val resize2Mapping = resize2.calculateMapping(sourceBitmap.size)
        val resize2BitmapFinger: String
        val resize2BitmapCorners: List<Int>
        val resize2Bitmap = sourceBitmap.mapping(resize2Mapping).apply {
            assertEquals(
                expected = "Bitmap(1291x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            resize2BitmapFinger = this.produceFingerPrint()
            resize2BitmapCorners = corners()
        }

        val resize3 = Resize(bigSize, Precision.EXACTLY, Scale.CENTER_CROP)
        val resize3Mapping = resize3.calculateMapping(sourceBitmap.size)
        val resize3BitmapFinger: String
        val resize3BitmapCorners: List<Int>
        val resize3Bitmap = sourceBitmap.mapping(resize3Mapping).apply {
            assertEquals(
                expected = "Bitmap(1936x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            resize3BitmapFinger = this.produceFingerPrint()
            resize3BitmapCorners = corners()
        }

        val resize4 = Resize(bigSize, Precision.EXACTLY, Scale.START_CROP)
        val resize4Mapping = resize4.calculateMapping(sourceBitmap.size)
        val resize4BitmapFinger: String
        val resize4BitmapCorners: List<Int>
        val resize4Bitmap = sourceBitmap.mapping(resize4Mapping).apply {
            assertEquals(
                expected = "Bitmap(1936x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            resize4BitmapFinger = this.produceFingerPrint()
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

        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize1BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, resize1BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize2BitmapFinger) >= 5,
            message = hammingDistance(sourceBitmapFinger, resize2BitmapFinger).toString()
        )
        assertTrue(
            actual = hammingDistance(sourceBitmapFinger, resize3BitmapFinger) >= 5,
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
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val redMaskBitmapFinger: String
        val redMaskBitmapCorners: List<Int>
        val redMaskBitmap = sourceBitmap.mask(TestColor.withA(TestColor.RED, a = 100)).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            redMaskBitmapFinger = this.produceFingerPrint()
            redMaskBitmapCorners = corners()
        }

        val greenMaskBitmapFinger: String
        val greenMaskBitmapCorners: List<Int>
        val greenMaskBitmap = sourceBitmap.mask(TestColor.withA(TestColor.GREEN, a = 100)).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            greenMaskBitmapFinger = this.produceFingerPrint()
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
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val rotate90BitmapFinger: String
        val rotate90BitmapCorners: List<Int>
        val rotate90Bitmap = sourceBitmap.rotate(90).apply {
            assertEquals(
                expected = "Bitmap(1936x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            rotate90BitmapFinger = this.produceFingerPrint()
            rotate90BitmapCorners = corners()
        }

        val rotate180BitmapFinger: String
        val rotate180BitmapCorners: List<Int>
        val rotate180Bitmap = sourceBitmap.rotate(180).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            rotate180BitmapFinger = this.produceFingerPrint()
            rotate180BitmapCorners = corners()
        }

        val rotate270BitmapFinger: String
        val rotate270BitmapCorners: List<Int>
        val rotate270Bitmap = sourceBitmap.rotate(270).apply {
            assertEquals(
                expected = "Bitmap(1936x1291,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            rotate270BitmapFinger = this.produceFingerPrint()
            rotate270BitmapCorners = corners()
        }

        val rotate360BitmapFinger: String
        val rotate360BitmapCorners: List<Int>
        val rotate360Bitmap = sourceBitmap.rotate(360).apply {
            assertEquals(
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            rotate360BitmapFinger = this.produceFingerPrint()
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
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotate(130).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(ColorType.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.rotate(130).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
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
                expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                actual = toShortInfoString()
            )
            sourceBitmapFinger = this.produceFingerPrint()
            sourceBitmapCorners = corners()
        }

        val smallRoundedCorneredBitmapFinger: String
        val smallRoundedCorneredBitmapCorners: List<Int>
        val smallRoundedCorneredBitmap =
            sourceBitmap.roundedCorners(floatArrayOf(10f, 10f, 10f, 10f, 10f, 10f, 10f, 10f))
                .apply {
                    assertEquals(
                        expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                        actual = toShortInfoString()
                    )
                    smallRoundedCorneredBitmapFinger = this.produceFingerPrint()
                    smallRoundedCorneredBitmapCorners = corners()
                }

        val bigRoundedCorneredBitmapFinger: String
        val bigRoundedCorneredBitmapCorners: List<Int>
        val bigRoundedCorneredBitmap =
            sourceBitmap.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f))
                .apply {
                    assertEquals(
                        expected = "Bitmap(1291x1936,RGBA_8888,sRGB)",
                        actual = toShortInfoString()
                    )
                    bigRoundedCorneredBitmapFinger = this.produceFingerPrint()
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
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }

        val bitmapColorType = BitmapColorType(ColorType.RGB_565)
        ResourceImages.jpeg.decode(bitmapColorType).bitmap.apply {
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
            assertTrue(actual = isOpaque)
            assertNotEquals(illegal = listOf(0, 0, 0, 0), actual = corners())
        }.roundedCorners(floatArrayOf(20f, 20f, 20f, 20f, 20f, 20f, 20f, 20f)).apply {
            assertEquals(expected = ColorType.RGBA_8888, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
            assertFalse(actual = isOpaque)
            assertEquals(expected = listOf(0, 0, 0, 0), actual = corners())
        }
    }

    @Test
    fun testScale() {
        val bitmap = ResourceImages.jpeg.decode().bitmap.apply {
            assertEquals("Bitmap(1291x1936,RGBA_8888,sRGB)", toShortInfoString())
        }
        bitmap.scale(1.5f).apply {
            assertEquals("Bitmap(1937x2904,RGBA_8888,sRGB)", toShortInfoString())
        }
        bitmap.scale(0.5f).apply {
            assertEquals("Bitmap(646x968,RGBA_8888,sRGB)", toShortInfoString())
        }
    }

    @Test
    fun testThumbnail() {
        // TODO test
    }
}