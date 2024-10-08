@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.ComposeBitmapPainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.ic_image_outline
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.util.toLogString
import org.jetbrains.compose.resources.painterResource
import kotlin.test.Test
import kotlin.test.assertEquals

class PaintersTest {

    @Test
    fun testPainterToLogString() {
        val sketchPainter = ComposeBitmapPainter(createBitmap(101, 202).toComposeBitmap())
        assertEquals(
            expected = sketchPainter.toString(),
            actual = sketchPainter.toLogString()
        )

        val bitmapPainter = BitmapPainter(createBitmap(101, 202).toComposeBitmap())
        assertEquals(
            expected = "BitmapPainter(size=101.0x202.0)",
            actual = bitmapPainter.toLogString()
        )

        val colorPainter = ColorPainter(Color.Blue)
        assertEquals(
            expected = "ColorPainter(color=${colorPainter.color.toArgb()})",
            actual = colorPainter.toLogString()
        )

        runComposeUiTest {
            setContent {
                val vectorPainter = painterResource(Res.drawable.ic_image_outline)
                assertEquals(
                    expected = "VectorPainter(size=${vectorPainter.intrinsicSize.toLogString()})",
                    actual = vectorPainter.toLogString()
                )
            }
        }

        val childPainter = ColorPainter(Color.Red)
        PainterWrapper(childPainter).also {
            assertEquals(
                expected = "PainterWrapper(painter=${childPainter.toLogString()})",
                actual = it.toLogString()
            )
        }
    }
}