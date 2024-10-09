package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.block
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ResizeAnimatablePainterTest {

    @Test
    fun testStartStop() = runTest {
        val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Yellow))
        val wrapper = ResizeAnimatablePainter(animatablePainter, Size(100f, 500f), CENTER_CROP)

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
            scale = CENTER_CROP,
        )
        val element11 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
            scale = CENTER_CROP,
        )
        val element2 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Green).asEquitable()),
            size = Size(100f, 500f),
            scale = CENTER_CROP,
        )
        val element3 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(500f, 100f),
            scale = CENTER_CROP,
        )
        val element4 = ResizeAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red).asEquitable()),
            size = Size(100f, 500f),
            scale = START_CROP,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Green))
        ResizeAnimatablePainter(
            painter = animatablePainter,
            size = Size(100f, 500f),
            scale = CENTER_CROP
        ).apply {
            assertEquals(
                expected = "ResizeAnimatablePainter(painter=${animatablePainter.toLogString()}, size=100.0x500.0, scale=CENTER_CROP)",
                actual = toString()
            )
        }
    }
}