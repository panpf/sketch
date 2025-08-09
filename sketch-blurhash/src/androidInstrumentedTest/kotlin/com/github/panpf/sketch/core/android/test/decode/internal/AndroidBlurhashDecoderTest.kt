package com.github.panpf.sketch.core.android.test.decode.internal

import com.github.panpf.sketch.decode.internal.BlurhashDecoder
import com.github.panpf.sketch.fetch.isBlurHashUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurhashDataSource
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.getBitmapOrThrow
import com.github.panpf.sketch.test.utils.shortInfoColorSpace
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.toShortInfoString
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AndroidBlurhashDecoderTest {

    val testableBlurhash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj"
    val testableBlurhashUri = "blurhash://$testableBlurhash&width=200&height=300"


    @Test
    fun testCorrectBlurhashUri() = runTest {
        isBlurHashUri(testableBlurhashUri.toUri())
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, testableBlurhashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        BlurhashDecoder(requestContext, dataSource)
        BlurhashDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, "blurhash://$testableBlurhash")
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        val decoder = BlurhashDecoder(requestContext, dataSource)

        assertEquals(expected = 100, actual = decoder.imageInfo.width)
        assertEquals(expected = 100, actual = decoder.imageInfo.height)
        assertEquals(expected = "", actual = decoder.imageInfo.mimeType)
    }

    @Test
    fun testFallbackToSize() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurhashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        val decoder = BlurhashDecoder(requestContext, dataSource)

        assertEquals(expected = 200, actual = decoder.imageInfo.width)
        assertEquals(expected = 300, actual = decoder.imageInfo.height)
        assertEquals(expected = "", actual = decoder.imageInfo.mimeType)
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurhashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        val decoder = BlurhashDecoder(requestContext, dataSource)

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
        val request = ImageRequest(context, testableBlurhashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        val element1 = BlurhashDecoder(requestContext, dataSource)
        val element11 = BlurhashDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, testableBlurhashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurhashDataSource(testableBlurhash, NETWORK)

        val decoder = BlurhashDecoder(requestContext, dataSource)

        assertTrue(
            actual = decoder.toString().contains("BlurhashDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        BlurhashDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "BlurhashHelperDecoder",
            actual = BlurhashDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BlurhashDecoder.Factory()

        ImageRequest(context, testableBlurhashUri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "")
            }.apply {
                assertTrue(this is BlurhashDecoder)
            }

        ImageRequest(context, testableBlurhashUri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/png")
            }.apply {
                assertTrue(this is BlurhashDecoder)
            }
    }


    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = BlurhashDecoder.Factory()
        val element11 = BlurhashDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "BlurhashHelperDecoder",
            actual = BlurhashDecoder.Factory().toString()
        )
    }
}