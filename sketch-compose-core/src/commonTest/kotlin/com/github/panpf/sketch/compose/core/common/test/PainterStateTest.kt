package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.name
import com.github.panpf.sketch.painter.asEquitable
import kotlin.test.Test
import kotlin.test.assertEquals

class PainterStateTest {

    @Test
    fun testEmpty() {
        assertEquals(null, PainterState.Empty.painter)
    }

    @Test
    fun testLoading() {
        assertEquals(null, PainterState.Loading(null).painter)
        assertEquals(
            expected = ColorPainter(color = Color.Red).asEquitable(),
            actual = PainterState.Loading(ColorPainter(Color.Red).asEquitable()).painter
        )
    }

    @Test
    fun testSuccess() {
        assertEquals(
            expected = ColorPainter(color = Color.Red).asEquitable(),
            actual = PainterState.Success(ColorPainter(Color.Red).asEquitable()).painter
        )
    }

    @Test
    fun testError() {
        assertEquals(null, PainterState.Error(null).painter)
        assertEquals(
            expected = ColorPainter(color = Color.Red).asEquitable(),
            actual = PainterState.Error(ColorPainter(Color.Red).asEquitable()).painter
        )
    }

    @Test
    fun testPainterStateName() {
        assertEquals(expected = "Loading", actual = PainterState.Loading(null).name)
        assertEquals(
            expected = "Success",
            actual = PainterState.Success(ColorPainter(Color.Red)).name
        )
        assertEquals(expected = "Error", actual = PainterState.Error(null).name)
        assertEquals(expected = "Empty", actual = PainterState.Empty.name)
    }
}