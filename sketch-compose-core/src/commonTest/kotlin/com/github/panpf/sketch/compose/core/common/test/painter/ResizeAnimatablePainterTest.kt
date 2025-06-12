package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.block
import com.github.panpf.sketch.util.toSize
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ResizeAnimatablePainterTest {

    @Test
    fun testConstructor() {
        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize()
        ).apply {
            assertEquals(expected = ContentScale.Crop, actual = this.contentScale)
            assertEquals(expected = Alignment.Center, actual = this.alignment)
        }
        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize(),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd
        ).apply {
            assertEquals(expected = ContentScale.Fit, actual = this.contentScale)
            assertEquals(expected = Alignment.BottomEnd, actual = this.alignment)
        }

        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize(),
            scale = Scale.START_CROP
        ).apply {
            assertEquals(expected = ContentScale.Crop, actual = this.contentScale)
            assertEquals(expected = Alignment.TopStart, actual = this.alignment)
        }
        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize(),
            scale = Scale.CENTER_CROP
        ).apply {
            assertEquals(expected = ContentScale.Crop, actual = this.contentScale)
            assertEquals(expected = Alignment.Center, actual = this.alignment)
        }
        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize(),
            scale = Scale.END_CROP
        ).apply {
            assertEquals(expected = ContentScale.Crop, actual = this.contentScale)
            assertEquals(expected = Alignment.TopEnd, actual = this.alignment)
        }
        ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
            size = com.github.panpf.sketch.util.Size(100, 100).toSize(),
            scale = Scale.FILL
        ).apply {
            assertEquals(expected = ContentScale.FillBounds, actual = this.contentScale)
            assertEquals(expected = Alignment.Center, actual = this.alignment)
        }
    }

    @Test
    fun testStartStop() = runTest {
        val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Yellow))
        val wrapper = ResizeAnimatablePainter(animatablePainter, Size(100f, 500f))

        assertFalse(actual = animatablePainter.isRunning())
        assertFalse(actual = wrapper.isRunning())

        wrapper.start()
        block(100)
        assertTrue(actual = animatablePainter.isRunning())
        assertTrue(actual = wrapper.isRunning())

        wrapper.start()
        block(100)
        assertTrue(actual = animatablePainter.isRunning())
        assertTrue(actual = wrapper.isRunning())

        wrapper.stop()
        block(100)
        assertFalse(actual = animatablePainter.isRunning())
        assertFalse(actual = wrapper.isRunning())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
        )
        val element11 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
        )
        val element2 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Green).asEquitable()),
            size = Size(100f, 500f),
        )
        val element3 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(500f, 100f),
        )
        val element4 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
            contentScale = ContentScale.FillBounds,
        )
        val element5 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
            alignment = Alignment.BottomCenter,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element1, element5)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element2, element5)
        assertNotEquals(element3, element4)
        assertNotEquals(element3, element5)
        assertNotEquals(element4, element5)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element1.hashCode(), element5.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element5.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element5.hashCode())
        assertNotEquals(element4.hashCode(), element5.hashCode())
    }

    @Test
    fun testToString() {
        val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Green))
        ResizeAnimatablePainter(
            painter = animatablePainter,
            size = Size(100f, 500f),
        ).apply {
            assertEquals(
                expected = "ResizeAnimatablePainter(painter=${animatablePainter.toLogString()}, size=100.0x500.0, contentScale=Crop, alignment=Center)",
                actual = toString()
            )
        }
    }
}