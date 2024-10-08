package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import com.github.panpf.sketch.painter.ComposeBitmapPainter
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.toLogString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ComposeBitmapPainterTest {

    @Test
    fun testComposeBitmapAsPainter() {
        val composeBitmap = createBitmap(100, 100).toComposeBitmap()
        assertEquals(
            expected = ComposeBitmapPainter(composeBitmap),
            actual = composeBitmap.asPainter()
        )
    }

    @Test
    fun testIntrinsicSize() {
        val composeBitmap = createBitmap(101, 202).toComposeBitmap()
        val composeBitmapPainter = ComposeBitmapPainter(composeBitmap)
        assertEquals(
            expected = Size(101f, 202f),
            actual = composeBitmapPainter.intrinsicSize
        )
    }

    @Test
    fun testOnDraw() {
        // TODO test: Draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() {
        val composeBitmap1 = createBitmap(101, 202).toComposeBitmap()
        val composeBitmap2 = createBitmap(101, 202).toComposeBitmap()
        val element1 = ComposeBitmapPainter(composeBitmap1)
        val element11 = ComposeBitmapPainter(composeBitmap1)
        val element2 = ComposeBitmapPainter(composeBitmap2)

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
    }

    @Test
    fun testToString() {
        val composeBitmap = createBitmap(101, 202).toComposeBitmap()
        val element = ComposeBitmapPainter(composeBitmap)
        assertEquals(
            expected = "ComposeBitmapPainter(bitmap=${composeBitmap.toLogString()})",
            actual = element.toString()
        )
    }
}