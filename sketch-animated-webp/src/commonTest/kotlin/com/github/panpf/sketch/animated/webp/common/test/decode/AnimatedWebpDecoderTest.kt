package com.github.panpf.sketch.animated.webp.common.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.AnimatedWebpDecoder
import com.github.panpf.sketch.decode.defaultAnimatedWebpDecoderFactory
import com.github.panpf.sketch.decode.supportAnimatedWebp
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

class AnimatedWebpDecoderTest {

    @Test
    fun testSupportAnimatedWebp() {
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
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[AnimatedWebpDecoder(decoderFactory=${defaultAnimatedWebpDecoderFactory()})]," +
                        "requestInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAnimatedWebp()
            supportAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[AnimatedWebpDecoder(decoderFactory=${defaultAnimatedWebpDecoderFactory()}),AnimatedWebpDecoder(decoderFactory=${defaultAnimatedWebpDecoderFactory()})]," +
                        "requestInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animWebp.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultAnimatedWebpDecoder =
            defaultAnimatedWebpDecoderFactory()?.create(requestContext, fetchResult)
                ?: return@runTest
        AnimatedWebpDecoder(defaultAnimatedWebpDecoder)
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animWebp.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultAnimatedWebpDecoder1 =
            defaultAnimatedWebpDecoderFactory()?.create(requestContext, fetchResult)
                ?: return@runTest
        val element1 = AnimatedWebpDecoder(defaultAnimatedWebpDecoder1)
        val element11 = AnimatedWebpDecoder(defaultAnimatedWebpDecoder1)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = ResourceImages.animWebp.let {
            FetchResult(it.toDataSource(context), it.mimeType)
        }
        val defaultAnimatedWebpDecoder =
            defaultAnimatedWebpDecoderFactory()?.create(requestContext, fetchResult)
                ?: return@runTest
        val decoder = AnimatedWebpDecoder(defaultAnimatedWebpDecoder)
        assertTrue(
            actual = decoder.toString().contains("AnimatedWebpDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        AnimatedWebpDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        val defaultAnimatedWebpDecoderFactory = defaultAnimatedWebpDecoderFactory()
        assertEquals(
            expected = "AnimatedWebpDecoder(${defaultAnimatedWebpDecoderFactory?.key})",
            actual = AnimatedWebpDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        if (Platform.current == Platform.iOS) {
            // Files in kotlin resources cannot be accessed in ios test environment.
            return@runTest
        }
        val (context, sketch) = getTestContextAndSketch()
        val factory = AnimatedWebpDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertTrue(this is AnimatedWebpDecoder)
            }

        // no mimeType
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is AnimatedWebpDecoder)
            }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is AnimatedWebpDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animWebp.uri) {
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
        val element1 = AnimatedWebpDecoder.Factory()
        val element11 = AnimatedWebpDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        val defaultAnimatedWebpDecoderFactory = defaultAnimatedWebpDecoderFactory()
        assertEquals(
            expected = "AnimatedWebpDecoder(decoderFactory=$defaultAnimatedWebpDecoderFactory)",
            actual = AnimatedWebpDecoder.Factory().toString()
        )
    }
}