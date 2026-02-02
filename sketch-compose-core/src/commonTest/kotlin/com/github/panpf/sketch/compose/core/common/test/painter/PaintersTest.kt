@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.painter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.images.Res
import com.github.panpf.sketch.images.ic_image_outline
import com.github.panpf.sketch.painter.ImageBitmapPainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.painter.key
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.TestKeyPainter
import com.github.panpf.sketch.test.utils.TestNullableKeyPainter
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.toComposeBitmap
import com.github.panpf.sketch.util.toLogString
import org.jetbrains.compose.resources.painterResource
import kotlin.test.Test
import kotlin.test.assertEquals

class PaintersTest {

    @Test
    fun testPainterKey() {
        TestKeyPainter(ColorPainter(Color.Gray), key = "testKey1").apply {
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        TestNullableKeyPainter(ColorPainter(Color.Gray), key = null).apply {
            assertEquals(
                expected = "TestNullableKeyPainter(painter=ColorPainter(color=-7829368))",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "TestNullableKeyPainter(painter=ColorPainter(color=-7829368)):equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }
        TestNullableKeyPainter(ColorPainter(Color.Gray), "testKey1").apply {
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "testKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        BitmapPainter(createBitmap(101, 202).toComposeBitmap()).apply {
            assertEquals(
                expected = "BitmapPainter(101.0x202.0)",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "BitmapPainter:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        ColorPainter(Color.Blue).apply {
            assertEquals(
                expected = "ColorPainter(${color.toArgb()})",
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "ColorPainter(${color.toArgb()})",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        // Files in kotlin resources cannot be accessed in ios test environment.
        if (Platform.current != Platform.iOS) {
            runComposeUiTest {
                setContent {
                    painterResource(Res.drawable.ic_image_outline).apply {
                        assertEquals(
                            expected = "VectorPainter(${intrinsicSize.toLogString()})",
                            actual = key(equalityKey = null)
                        )
                        assertEquals(
                            expected = "VectorPainter:equalityKey1",
                            actual = key(equalityKey = "equalityKey1")
                        )
                    }
                }
            }
        }

        ImageBitmapPainter(createBitmap(101, 202).toComposeBitmap()).apply {
            assertEquals(
                expected = toString(),
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "${toString()}:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }

        PainterWrapper(ColorPainter(Color.Red)).apply {
            assertEquals(
                expected = toString(),
                actual = key(equalityKey = null)
            )
            assertEquals(
                expected = "${toString()}:equalityKey1",
                actual = key(equalityKey = "equalityKey1")
            )
        }
    }

    @Test
    fun testPainterToLogString() {
        val sketchPainter = ImageBitmapPainter(createBitmap(101, 202).toComposeBitmap())
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

        // Files in kotlin resources cannot be accessed in ios test environment.
        if (Platform.current != Platform.iOS) {
            runComposeUiTest {
                setContent {
                    val vectorPainter = painterResource(Res.drawable.ic_image_outline)
                    assertEquals(
                        expected = "VectorPainter(size=${vectorPainter.intrinsicSize.toLogString()})",
                        actual = vectorPainter.toLogString()
                    )
                }
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