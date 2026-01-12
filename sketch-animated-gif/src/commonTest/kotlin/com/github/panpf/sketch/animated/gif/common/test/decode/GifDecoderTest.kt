package com.github.panpf.sketch.animated.gif.common.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.GifDecoder
import com.github.panpf.sketch.decode.defaultGifDecoderFactory
import com.github.panpf.sketch.decode.supportGif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.disallowAnimatedImage
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.Platform
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.current
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GifDecoderTest {

    @Test
    fun testSupportGif() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[GifDecoder(decoderFactory=${defaultGifDecoderFactory()})]," +
                        "requestInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportGif()
            supportGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[GifDecoder(decoderFactory=${defaultGifDecoderFactory()}),GifDecoder(decoderFactory=${defaultGifDecoderFactory()})]," +
                        "requestInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animGif.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultGifDecoder = defaultGifDecoderFactory().create(requestContext, fetchResult)!!
        GifDecoder(defaultGifDecoder)
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animGif.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultGifDecoder1 = defaultGifDecoderFactory().create(requestContext, fetchResult)!!
        val element1 = GifDecoder(defaultGifDecoder1)
        val element11 = GifDecoder(defaultGifDecoder1)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animGif.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultGifDecoder = defaultGifDecoderFactory().create(requestContext, fetchResult)!!
        val decoder = GifDecoder(defaultGifDecoder)
        assertTrue(
            actual = decoder.toString().contains("GifDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        GifDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        val defaultGifDecoderFactory = defaultGifDecoderFactory()
        assertEquals(
            expected = "GifDecoder(${defaultGifDecoderFactory.key})",
            actual = GifDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val factory = GifDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertTrue(this is GifDecoder)
            }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is GifDecoder)
            }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is GifDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
            disallowAnimatedImage()
        }.createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = null)
        }.apply {
            assertNull(this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertNull(this)
            }

        // Disguised, mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertNull(this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = GifDecoder.Factory()
        val element11 = GifDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        val defaultGifDecoderFactory = defaultGifDecoderFactory()
        assertEquals(
            expected = "GifDecoder(decoderFactory=$defaultGifDecoderFactory)",
            actual = GifDecoder.Factory().toString()
        )
    }
}