package com.github.panpf.sketch.blurhash.nonandroid.test.decode

import com.github.panpf.sketch.decode.BlurHashDecoder
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlurHashDecoderNonAndroidTest {

    private val blurHashUri = newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 200, 300)

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, blurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(blurHashUri.toUri())

        BlurHashDecoder(requestContext, dataSource)
        BlurHashDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, blurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(blurHashUri.toUri())

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
        val request = ImageRequest(context, blurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = BlurHashDataSource(blurHashUri.toUri())

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
            expected = "BlurHashDecoder",
            actual = BlurHashDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = BlurHashDecoder.Factory()

        ImageRequest(context, blurHashUri)
            .createDecoderOrNull(sketch, factory).apply {
                assertTrue(this is BlurHashDecoder)
            }

        ImageRequest(context, blurHashUri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(dataSource = FileDataSource("/path/to/file.txt".toPath()))
            }.apply {
                assertNull(this)
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
            expected = "BlurHashDecoder",
            actual = BlurHashDecoder.Factory().toString()
        )
    }
}