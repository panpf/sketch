@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.blurhash.test.state

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.painter.BlurhashPainter
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.state.rememberBlurhashStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlurhashStateImageTest {

    @Test
    fun testRememberColorPainterStateImage() {
        runComposeUiTest {
            setContent {
                rememberBlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4").apply {
                    assertEquals(
                        expected = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4"),
                        actual = this
                    )
                }
            }
        }
    }


    @Test
    fun testKey() {
        assertEquals(
            expected = "BlurhashPainter(blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4)",
            actual = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4").key
        )
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4").apply {
            assertEquals(
                expected = BlurhashPainter("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4").asImage(),
                actual = getImage(sketch, request, null)
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4")
        val element11 = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4")
        val element2 = BlurhashStateImage("blurhash://LKN]Rv%2Tw=w]~RBVZRi};RPxuwH")

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
            expected = "BlurhashStateImage(blurhash=blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4)",
            actual = BlurhashStateImage("blurhash://L6PZfSi_.AyE_3t7t7R**0o#DgR4").toString()
        )
    }
}