@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.EquitableAnimatablePainter
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import kotlin.test.Test
import kotlin.test.assertEquals

class EquitablePainterTest {

    @Test
    fun testAsEquitable() {
        runComposeUiTest {
            setContent {
                ColorPainter(Color.Blue).asEquitable("112").apply {
                    assertEquals(
                        expected = EquitablePainter(
                            painter = ColorPainter(Color.Blue),
                            equalityKey = "112"
                        ),
                        actual = this
                    )
                }

                TestAnimatablePainter(ColorPainter(Color.Blue)).asEquitable("112").apply {
                    assertEquals(
                        expected = EquitableAnimatablePainter(
                            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
                            equalityKey = "112"
                        ),
                        actual = this
                    )
                }

                ColorPainter(Color.Blue).asEquitable().apply {
                    assertEquals(
                        expected = EquitablePainter(
                            painter = ColorPainter(Color.Blue),
                            equalityKey = Color.Blue.value
                        ),
                        actual = this
                    )
                }

                BrushPainter(Brush.linearGradient()).asEquitable().apply {
                    assertEquals(
                        expected = EquitablePainter(
                            painter = BrushPainter(Brush.linearGradient()),
                            equalityKey = BrushPainter(Brush.linearGradient())
                        ),
                        actual = this
                    )
                }
            }
        }
    }

    // TODO test
}