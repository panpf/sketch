package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.test.utils.SizeColorPainter
import kotlin.test.Test
import kotlin.test.assertEquals

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
}