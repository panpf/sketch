package com.github.panpf.sketch.blurhash.common.test.state

import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.state.BlurHashStateImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.util.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BlurHashStateImageTest {

    private val blurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    private val blurHash2 = "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH"

    @Test
    fun testConstructor() {
        BlurHashStateImage(blurHash)
        BlurHashStateImage(blurHash, Size.Empty)
        BlurHashStateImage(blurHash, null)
        BlurHashStateImage(blurHash = blurHash, size = Size.Empty)
        BlurHashStateImage(blurHash = blurHash, size = null)
    }

    @Test
    fun testKey() {
        BlurHashStateImage(blurHash).apply {
            assertEquals(
                expected = "BlurHashStateImage('$blurHash',null)",
                actual = key
            )
        }

        val size = Size(200, 100)
        BlurHashStateImage(blurHash, size).apply {
            assertEquals(
                expected = "BlurHashStateImage('$blurHash',${size})",
                actual = key
            )
        }
    }

    @Test
    fun testGetImage() {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val memoryCache = sketch.memoryCache

        memoryCache.clear()
        assertEquals(0, memoryCache.size)

        val image1 = BlurHashStateImage(blurHash)
            .getImage(sketch, request, null)!!.apply {
                assertEquals(40000L, byteCount)
                assertEquals(Size(100, 100), this.size)

                assertEquals(40000L, memoryCache.size)
                val cacheKey = newBlurHashUri(blurHash, 100, 100)
                assertTrue(memoryCache.exist(cacheKey))
            }

        val image2 = BlurHashStateImage(blurHash)
            .getImage(sketch, request, null)!!.apply {
                assertEquals(40000L, byteCount)
                assertEquals(Size(100, 100), this.size)

                assertEquals(40000L, memoryCache.size)
                val cacheKey = newBlurHashUri(blurHash, 100, 100)
                assertTrue(memoryCache.exist(cacheKey))
            }
        assertSame(image1, image2)

        val image3 = BlurHashStateImage(
            blurHash = blurHash,
            size = Size(200, 200)
        ).getImage(sketch, request, null)!!.apply {
            assertEquals(160000L, byteCount)
            assertEquals(Size(200, 200), this.size)

            assertEquals(200000L, memoryCache.size)
            val cacheKey = newBlurHashUri(blurHash, 200, 200)
            assertTrue(memoryCache.exist(cacheKey))
        }

        BlurHashStateImage(
            blurHash = newBlurHashUri(blurHash, 300, 300)
        ).getImage(sketch, request, null)!!.apply {
            assertEquals(360000L, byteCount)
            assertEquals(Size(300, 300), this.size)

            assertEquals(560000L, memoryCache.size)
            val cacheKey = newBlurHashUri(blurHash, 300, 300)
            assertTrue(memoryCache.exist(cacheKey))
        }

        val image4 = BlurHashStateImage(
            blurHash = newBlurHashUri(blurHash, 300, 0),
            size = Size(200, 200)
        ).getImage(sketch, request, null)!!.apply {
            assertEquals(160000L, byteCount)
            assertEquals(Size(200, 200), this.size)

            assertEquals(560000L, memoryCache.size)
            val cacheKey = newBlurHashUri(blurHash, 200, 200)
            assertTrue(memoryCache.exist(cacheKey))
        }
        assertSame(image3, image4)

        assertFailsWith(IllegalArgumentException::class) {
            BlurHashStateImage("invalid_uri").getImage(sketch, request, null)
        }
        assertFailsWith(IllegalArgumentException::class) {
            BlurHashStateImage(newBlurHashUri("invalid_uri")).getImage(sketch, request, null)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val element1 = BlurHashStateImage(blurHash)
        val element11 = BlurHashStateImage(blurHash)
        val element2 = BlurHashStateImage(blurHash = blurHash2)
        val element3 = BlurHashStateImage(blurHash, Size(100, 200))

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
    fun testToString() {
        val size = Size(100, 200)
        assertEquals(
            expected = "BlurHashStateImage(blurHash='${blurHash}', size=$size)",
            actual = BlurHashStateImage(blurHash, size).toString()
        )
    }
}