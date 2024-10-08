@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.painter.rememberResizePainter
import com.github.panpf.sketch.painter.resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResizePainterTest {

    @Test
    fun testRememberResizePainter() {
        runComposeUiTest {
            setContent {
                rememberResizePainter(
                    painter = ColorPainter(Color.Blue),
                    size = Size(100, 100).toSize()
                ).apply {
                    assertEquals(
                        expected = ResizePainter(
                            painter = ColorPainter(Color.Blue),
                            size = Size(100, 100).toSize(),
                            scale = Scale.CENTER_CROP
                        ),
                        actual = this
                    )
                }

                rememberResizePainter(
                    painter = ColorPainter(Color.Blue),
                    size = Size(100, 100).toSize(),
                    scale = Scale.START_CROP
                ).apply {
                    assertEquals(
                        expected = ResizePainter(
                            painter = ColorPainter(Color.Blue),
                            size = Size(100, 100).toSize(),
                            scale = Scale.START_CROP
                        ),
                        actual = this
                    )
                }

                rememberResizePainter(
                    painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
                    size = Size(100, 100).toSize(),
                    scale = Scale.START_CROP
                ).apply {
                    assertEquals(
                        expected = ResizeAnimatablePainter(
                            painter = TestAnimatablePainter(ColorPainter(Color.Blue)),
                            size = Size(100, 100).toSize(),
                            scale = Scale.START_CROP
                        ),
                        actual = this
                    )
                }
            }
        }
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

    // TODO test
}