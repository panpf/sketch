package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.EquitableAnimatablePainter
import com.github.panpf.sketch.painter.key
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.block
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EquitableAnimatablePainterTest {

    @Test
    fun testKey() {
        assertEquals(
            expected = TestAnimatablePainter(ColorPainter(Color.Red)).key(Color.Red),
            actual = EquitableAnimatablePainter(
                painter = TestAnimatablePainter(ColorPainter(Color.Red)),
                equalityKey = Color.Red
            ).key
        )
    }

    @Test
    fun testAcceptType() {
        val bitmapPainter = ColorPainter(Color.Red)
        assertFailsWith(IllegalArgumentException::class) {
            EquitableAnimatablePainter(bitmapPainter, equalityKey = "key")
        }

        TestAnimatablePainter(ColorPainter(Color.Green))
    }

    @Test
    fun testStartStop() = runTest {
        val animatablePainter = TestAnimatablePainter(ColorPainter(Color.Yellow))
        val wrapper = EquitableAnimatablePainter(animatablePainter, equalityKey = "key")

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
        val element1 = EquitableAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red)),
            equalityKey = Color.Red
        )
        val element11 = EquitableAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red)),
            equalityKey = Color.Red
        )
        val element2 = EquitableAnimatablePainter(
            painter = TestAnimatablePainter(ColorPainter(Color.Red)),
            equalityKey = TestColor.CYAN
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val painter = TestAnimatablePainter(ColorPainter(Color.Red))
        assertEquals(
            expected = "EquitableAnimatablePainter(painter=${painter.toLogString()}, equalityKey=${Color.Red})",
            actual = EquitableAnimatablePainter(
                painter = painter,
                equalityKey = Color.Red,
            ).toString()
        )
    }
}