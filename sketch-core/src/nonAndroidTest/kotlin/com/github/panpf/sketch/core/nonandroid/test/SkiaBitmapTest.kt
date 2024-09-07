package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImageInfo
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals

class SkiaBitmapTest {

    @Test
    fun testSkiaBitmap() {
        assertEquals(
            expected = org.jetbrains.skia.Bitmap::class,
            actual = SkiaBitmap::class
        )

        SkiaBitmap().apply {
            assertEquals(expected = 0, actual = rowBytes)
            assertEquals(expected = 0, actual = rowBytesAsPixels)
            assertEquals(expected = 0, actual = computeByteSize())
            assertEquals(expected = 0, actual = width)
            assertEquals(expected = 0, actual = height)
        }

        SkiaBitmap(SkiaImageInfo(100, 100, ColorType.RGB_565, ColorAlphaType.OPAQUE)).apply {
            assertEquals(expected = 200, actual = rowBytes)
            assertEquals(expected = 100, actual = rowBytesAsPixels)
            assertEquals(expected = 20000, actual = computeByteSize())
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 100, actual = height)
            assertEquals(expected = ColorType.RGB_565, actual = colorType)
            assertEquals(expected = ColorAlphaType.OPAQUE, actual = alphaType)
        }

        SkiaBitmap(200, 200).apply {
            assertEquals(expected = 800, actual = rowBytes)
            assertEquals(expected = 200, actual = rowBytesAsPixels)
            assertEquals(expected = 160000, actual = computeByteSize())
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 200, actual = height)
            assertEquals(expected = ColorType.N32, actual = colorType)
            assertEquals(expected = ColorAlphaType.PREMUL, actual = alphaType)
        }

        SkiaBitmap(200, 200, ColorType.ARGB_4444, ColorAlphaType.PREMUL).apply {
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