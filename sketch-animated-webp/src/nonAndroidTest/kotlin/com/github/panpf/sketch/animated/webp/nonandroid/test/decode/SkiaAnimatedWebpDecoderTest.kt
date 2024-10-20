package com.github.panpf.sketch.animated.webp.nonandroid.test.decode

import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder
import com.github.panpf.sketch.decode.SkiaAnimatedWebpDecoder.Factory
import com.github.panpf.sketch.decode.supportSkiaAnimatedWebp
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
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
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.transform.PixelOpacity.TRANSLUCENT
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SkiaAnimatedWebpDecoderTest {

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
                        "decoderFactoryList=[SkiaAnimatedWebpDecoder]," +
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
                        "decoderFactoryList=[SkiaAnimatedWebpDecoder,SkiaAnimatedWebpDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
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
        val dataSource = ResourceImages.animWebp.toDataSource(context)

        SkiaAnimatedWebpDecoder(requestContext, dataSource)
        SkiaAnimatedWebpDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

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
        val factory = Factory()

        ImageRequest(context, ResourceImages.animWebp.uri) {
            colorSpace(ColorSpace.sRGB)
            onAnimationEnd { }
            onAnimationStart { }
            animatedTransformation(TranslucentAnimatedTransformation)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(width = 480, height = 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            assertEquals(expected = null, actual = image.asOrThrow<AnimatedImage>().repeatCount)
            assertNotEquals(
                illegal = null,
                actual = image.asOrThrow<AnimatedImage>().animatedTransformation
            )
            assertNotEquals(
                illegal = null,
                actual = image.asOrThrow<AnimatedImage>().animationStartCallback
            )
            assertNotEquals(
                illegal = null,
                actual = image.asOrThrow<AnimatedImage>().animationEndCallback
            )
        }

        ImageRequest(context, ResourceImages.animWebp.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(width = 480, height = 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            assertEquals(expected = 3, actual = image.asOrThrow<AnimatedImage>().repeatCount)
            assertEquals(
                expected = null,
                actual = image.asOrThrow<AnimatedImage>().animatedTransformation
            )
            assertEquals(
                expected = null,
                actual = image.asOrThrow<AnimatedImage>().animationStartCallback
            )
            assertEquals(
                expected = null,
                actual = image.asOrThrow<AnimatedImage>().animationEndCallback
            )
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val element1 = SkiaAnimatedWebpDecoder(requestContext, dataSource)
        val element11 = SkiaAnimatedWebpDecoder(requestContext, dataSource)

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
        val decoder = SkiaAnimatedWebpDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("SkiaAnimatedWebpDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryConstructor() {
        Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "SkiaAnimatedWebpDecoder",
            actual = Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        // normal
        ImageRequest(context, ResourceImages.animWebp.uri).createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = "image/webp")
        }.apply {
            assertTrue(actual = this is SkiaAnimatedWebpDecoder)
        }

        // no mimeType
        ImageRequest(context, ResourceImages.animWebp.uri).createDecoderOrNull(sketch, factory) {
            it.copy(mimeType = null)
        }.apply {
            assertTrue(actual = this is SkiaAnimatedWebpDecoder)
        }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(actual = this is SkiaAnimatedWebpDecoder)
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
        val element1 = Factory()
        val element11 = Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "SkiaAnimatedWebpDecoder",
            actual = Factory().toString()
        )
    }

    private data object TranslucentAnimatedTransformation : AnimatedTransformation {
        override val key: String = "TranslucentAnimatedTransformation"

        override fun transform(canvas: Any, bounds: Rect): PixelOpacity {
            return TRANSLUCENT
        }
    }
}