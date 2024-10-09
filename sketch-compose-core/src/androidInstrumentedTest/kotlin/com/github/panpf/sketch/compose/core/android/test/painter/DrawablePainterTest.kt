@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.android.test.painter

import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.AndroidBitmap
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.painter.DrawableAnimatablePainter
import com.github.panpf.sketch.painter.DrawablePainter
import com.github.panpf.sketch.painter.EmptyPainter
import com.github.panpf.sketch.painter.MAIN_HANDLER
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.painter.intrinsicSize
import com.github.panpf.sketch.painter.rememberDrawablePainter
import com.github.panpf.sketch.test.utils.SizeDrawable
import com.github.panpf.sketch.test.utils.TestAnimatable2CompatDrawable
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.asEquitableWithThis
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DrawablePainterTest {

    @Test
    fun testRememberDrawablePainter() {
        runComposeUiTest {
            setContent {
                rememberDrawablePainter(TestAnimatable2CompatDrawable(ColorDrawable(TestColor.YELLOW).asEquitable()).asEquitableWithThis()).apply {
                    assertEquals(
                        expected = DrawableAnimatablePainter(
                            TestAnimatable2CompatDrawable(
                                ColorDrawable(TestColor.YELLOW).asEquitable()
                            ).asEquitableWithThis()
                        ),
                        actual = this,
                    )
                }

                val bitmap = remember { AndroidBitmap(100, 100) }
                rememberDrawablePainter(BitmapDrawable(null, bitmap).asEquitable(bitmap)).apply {
                    assertEquals(
                        expected = DrawablePainter(
                            BitmapDrawable(
                                null,
                                bitmap
                            ).asEquitable(bitmap)
                        ),
                        actual = this,
                    )
                }

                rememberDrawablePainter(ColorDrawable(TestColor.RED).asEquitable()).apply {
                    assertEquals(
                        expected = DrawablePainter(ColorDrawable(TestColor.RED).asEquitable()),
                        actual = this,
                    )
                }

                rememberDrawablePainter(
                    SizeDrawable(
                        ColorDrawable(TestColor.RED).asEquitable(),
                        Size(100, 100)
                    ).asEquitable("Size(100, 100)")
                ).apply {
                    assertEquals(
                        expected = DrawablePainter(
                            SizeDrawable(
                                ColorDrawable(TestColor.RED).asEquitable(),
                                Size(100, 100)
                            ).asEquitable("Size(100, 100)")
                        ),
                        actual = this,
                    )
                }
            }
        }
    }

    @Test
    fun testAsPainter() {
        (null as Drawable?).asPainter().apply {
            assertSame(expected = EmptyPainter, actual = this)
        }

        TestAnimatable2CompatDrawable(ColorDrawable(TestColor.YELLOW).asEquitable())
            .asPainter()
            .apply {
                assertEquals(
                    expected = DrawableAnimatablePainter(
                        TestAnimatable2CompatDrawable(ColorDrawable(TestColor.YELLOW).asEquitable())
                    ),
                    actual = this
                )
            }

        BitmapDrawable(null, AndroidBitmap(100, 100)).asPainter().apply {
            assertTrue(
                actual = this is BitmapPainter,
                message = "Expected: BitmapPainter, actual: $this"
            )
        }

        ColorDrawable(TestColor.RED).asPainter().apply {
            assertEquals(
                expected = ColorPainter(Color(TestColor.RED)),
                actual = this,
            )
        }

        SizeDrawable(ColorDrawable(TestColor.RED).asEquitable(), Size(100, 100)).asPainter().apply {
            assertEquals(
                expected = DrawablePainter(
                    SizeDrawable(
                        drawable = ColorDrawable(TestColor.RED).asEquitable(),
                        size = Size(100, 100)
                    )
                ),
                actual = this,
            )
        }
    }

    @Test
    fun testConstructor() {
        val drawable = ColorDrawable(TestColor.RED)
        DrawablePainter(drawable)
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = drawable.bounds
        )

        val drawable1 = SizeDrawable(ColorDrawable(TestColor.RED), Size(101, -1))
        DrawablePainter(drawable1)
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = drawable1.bounds
        )

        val drawable2 = SizeDrawable(ColorDrawable(TestColor.RED), Size(-1, 202))
        DrawablePainter(drawable2)
        assertEquals(
            expected = Rect(0, 0, 0, 0),
            actual = drawable2.bounds
        )

        val drawable3 = SizeDrawable(ColorDrawable(TestColor.RED), Size(101, 202))
        DrawablePainter(drawable3)
        assertEquals(
            expected = Rect(0, 0, 101, 202),
            actual = drawable3.bounds
        )
    }

    @Test
    fun testIntrinsicSize() {
        DrawablePainter(ColorDrawable(TestColor.YELLOW)).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size.Unspecified,
                actual = this.intrinsicSize
            )
        }

        DrawablePainter(ColorDrawable(TestColor.YELLOW).size(Size(101, 202))).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size(101f, 202f),
                actual = intrinsicSize
            )
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testRememberObserver() {
        val drawable = ColorDrawable(TestColor.YELLOW)
        val drawablePainter = DrawablePainter(drawable)
        assertEquals(0, drawablePainter.rememberedCount)
        assertNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onRemembered()
        assertEquals(1, drawablePainter.rememberedCount)
        assertNotNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onRemembered()
        assertEquals(2, drawablePainter.rememberedCount)
        assertNotNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onForgotten()
        assertEquals(1, drawablePainter.rememberedCount)
        assertNotNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onRemembered()
        assertEquals(2, drawablePainter.rememberedCount)
        assertNotNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onAbandoned()
        assertEquals(1, drawablePainter.rememberedCount)
        assertNotNull(drawable.callback)
        assertTrue(drawable.isVisible)

        drawablePainter.onForgotten()
        assertEquals(0, drawablePainter.rememberedCount)
        assertNull(drawable.callback)
        assertFalse(drawable.isVisible)

        drawablePainter.onForgotten()
        assertEquals(0, drawablePainter.rememberedCount)
        assertNull(drawable.callback)
        assertFalse(drawable.isVisible)
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = DrawablePainter(ColorDrawable(TestColor.GRAY).asEquitable())
        val element11 = DrawablePainter(ColorDrawable(TestColor.GRAY).asEquitable())
        val element2 = DrawablePainter(ColorDrawable(TestColor.YELLOW).asEquitable())

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
            expected = "DrawablePainter(drawable=ColorDrawable(-7829368))",
            actual = DrawablePainter(ColorDrawable(TestColor.GRAY)).toString()
        )
    }

    @Test
    fun testMainHandler() {
        assertEquals(
            expected = Looper.getMainLooper(),
            actual = MAIN_HANDLER.looper
        )
    }

    @Test
    fun testDrawableIntrinsicSize() {
        SizeDrawable(ColorDrawable(TestColor.RED), Size(100, 100)).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size(100f, 100f),
                actual = this.intrinsicSize
            )
        }

        SizeDrawable(ColorDrawable(TestColor.RED), Size(-100, 100)).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size.Unspecified,
                actual = this.intrinsicSize
            )
        }

        SizeDrawable(ColorDrawable(TestColor.RED), Size(100, -100)).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size.Unspecified,
                actual = this.intrinsicSize
            )
        }

        SizeDrawable(ColorDrawable(TestColor.RED), Size(-100, -100)).apply {
            assertEquals(
                expected = androidx.compose.ui.geometry.Size.Unspecified,
                actual = this.intrinsicSize
            )
        }
    }

    @Test
    fun testEmptyPainter() {
        assertEquals(
            expected = androidx.compose.ui.geometry.Size.Unspecified,
            actual = EmptyPainter.intrinsicSize
        )

        assertSame(
            expected = EmptyPainter,
            actual = EmptyPainter
        )
    }
}