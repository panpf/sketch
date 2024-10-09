@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.ResizeAnimatablePainter
import com.github.panpf.sketch.painter.ResizePainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.painter.rememberResizePainter
import com.github.panpf.sketch.painter.resize
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.resize.Scale.CENTER_CROP
import com.github.panpf.sketch.resize.Scale.START_CROP
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
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

    @Test
    fun testIntrinsicSize() {
        val bitmapPainter = createBitmap(100, 200).toComposeBitmap().asPainter()
        ResizePainter(
            bitmapPainter,
            androidx.compose.ui.geometry.Size(500f, 300f),
            CENTER_CROP
        ).apply {
            assertEquals(androidx.compose.ui.geometry.Size(500f, 300f), intrinsicSize)
            assertEquals(androidx.compose.ui.geometry.Size(500f, 300f), size)
            assertSame(bitmapPainter, painter)
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ResizePainter(
            painter = ColorPainter(Color.Red).asEquitable(),
            size = androidx.compose.ui.geometry.Size(100f, 500f),
            scale = CENTER_CROP,
        )
        val element11 = ResizePainter(
            painter = ColorPainter(Color.Red).asEquitable(),
            size = androidx.compose.ui.geometry.Size(100f, 500f),
            scale = CENTER_CROP,
        )
        val element2 = ResizePainter(
            painter = ColorPainter(Color.Green).asEquitable(),
            size = androidx.compose.ui.geometry.Size(100f, 500f),
            scale = CENTER_CROP,
        )
        val element3 = ResizePainter(
            painter = ColorPainter(Color.Red).asEquitable(),
            size = androidx.compose.ui.geometry.Size(500f, 100f),
            scale = CENTER_CROP,
        )
        val element4 = ResizePainter(
            painter = ColorPainter(Color.Red).asEquitable(),
            size = androidx.compose.ui.geometry.Size(100f, 500f),
            scale = START_CROP,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element1, element4)
        assertNotEquals(element2, element3)
        assertNotEquals(element2, element4)
        assertNotEquals(element3, element4)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element1.hashCode(), element4.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element4.hashCode())
        assertNotEquals(element3.hashCode(), element4.hashCode())
    }

    @Test
    fun testToString() {
        val bitmapPainter = createBitmap(100, 200).toComposeBitmap().asPainter()
        ResizePainter(
            painter = bitmapPainter,
            size = androidx.compose.ui.geometry.Size(500f, 300f),
            scale = CENTER_CROP
        ).apply {
            assertEquals(
                "ResizePainter(painter=${bitmapPainter.toLogString()}, size=500.0x300.0, scale=CENTER_CROP)",
                toString()
            )
        }
    }
}