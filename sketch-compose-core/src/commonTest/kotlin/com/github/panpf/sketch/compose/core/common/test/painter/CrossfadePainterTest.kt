package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.CrossfadePainter
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.asAnimatablePainter
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.block
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CrossfadePainterTest {

    @Test
    fun testConstructor() {
        val startPainter = SizeColorPainter(Color.Red, Size(100f, 200f))
        val endPainter = SizeColorPainter(Color.Yellow, Size(200f, 100f))

        CrossfadePainter(start = startPainter, end = endPainter).apply {
            assertEquals(ContentScale.Fit, contentScale)
            assertTrue(fitScale)
            assertEquals(200, durationMillis)
            assertTrue(fadeStart)
            assertFalse(preferExactIntrinsicSize)
        }

        CrossfadePainter(
            start = startPainter,
            end = endPainter,
            fitScale = false,
            durationMillis = 2000,
            fadeStart = false,
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(ContentScale.Crop, contentScale)
            assertFalse(fitScale)
            assertEquals(2000, durationMillis)
            assertFalse(fadeStart)
            assertTrue(preferExactIntrinsicSize)
        }

        CrossfadePainter(
            start = startPainter,
            end = endPainter,
            contentScale = ContentScale.FillBounds,
            durationMillis = 2000,
            fadeStart = false,
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(ContentScale.FillBounds, contentScale)
            assertFalse(fitScale)
            assertEquals(2000, durationMillis)
            assertFalse(fadeStart)
            assertTrue(preferExactIntrinsicSize)
        }

        // TODO teest alignment
    }

    @Test
    fun testIntrinsicSize() {
        /*
         * null
         */
        CrossfadePainter(null, null, preferExactIntrinsicSize = false).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }
        CrossfadePainter(null, null, preferExactIntrinsicSize = true).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }

        CrossfadePainter(
            start = null,
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size(200f, 100f), intrinsicSize)
        }
        CrossfadePainter(
            start = null,
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size(200f, 100f), intrinsicSize)
        }

        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = null,
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size(100f, 200f), intrinsicSize)
        }
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = null,
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size(100f, 200f), intrinsicSize)
        }

        /*
         * Size.Unspecified
         */
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size.Unspecified),
            end = SizeColorPainter(Color.Yellow, Size.Unspecified),
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size.Unspecified),
            end = SizeColorPainter(Color.Yellow, Size.Unspecified),
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }

        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size.Unspecified),
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size.Unspecified),
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size(200f, 100f), intrinsicSize)
        }

        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = SizeColorPainter(Color.Yellow, Size.Unspecified),
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size.Unspecified, intrinsicSize)
        }
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = SizeColorPainter(Color.Yellow, Size.Unspecified),
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size(100f, 200f), intrinsicSize)
        }

        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = false
        ).apply {
            assertEquals(Size(200f, 200f), intrinsicSize)
        }
        CrossfadePainter(
            start = SizeColorPainter(Color.Red, Size(100f, 200f)),
            end = SizeColorPainter(Color.Yellow, Size(200f, 100f)),
            preferExactIntrinsicSize = true
        ).apply {
            assertEquals(Size(200f, 200f), intrinsicSize)
        }
    }

    @Test
    fun testDraw() {
        // TODO test: Screenshot test or draw to Bitmap, then compare Bitmap
    }

    @Test
    fun testStartStopIsRunning() {
        val startPainter =
            SizeColorPainter(Color.Red, Size(100f, 200f))
        val endPainter =
            SizeColorPainter(Color.Yellow, Size(200f, 100f))
        CrossfadePainter(startPainter, endPainter).apply {
            assertFalse(this.isRunning())

            start()
            block(100)
            assertTrue(this.isRunning())

            stop()
            block(100)
            assertFalse(this.isRunning())
        }

        val startAnimatablePainter =
            SizeColorPainter(Color.Red, Size(100f, 200f)).asAnimatablePainter()
        val endAnimatablePainter =
            SizeColorPainter(Color.Yellow, Size(200f, 100f)).asAnimatablePainter()
        CrossfadePainter(startAnimatablePainter, endAnimatablePainter).apply {
            assertFalse(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertFalse(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertFalse(this.isRunning())

            start()
            block(100)
            assertTrue(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertTrue(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertTrue(this.isRunning())

            stop()
            block(100)
            assertFalse(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertFalse(startAnimatablePainter.asOrThrow<AnimatablePainter>().isRunning())
            assertFalse(this.isRunning())
        }
    }

    @Test
    fun testRememberObserver() {
        val startPainter = SizeColorPainter(Color.Red, Size(100f, 200f))
        val endPainter = SizeColorPainter(Color.Yellow, Size(200f, 100f))
        val crossfadePainter = CrossfadePainter(startPainter, endPainter)

        assertEquals(expected = false, actual = crossfadePainter.isRunning())

        crossfadePainter.onRemembered()
        assertEquals(expected = true, actual = crossfadePainter.isRunning())

        crossfadePainter.onAbandoned()
        assertEquals(expected = false, actual = crossfadePainter.isRunning())

        crossfadePainter.onRemembered()
        assertEquals(expected = false, actual = crossfadePainter.isRunning())

        crossfadePainter.onForgotten()
        assertEquals(expected = false, actual = crossfadePainter.isRunning())
    }

    @Test
    fun testEqualsAndHashCode() {
        val startPainter = SizeColorPainter(Color.Red, Size(100f, 200f))
        val endPainter = SizeColorPainter(Color.Yellow, Size(200f, 100f))
        val endPainter2 = SizeColorPainter(Color.Yellow, Size(300f, 500f))

        val element1 = CrossfadePainter(startPainter, endPainter)
        val element11 = CrossfadePainter(startPainter, endPainter)
        val element2 = CrossfadePainter(startPainter, endPainter2)

        assertNotEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        val startPainter = SizeColorPainter(Color.Red, Size(100f, 200f))
        val endPainter = SizeColorPainter(Color.Yellow, Size(200f, 100f))
        val crossfadePainter = CrossfadePainter(startPainter, endPainter)
        assertEquals(
            expected = "CrossfadePainter(start=${startPainter.toLogString()}, end=${endPainter.toLogString()}, contentScale=Fit, durationMillis=200, fadeStart=true, preferExactIntrinsicSize=false)",
            actual = crossfadePainter.toString()
        )
    }
}