package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.painter.resize
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toSize
import kotlin.test.Test
import kotlin.test.assertTrue

class ResizePainterTest {
    // TODO test

    @Test
    fun testRememberResizePainter() {
        // TODO test
    }

    @Test
    fun testPainterResize() {
        ColorPainter(Color.Green).resize(Size(100, 100).toSize()).apply {
            assertTrue(this !is ResizeAnimatablePainter)
        }

        ColorPainter(Color.Green).asAnimatablePainter().resize(Size(100, 100).toSize()).apply {
            assertTrue(this is ResizeAnimatablePainter)
        }
    }
}