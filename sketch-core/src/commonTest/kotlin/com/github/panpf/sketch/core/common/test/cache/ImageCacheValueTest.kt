package com.github.panpf.sketch.core.common.test.cache

import com.github.panpf.sketch.cache.ImageCacheValue
import com.github.panpf.sketch.test.utils.FakeImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ImageCacheValueTest {

    @Test
    fun testConstructor() {
        ImageCacheValue(FakeImage(100, 200)).apply {
            assertEquals(FakeImage(100, 200), image)
            assertEquals(null, extras)
        }
        ImageCacheValue(FakeImage(100, 200), extras = mapOf("key" to "value")).apply {
            assertEquals(FakeImage(100, 200), image)
            assertEquals(mapOf("key" to "value"), extras)
        }
    }

    @Test
    fun testSize() {
        val image = FakeImage(100, 200)
        ImageCacheValue(image).apply {
            assertEquals(100 * 200 * 4L, size)
        }
    }

    @Test
    fun testCheckValid() {
        ImageCacheValue(FakeImage(100, 200)).apply {
            assertTrue(checkValid())
        }
        ImageCacheValue(FakeImage(100, 200, valid = false)).apply {
            assertFalse(checkValid())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ImageCacheValue(FakeImage(100, 200))
        val element11 = element1.copy()
        val element2 = element1.copy(image = FakeImage(200, 100))
        val element3 = element1.copy(extras = mapOf("key" to "value"))

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = element3)
        assertNotEquals(illegal = element2, actual = element3)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element3.hashCode())
        assertNotEquals(illegal = element2.hashCode(), actual = element3.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ImageCacheValue(image=FakeImage(size=100x200), extras={key=value})",
            actual = ImageCacheValue(
                image = FakeImage(width = 100, height = 200),
                extras = mapOf("key" to "value")
            ).toString()
        )
    }
}