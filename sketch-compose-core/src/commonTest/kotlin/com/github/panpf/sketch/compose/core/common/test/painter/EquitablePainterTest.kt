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
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.util.key
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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

    @Test
    fun testKey() {
        assertEquals(
            expected = "EquitablePainter('${key(Color.Red)}')",
            actual = EquitablePainter(
                painter = ColorPainter(Color.Red),
                equalityKey = Color.Red
            ).key
        )
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = EquitablePainter(
            painter = ColorPainter(Color.Red),
            equalityKey = Color.Red
        )
        val element11 = EquitablePainter(
            painter = ColorPainter(Color.Red),
            equalityKey = Color.Red
        )
        val element2 = EquitablePainter(
            painter = ColorPainter(Color.Red),
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
        val painter = ColorPainter(Color.Red)
        assertEquals(
            expected = "EquitablePainter(painter=${painter.toLogString()}, equalityKey=${Color.Red})",
            actual = EquitablePainter(
                painter = painter,
                equalityKey = Color.Red,
            ).toString()
        )
    }
}