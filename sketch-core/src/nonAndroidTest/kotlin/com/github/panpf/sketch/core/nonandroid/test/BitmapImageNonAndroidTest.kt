package com.github.panpf.sketch.core.nonandroid.test

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.util.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BitmapImageNonAndroidTest {

    @Test
    fun testAsImage() {
        val sourceBitmap = createBitmap(100, 100)
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
        val bitmap = createBitmap(100, 100)
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
        BitmapImage(createBitmap(100, 200)).apply {
            assertEquals(expected = 100, actual = width)
            assertEquals(expected = 200, actual = height)
        }
        BitmapImage(createBitmap(200, 100)).apply {
            assertEquals(expected = 200, actual = width)
            assertEquals(expected = 100, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        BitmapImage(createBitmap(100, 200)).apply {
            assertEquals(expected = 100 * 200 * 4L, actual = byteCount)
        }
        BitmapImage(createBitmap(200, 300)).apply {
            assertEquals(expected = 200 * 300 * 4L, actual = byteCount)
        }
    }

    @Test
    fun testCheckValid() {
        BitmapImage(createBitmap(100, 200)).apply {
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
            assertTrue(actual = checkValid())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BitmapImage(createBitmap(100, 200))
        val element11 = element1.copy()
        val element2 = element1.copy(createBitmap(200, 100))
        val element3 = element1.copy(shareable = false)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val bitmap = createBitmap(100, 200)
        assertEquals(
            expected = "BitmapImage(bitmap=${bitmap.toLogString()}, shareable=true)",
            actual = BitmapImage(bitmap).toString()
        )
    }
}