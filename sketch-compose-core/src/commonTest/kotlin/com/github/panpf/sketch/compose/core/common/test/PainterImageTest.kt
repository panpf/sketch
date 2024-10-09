package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.PainterImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PainterImageTest {

    @Test
    fun testAsImage() {
        ColorPainter(Color.Red).asEquitable().asImage().apply {
            assertEquals(
                expected = PainterImage(
                    painter = ColorPainter(color = Color.Red).asEquitable(),
                    shareable = true
                ),
                actual = this
            )
        }

        ColorPainter(Color.Red).asAnimatablePainter().asEquitableWithThis().asImage().apply {
            assertEquals(
                expected = PainterImage(
                    painter = ColorPainter(color = Color.Red).asAnimatablePainter()
                        .asEquitableWithThis(),
                    shareable = false
                ),
                actual = this
            )
        }
    }

    @Test
    fun testShareable() {
        PainterImage(
            painter = ColorPainter(color = Color.Red).asEquitable(),
        ).apply {
            assertEquals(expected = true, actual = shareable)
        }

        PainterImage(
            painter = ColorPainter(color = Color.Red).asAnimatablePainter()
                .asEquitableWithThis(),
        ).apply {
            assertEquals(expected = false, actual = shareable)
        }

        PainterImage(
            painter = ColorPainter(color = Color.Red).asAnimatablePainter()
                .asEquitableWithThis(),
            shareable = true
        ).apply {
            assertEquals(expected = true, actual = shareable)
        }
    }

    @Test
    fun testWidthAndHeight() {
        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size.Unspecified),
        ).apply {
            assertEquals(expected = -1, actual = width)
            assertEquals(expected = -1, actual = height)
        }

        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size(1.4f, 1.5f)),
        ).apply {
            assertEquals(expected = 1, actual = width)
            assertEquals(expected = 2, actual = height)
        }

        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size(1.5f, 1.4f)),
        ).apply {
            assertEquals(expected = 2, actual = width)
            assertEquals(expected = 1, actual = height)
        }
    }

    @Test
    fun testByteCount() {
        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size.Unspecified),
        ).apply {
            assertEquals(expected = 0L, actual = byteCount)
        }

        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size(101.4f, 202.5f)),
        ).apply {
            assertEquals(expected = 82012, actual = byteCount)
        }
    }

    @Test
    fun testCheckValid() {
        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size.Unspecified),
        ).apply {
            assertEquals(expected = true, actual = checkValid())
        }

        PainterImage(
            painter = SizeColorPainter(color = Color.Red, Size(101.4f, 202.5f)),
        ).apply {
            assertEquals(expected = true, actual = checkValid())
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = PainterImage(SizeColorPainter(color = Color.Red, Size(101f, 202f)))
        val element11 = element1.copy()
        val element2 =
            element1.copy(painter = SizeColorPainter(color = Color.Green, Size(101f, 202f)))
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
        PainterImage(SizeColorPainter(color = Color.Red, Size(101f, 202f))).apply {
            assertEquals(
                expected = "PainterImage(painter=SizeColorPainter(color=18446462598732840960, size=Size(101.0, 202.0)), shareable=true)",
                actual = toString()
            )
        }
    }
}