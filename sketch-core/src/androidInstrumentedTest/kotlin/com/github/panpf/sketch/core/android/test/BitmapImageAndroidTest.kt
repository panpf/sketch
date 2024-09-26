package com.github.panpf.sketch.core.android.test

import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.util.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BitmapImageAndroidTest {

    @Test
    fun testAsImage() {
        val sourceBitmap = AndroidBitmap(100, 100)
        sourceBitmap.asImage().apply {
            assertSame(expected = sourceBitmap, actual = bitmap)
            assertTrue(actual = shareable)
        }
        sourceBitmap.asImage(shareable = false).apply {
            assertSame(expected = sourceBitmap, actual = bitmap)
            assertFalse(actual = shareable)
        }
    }

    @Test
    fun testConstructor() {
        val bitmap = AndroidBitmap(100, 100)
        BitmapImage(bitmap).apply {
            assertSame(expected = bitmap, actual = bitmap)
            assertTrue(actual = shareable)
        }
        BitmapImage(bitmap, shareable = false).apply {
            assertSame(expected = bitmap, actual = bitmap)
            assertFalse(actual = shareable)
        }
    }

    @Test
    fun testWidthHeight() {
        BitmapImage(AndroidBitmap(100, 200)).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
        }
        BitmapImage(AndroidBitmap(200, 100)).apply {
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 100, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        BitmapImage(AndroidBitmap(100, 200)).apply {
            assertEquals(expected = 100 * 200 * 4L, actual = byteCount)
        }
        BitmapImage(AndroidBitmap(200, 300)).apply {
            assertEquals(expected = 200 * 300 * 4L, actual = byteCount)
        }
    }

    @Test
    fun testCheckValid() {
        BitmapImage(AndroidBitmap(100, 200)).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testToString() {
        val bitmap = AndroidBitmap(100, 200)
        assertEquals(
            expected = "BitmapImage(bitmap=${bitmap.toLogString()}, shareable=true)",
            actual = BitmapImage(bitmap).toString()
        )
    }
}