package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.byteCount
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.isMutable
import com.github.panpf.sketch.test.utils.decode
import org.jetbrains.skia.ColorType.RGBA_8888
import org.jetbrains.skia.ColorType.RGB_565
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BitmapNonAndroidTest {

    @Test
    fun testBitmap() {
        assertEquals(
            expected = org.jetbrains.skia.Bitmap::class,
            actual = com.github.panpf.sketch.Bitmap::class
        )
    }

    @Test
    fun testWidth() {
        assertEquals(expected = 100, actual = SkiaBitmap(100, 200).width)
        assertEquals(expected = 200, actual = SkiaBitmap(200, 100).width)
    }

    @Test
    fun testHeight() {
        assertEquals(expected = 200, actual = SkiaBitmap(100, 200).height)
        assertEquals(expected = 100, actual = SkiaBitmap(200, 100).height)
    }

    @Test
    fun testByteCount() {
        assertEquals(expected = 80000, actual = SkiaBitmap(100, 200, RGBA_8888).byteCount)
        assertEquals(expected = 40000, actual = SkiaBitmap(200, 100, RGB_565).byteCount)
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
}