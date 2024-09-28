package com.github.panpf.sketch.animated.nonandroid.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.WebpSkiaAnimatedDecoder
import com.github.panpf.sketch.decode.supportSkiaAnimatedWebp
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.onAnimationEnd
import com.github.panpf.sketch.request.onAnimationStart
import com.github.panpf.sketch.request.repeatCount
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.createDecoderOrDefault
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.decode
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WebpSkiaAnimatedDecoderTest {

    @Test
    fun testSupportAnimatedWebp() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSkiaAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[WebpSkiaAnimatedDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSkiaAnimatedWebp()
            supportSkiaAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[WebpSkiaAnimatedDecoder,WebpSkiaAnimatedDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpSkiaAnimatedDecoder.Factory()

        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = ImageInfo(480, 270, "image/webp"),
                    actual = imageInfo
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpSkiaAnimatedDecoder.Factory()

        ImageRequest(context, ResourceImages.animWebp.uri) {
            colorSpace(ColorSpace.sRGB)
            onAnimationEnd { }
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(width = 480, height = 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            assertEquals(expected = null, actual = image.asOrThrow<SkiaAnimatedImage>().repeatCount)
        }

        ImageRequest(context, ResourceImages.animWebp.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(width = 480, height = 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            assertEquals(expected = 3, actual = image.asOrThrow<SkiaAnimatedImage>().repeatCount)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val element1 = WebpSkiaAnimatedDecoder(requestContext, dataSource)
        val element11 = WebpSkiaAnimatedDecoder(requestContext, dataSource)

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
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val decoder = WebpSkiaAnimatedDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("WebpSkiaAnimatedDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryKey() = runTest {
        assertEquals(
            expected = "WebpSkiaAnimatedDecoder",
            actual = WebpSkiaAnimatedDecoder.Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = WebpSkiaAnimatedDecoder.Factory()

        // normal
        ImageRequest(context, ResourceImages.animWebp.uri).createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = "image/webp")
        }.apply {
            assertTrue(actual = this is WebpSkiaAnimatedDecoder)
        }

        // no mimeType
        ImageRequest(context, ResourceImages.animWebp.uri).createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = null)
        }.apply {
            assertTrue(actual = this is WebpSkiaAnimatedDecoder)
        }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(actual = this is WebpSkiaAnimatedDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animWebp.uri) {
            disallowAnimatedImage()
        }.createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = null)
        }.apply {
            assertNull(actual = this)
        }

        // data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertNull(actual = this)
            }

        // Correct mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertNull(actual = this)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = WebpSkiaAnimatedDecoder.Factory()
        val element11 = WebpSkiaAnimatedDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "WebpSkiaAnimatedDecoder",
            actual = WebpSkiaAnimatedDecoder.Factory().toString()
        )
    }
}