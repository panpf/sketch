package com.github.panpf.sketch.blurhash.test.state

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.fetch.BlurHashUtil
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurHashStateImageTest {

    @Test
    fun testKey() {
        val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100"
        val size = Size(200, 100)
        BlurHashStateImage(blurHash).apply {
            assertEquals(
                expected = "BlurHashStateImage($blurHash,null)",
                actual = key
            )
        }
        BlurHashStateImage(blurHash, size).apply {
            assertEquals(
                expected = "BlurHashStateImage($blurHash,${size})",
                actual = key
            )
        }

    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"

        assertEquals(0, sketch.memoryCache.size)

        BlurHashStateImage(blurHash, Size(100, 200)).apply {
            val image1 = getImage(sketch, request, null)
            assertTrue(image1 is BitmapImage, "Should return BitmapImage")

            val image2 = getImage(sketch, request, null)
            assertEquals(image1, image2, "Should return cached image")

            val cacheKey = "blurhash:$blurHash"
            assertTrue(sketch.memoryCache.exist(cacheKey), "Should be cached in memory")
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100")
        val element11 = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4&width=100&height=100")
        val element2 = BlurHashStateImage("LKN]Rv%2Tw=w]~RBVZRi};RPxuwH&width=100&height=100")
        val element3 = BlurHashStateImage("LKN]Rv%2Tw=w]~RBVZRi};RPxuwH", Size(100, 200))

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
    fun testInstantiateBlurHashStateImageWithInvalidBlurHash() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)

        val invalidBlurHash = "invalid_blur_hash_string"
        assertFalse(BlurHashUtil.isValid(invalidBlurHash))

        BlurHashStateImage(invalidBlurHash, Size(100, 200)).apply {
            val image = getImage(sketch, request, null)
            assertEquals(null, image)
        }

        val invalidBlurHashUri = "invalid_blur_hash_string&width=100&height=100"
        BlurHashStateImage(invalidBlurHashUri).apply {
            val image = getImage(sketch, request, null)
            assertEquals(null, image)
        }
    }

    @Test
    fun testToString() {
        assertEquals(
            expected = "BlurHashStateImage(blurHash=L6PZfSi_.AyE_3t7t7R**0o#DgR4, size=100x200)",
            actual = BlurHashStateImage("L6PZfSi_.AyE_3t7t7R**0o#DgR4", Size(100, 200)).toString()
        )
    }
}