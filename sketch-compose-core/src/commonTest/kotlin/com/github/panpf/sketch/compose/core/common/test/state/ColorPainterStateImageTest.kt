@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImage
import com.github.panpf.sketch.state.rememberColorPainterStateImageWithInt
import com.github.panpf.sketch.state.rememberColorPainterStateImageWithLong
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ColorPainterStateImageTest {

    @Test
    fun testRememberColorPainterStateImage() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImage(Color.Blue).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Blue),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorPainterStateImageWithLong() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImageWithLong(0xFFFFFFFF).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color(0xFFFFFFFF)),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberColorPainterStateImageWithInt() {
        runComposeUiTest {
            setContent {
                rememberColorPainterStateImageWithInt(Color.Blue.toArgb()).apply {
                    assertEquals(
                        expected = ColorPainterStateImage(Color(Color.Blue.toArgb())),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testKey() {
        assertEquals(
            expected = "ColorPainter(-65536)",
            actual = ColorPainterStateImage(Color.Red).key
        )
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        ColorPainterStateImage(Color.Red).apply {
            assertEquals(
                expected = ColorPainter(color).asImage(),
                actual = getImage(sketch, request, null)
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = ColorPainterStateImage(Color.Red)
        val element11 = ColorPainterStateImage(Color.Red)
        val element2 = ColorPainterStateImage(Color.Gray)

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "ColorPainterStateImage(color=-65536)",
            actual = ColorPainterStateImage(Color.Red).toString()
        )
    }
}