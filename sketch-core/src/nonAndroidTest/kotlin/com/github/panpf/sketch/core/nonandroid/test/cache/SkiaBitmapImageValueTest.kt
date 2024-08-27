package com.github.panpf.sketch.core.nonandroid.test.cache

import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.cache.SkiaBitmapImageValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SkiaBitmapImageValueTest {

    @Test
    fun testSize() {
        val bitmap1 = SkiaBitmap().apply {
            allocN32Pixels(100, 100, opaque = false)
        }
        val bitmap2 = SkiaBitmap().apply {
            allocN32Pixels(200, 200, opaque = false)
        }

        val imageValue1 = SkiaBitmapImageValue(bitmap1.asSketchImage())
        val imageValue2 = SkiaBitmapImageValue(bitmap2.asSketchImage())

        assertEquals(
            expected = bitmap1.asSketchImage().byteCount,
            actual = imageValue1.size
        )
        assertEquals(
            expected = bitmap2.asSketchImage().byteCount,
            actual = imageValue2.size
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val bitmap1 = SkiaBitmap().apply {
            allocN32Pixels(100, 100, opaque = false)
        }
        val bitmap2 = SkiaBitmap().apply {
            allocN32Pixels(200, 200, opaque = false)
        }

        val imageValue1 = SkiaBitmapImageValue(bitmap1.asSketchImage())
        val imageValue12 = SkiaBitmapImageValue(bitmap1.asSketchImage())
        val imageValue2 = SkiaBitmapImageValue(bitmap2.asSketchImage())
        val imageValue3 =
            SkiaBitmapImageValue(bitmap1.asSketchImage(), mapOf("key" to "value"))

        assertEquals(expected = imageValue1, actual = imageValue1)
        assertEquals(expected = imageValue1, actual = imageValue12)
        assertNotEquals(illegal = imageValue1, actual = null as Any?)
        assertNotEquals(illegal = imageValue1, actual = Any())
        assertNotEquals(illegal = imageValue1, actual = imageValue2)
        assertNotEquals(illegal = imageValue1, actual = imageValue3)
        assertNotEquals(illegal = imageValue2, actual = imageValue3)

        assertEquals(
            expected = imageValue1.hashCode(),
            actual = imageValue12.hashCode()
        )
        assertNotEquals(
            illegal = imageValue1.hashCode(),
            actual = imageValue2.hashCode()
        )
        assertNotEquals(
            illegal = imageValue1.hashCode(),
            actual = imageValue3.hashCode()
        )
        assertNotEquals(
            illegal = imageValue2.hashCode(),
            actual = imageValue3.hashCode()
        )
    }

    @Test
    fun testToString() {
        val bitmap1 = SkiaBitmap().apply {
            allocN32Pixels(100, 100, opaque = false)
        }
        val bitmap2 = SkiaBitmap().apply {
            allocN32Pixels(200, 200, opaque = false)
        }

        val imageValue1 = SkiaBitmapImageValue(bitmap1.asSketchImage())
        val imageValue2 =
            SkiaBitmapImageValue(bitmap2.asSketchImage(), mapOf("key" to "value"))

        assertEquals(
            expected = "SkiaBitmapImageValue(image=${bitmap1.asSketchImage()}, extras=null)",
            actual = imageValue1.toString()
        )
        assertEquals(
            expected = "SkiaBitmapImageValue(image=${bitmap2.asSketchImage()}, extras=${mapOf("key" to "value")})",
            actual = imageValue2.toString()
        )
    }
}