package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.byteCount
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.isMutable
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.util.Size
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapNonAndroidTest {

    @Test
    fun testBitmapTypealias() {
        assertEquals(
            expected = org.jetbrains.skia.Bitmap::class,
            actual = com.github.panpf.sketch.Bitmap::class
        )
    }

    @Test
    fun testWidth() {
        assertEquals(expected = 100, actual = createBitmap(100, 200).width)
        assertEquals(expected = 200, actual = createBitmap(200, 100).width)
    }

    @Test
    fun testHeight() {
        assertEquals(expected = 200, actual = createBitmap(100, 200).height)
        assertEquals(expected = 100, actual = createBitmap(200, 100).height)
    }

    @Test
    fun testSize() {
        assertEquals(expected = Size(100, 200), actual = createBitmap(100, 200).size)
        assertEquals(expected = Size(200, 100), actual = createBitmap(200, 100).size)
    }

    @Test
    fun testByteCount() {
        assertEquals(
            expected = 80000,
            actual = createBitmap(100, 200, ColorType.RGBA_8888).byteCount
        )
        assertEquals(expected = 40000, actual = createBitmap(200, 100, ColorType.RGB_565).byteCount)
    }

    @Test
    fun testIsMutable() {
        assertTrue(ResourceImages.jpeg.decode().bitmap.isMutable)
        assertFalse(ResourceImages.jpeg.decode().bitmap.apply { setImmutable() }.isMutable)
    }

    @Test
    fun testIsImmutable() {
        assertFalse(ResourceImages.jpeg.decode().bitmap.isImmutable)
        assertTrue(ResourceImages.jpeg.decode().bitmap.apply { setImmutable() }.isImmutable)
    }

    @Test
    fun testCreateBitmap() {
        Bitmap().apply {
            assertEquals(expected = 0, actual = rowBytes)
            assertEquals(expected = 0, actual = rowBytesAsPixels)
            assertEquals(expected = 0, actual = computeByteSize())
            assertEquals(expected = 0, actual = width)
            assertEquals(expected = 0, actual = height)
        }

        createBitmap(ImageInfo(100, 100, ColorType.RGB_565, ColorAlphaType.OPAQUE)).apply {
            assertEquals(expected = 200, actual = rowBytes)
            assertEquals(expected = 100, actual = rowBytesAsPixels)
            assertEquals(expected = 20000, actual = computeByteSize())
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 100, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
        }

        createBitmap(200, 200).apply {
            assertEquals(expected = 800, actual = rowBytes)
            assertEquals(expected = 200, actual = rowBytesAsPixels)
            assertEquals(expected = 160000, actual = computeByteSize())
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.N32, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
        }

        createBitmap(200, 200, ColorType.ARGB_4444, ColorAlphaType.PREMUL).apply {
            assertEquals(expected = 400, actual = rowBytes)
            assertEquals(expected = 200, actual = rowBytesAsPixels)
            assertEquals(expected = 80000, actual = computeByteSize())
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.ARGB_4444, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
        }
    }
}