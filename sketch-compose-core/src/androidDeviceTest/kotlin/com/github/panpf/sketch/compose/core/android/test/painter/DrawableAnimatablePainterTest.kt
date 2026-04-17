package com.github.panpf.sketch.compose.core.android.test.painter

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.painter.DrawableAnimatablePainter
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class DrawableAnimatablePainterTest {

    @Test
    fun testConstructor() {
        assertFailsWith(IllegalArgumentException::class) {
            DrawableAnimatablePainter(ColorDrawable(TestColor.GRAY))
        }

        DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.GRAY)))
    }

    @Test
    fun testStartStopIsRunning() {
        val animatedImagePainter =
            DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.GRAY)))

        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.start()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.stop()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.onRemembered()
        assertEquals(true, animatedImagePainter.isRunning())

        animatedImagePainter.stop()
        assertEquals(false, animatedImagePainter.isRunning())

        animatedImagePainter.start()
        assertEquals(true, animatedImagePainter.isRunning())

        animatedImagePainter.onForgotten()
        assertEquals(false, animatedImagePainter.isRunning())
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 =
            DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.GRAY).asEquitable()))
        val element11 =
            DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.GRAY).asEquitable()))
        val element2 =
            DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.YELLOW).asEquitable()))

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
            expected = "DrawableAnimatablePainter(drawable=TestAnimatable2CompatDrawable(drawable=ColorDrawable(color=-7829368)))",
            actual = DrawableAnimatablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.GRAY))).toString()
        )
    }
}