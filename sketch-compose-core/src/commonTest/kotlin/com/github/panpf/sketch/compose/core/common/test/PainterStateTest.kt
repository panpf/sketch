package com.github.panpf.sketch.compose.core.common.test

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.name
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.test.utils.fakeErrorImageResult
import com.github.panpf.sketch.test.utils.fakeSuccessImageResult
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class PainterStateTest {

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
        val context = getTestContext()
        assertEquals(
            expected = ColorPainter(color = Color.Red).asEquitable(),
            actual = PainterState.Success(
                fakeSuccessImageResult(context),
                ColorPainter(Color.Red).asEquitable()
            ).painter
        )
    }

    @Test
    fun testError() {
        val context = getTestContext()
        assertEquals(null, PainterState.Error(fakeErrorImageResult(context), null).painter)
        assertEquals(
            expected = ColorPainter(color = Color.Red).asEquitable(),
            actual = PainterState.Error(
                fakeErrorImageResult(context),
                ColorPainter(Color.Red).asEquitable()
            ).painter
        )
    }

    @Test
    fun testPainterStateName() {
        val context = getTestContext()
        assertEquals(expected = "Loading", actual = PainterState.Loading(null).name)
        assertEquals(
            expected = "Success",
            actual = PainterState.Success(
                fakeSuccessImageResult(context),
                ColorPainter(Color.Red)
            ).name
        )
        assertEquals(
            expected = "Error",
            actual = PainterState.Error(fakeErrorImageResult(context), null).name
        )
    }
}