package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.test.utils.SizeColorPainter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PainterWrapperTest {

    @Test
    fun testIntrinsicSize() {
        PainterWrapper(ColorPainter(Color.Red)).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }

        PainterWrapper(SizeColorPainter(Color.Red, Size(100f, 202f))).apply {
            assertEquals(Size(100f, 202f), intrinsicSize)
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PainterWrapper(ColorPainter(Color.Red))
        val element11 = PainterWrapper(ColorPainter(Color.Red))
        val element2 = PainterWrapper(ColorPainter(Color.Green))

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = element2)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
        assertNotEquals(illegal = element1.hashCode(), actual = element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "PainterWrapper(painter=ColorPainter(color=-65536))",
            actual = PainterWrapper(ColorPainter(Color.Red)).toString()
        )
    }
}