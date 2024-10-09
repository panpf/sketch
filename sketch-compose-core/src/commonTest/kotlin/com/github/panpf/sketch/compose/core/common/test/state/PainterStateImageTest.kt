@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.rememberPainterStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PainterStateImageTest {

    @Test
    fun testRememberPainterStateImage() {
        runComposeUiTest {
            setContent {
                rememberPainterStateImage(ColorPainter(Color.Blue).asEquitable()).apply {
                    assertEquals(
                        expected = PainterStateImage(ColorPainter(Color.Blue).asEquitable()),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testKey() {
        PainterStateImage(ColorPainter(Color.Red).asEquitable()).apply {
            assertEquals(
                expected = "PainterStateImage(EquitablePainter('18446462598732840960'))",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        PainterStateImage(ColorPainter(Color.Red).asEquitable()).apply {
            assertEquals(
                ColorPainter(Color.Red).asEquitable().asImage(),
                getImage(sketch, request, null)
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val stateImage1 = PainterStateImage(ColorPainter(Color.Red).asEquitable())
        val stateImage11 = PainterStateImage(ColorPainter(Color.Red).asEquitable())
        val stateImage2 = PainterStateImage(ColorPainter(Color.Green).asEquitable())

        assertEquals(stateImage1, stateImage11)
        assertNotEquals(stateImage1, stateImage2)
        assertNotEquals(stateImage1, null as Any?)
        assertNotEquals(stateImage1, Any())

        assertEquals(stateImage1.hashCode(), stateImage11.hashCode())
        assertNotEquals(stateImage1.hashCode(), stateImage2.hashCode())
    }

    @Test
    fun testToString() {
        PainterStateImage(ColorPainter(Color.Red).asEquitable()).apply {
            assertEquals(
                expected = "PainterStateImage(painter=EquitablePainter(painter=ColorPainter(color=-65536), equalityKey=18446462598732840960))",
                actual = toString()
            )
        }
    }
}