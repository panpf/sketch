package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.util.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BitmapImageNonAndroidTest {

    @Test
    fun testAsImage() {
        val skiaBitmap = SkiaBitmap(100, 100)
        assertEquals(
            expected = BitmapImage(skiaBitmap),
            actual = skiaBitmap.asImage()
        )
    }

    @Test
    fun testConstructor() {
        val mutableBitmap = SkiaBitmap(100, 100)
        assertFalse(mutableBitmap.isImmutable)
        BitmapImage(mutableBitmap).apply {
            assertSame(expected = mutableBitmap, actual = bitmap)
            assertFalse(actual = shareable)
        }
        BitmapImage(mutableBitmap, shareable = true).apply {
            assertSame(expected = mutableBitmap, actual = bitmap)
            assertTrue(actual = shareable)
        }

        val immutableBitmap = ResourceImages.jpeg.decode().bitmap.apply {
            setImmutable()
        }
        assertTrue(immutableBitmap.isImmutable)
        BitmapImage(immutableBitmap).apply {
            assertSame(expected = immutableBitmap, actual = bitmap)
            assertTrue(actual = shareable)
        }
    }

    @Test
    fun testWidthHeight() {
        BitmapImage(SkiaBitmap(100, 200)).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
        }
        BitmapImage(SkiaBitmap(200, 100)).apply {
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 100, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        BitmapImage(SkiaBitmap(100, 200)).apply {
            assertEquals(expected = 100 * 200 * 4L, actual = byteCount)
        }
        BitmapImage(SkiaBitmap(200, 300)).apply {
            assertEquals(expected = 200 * 300 * 4L, actual = byteCount)
        }
    }

    @Test
    fun testCacheValue() {
        val bitmap = SkiaBitmap(100, 200)
        BitmapImage(bitmap).apply {
            assertTrue(actual = cachedInMemory)
        }
        BitmapImage(bitmap, cachedInMemory = false).apply {
            assertFalse(actual = cachedInMemory)
        }
    }

    @Test
    fun testCachedInMemory() {
        BitmapImage(SkiaBitmap(100, 200)).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testToString() {
        val bitmap = SkiaBitmap(100, 200)
        assertEquals(
            "BitmapImage(bitmap=${bitmap.toLogString()}, shareable=false)",
            BitmapImage(bitmap).toString()
        )

        val bitmap2 = SkiaBitmap(200, 100)
        assertEquals(
            "BitmapImage(bitmap=${bitmap2.toLogString()}, shareable=false)",
            BitmapImage(bitmap2, shareable = false).toString()
        )
    }
}