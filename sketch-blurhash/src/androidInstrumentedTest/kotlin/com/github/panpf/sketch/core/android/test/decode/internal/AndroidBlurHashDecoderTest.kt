package com.github.panpf.sketch.core.android.test.decode.internal

import com.github.panpf.sketch.decode.internal.BlurHashDecoder
import com.github.panpf.sketch.fetch.isBlurHashUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AndroidBlurHashDecoderTest {

    val testableBlurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"
    val testableBlurHashUri = "blurhash://$testableBlurHash&width=200&height=300"


    @Test
    fun testCorrectBlurHashUri() = runTest {
        isBlurHashUri(testableBlurHashUri.toUri())
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, testableBlurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        BlurHashDecoder(requestContext, dataSource)
        BlurHashDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "blurhash://$testableBlurHash")
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        val decoder = BlurHashDecoder(requestContext, dataSource)

        assertEquals(expected = 100, actual = decoder.imageInfo.width)
        assertEquals(expected = 100, actual = decoder.imageInfo.height)
        assertEquals(expected = "", actual = decoder.imageInfo.mimeType)
    }

    @Test
    fun testFallbackToSize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        val decoder = BlurHashDecoder(requestContext, dataSource)

        assertEquals(expected = 200, actual = decoder.imageInfo.width)
        assertEquals(expected = 300, actual = decoder.imageInfo.height)
        assertEquals(expected = "", actual = decoder.imageInfo.mimeType)
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        val decoder = BlurHashDecoder(requestContext, dataSource)

        decoder.decode().apply {
            assertEquals(
                expected = "ImageInfo(200x300,'')",
                actual = imageInfo.toShortString()
            )
            assertEquals(expected = NETWORK, actual = dataFrom)
            assertNull(actual = transformeds)
            assertEquals(
                expected = "Bitmap(200x300,ARGB_8888${shortInfoColorSpace("SRGB")})",
                actual = image.getBitmapOrThrow().toShortInfoString()
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        val element1 = BlurHashDecoder(requestContext, dataSource)
        val element11 = BlurHashDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(testableBlurHash, NETWORK)

        val decoder = BlurHashDecoder(requestContext, dataSource)

        assertTrue(
            actual = decoder.toString().contains("BlurHashDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        BlurHashDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "BlurHashHelperDecoder",
            actual = BlurHashDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BlurHashDecoder.Factory()

        ImageRequest(context, testableBlurHashUri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "")
            }.apply {
                assertTrue(this is BlurHashDecoder)
            }

        ImageRequest(context, testableBlurHashUri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/png")
            }.apply {
                assertTrue(this is BlurHashDecoder)
            }
    }


    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BlurHashDecoder.Factory()
        val element11 = BlurHashDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "BlurHashHelperDecoder",
            actual = BlurHashDecoder.Factory().toString()
        )
    }
}