@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.blurhash.test.state

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.fetch.BlurhashUtil
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.state.rememberBlurhashStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.Size
import kotlin.test.*

class BlurhashStateImageTest {

    @Test
    fun testRememberBlurhashStateImage() {
        runComposeUiTest {
            setContent {
                rememberBlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)).apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)),
                        actual = this
                    )
                }
            }
        }
    }

    @Test
    fun testRememberBlurhashStateImageWithSize() {
        runComposeUiTest {
            setContent {
                rememberBlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)).apply {
                    assertEquals(
                        expected = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)),
                        actual = this
                    )
                }
            }
        }
    }


    @Test
    fun testKey() {
        val blurhash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"
        val size = Size(200, 100)
        BlurhashStateImage(blurhash).apply {
            assertEquals(
                expected = "BlurhashStateImage($blurhash,null)",
                actual = key
            )
        }
        BlurhashStateImage(blurhash, size).apply {
            assertEquals(
                expected = "BlurhashStateImage($blurhash,${size})",
                actual = key
            )
        }

    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val blurhash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"

        assertEquals(0, sketch.memoryCache.size)

        BlurhashStateImage(blurhash, Size(100, 200)).apply {
            val image1 = getImage(sketch, request, null)
            assertTrue(image1 is BitmapImage, "Should return BitmapImage")

            val image2 = getImage(sketch, request, null)
            assertEquals(image1, image2, "Should return cached image")

            val cacheKey = "blurhash:$blurhash"
            assertTrue(sketch.memoryCache.exist(cacheKey), "Should be cached in memory")
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100")
        val element11 = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100")
        val element2 = BlurhashStateImage("LKN]Rv%2Tw=w]~RBVZRi};RPxuwH&width=100&height=100")
        val element3 = BlurhashStateImage("LKN]Rv%2Tw=w]~RBVZRi};RPxuwH", Size(100, 200))

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertEquals(element1.hashCode(), element11.hashCode())
        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testInstantiateBlurhashStateImageWithInvalidBlurhash() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        val invalidBlurhash = "invalid_blurhash_string"
        assertFalse(BlurhashUtil.isValid(invalidBlurhash))

        BlurhashStateImage(invalidBlurhash, Size(100, 200)).apply {
            val image = getImage(sketch, request, null)
            assertEquals(null, image)
        }

        val invalidBlurhashUri = "invalid_blurhash_string&width=100&height=100"
        BlurhashStateImage(invalidBlurhashUri).apply {
            val image = getImage(sketch, request, null)
            assertEquals(null, image)
        }
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "BlurhashStateImage(blurhash=L6PZfSi_.AyE_3t7t7R**0o#DgR4, size=100x200)",
            actual = BlurhashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)).toString()
        )
    }
}