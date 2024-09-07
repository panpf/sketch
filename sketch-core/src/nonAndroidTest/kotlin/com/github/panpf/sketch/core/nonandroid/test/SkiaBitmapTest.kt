package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImageInfo
import org.jetbrains.skia.ColorInfo
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
        SkiaBitmap(SkiaImageInfo(ColorInfo.DEFAULT, 100, 100)).apply {
            assertEquals(expected = 0, actual = rowBytes)
            assertEquals(expected = 0, actual = rowBytesAsPixels)
            assertEquals(expected = 0, actual = computeByteSize())
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 100, actual = height)
        }
    }
}