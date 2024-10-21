package com.github.panpf.sketch.animated.gif.nonandroid.test.decode

import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.SkiaGifDecoder
import com.github.panpf.sketch.decode.SkiaGifDecoder.Factory
import com.github.panpf.sketch.decode.supportSkiaGif
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.animatedTransformation
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.disallowAnimatedImage
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

class GifSkiaAnimatedDecoderTest {

    @Test
    fun testSupportGif() {
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
            supportSkiaGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[SkiaGifDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportSkiaGif()
            supportSkiaGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[SkiaGifDecoder,SkiaGifDecoder]," +
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

        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)

        SkiaGifDecoder(requestContext, dataSource)
        SkiaGifDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrDefault(sketch, factory)
            .apply {
                assertEquals(
                    expected = ImageInfo(480, 480, "image/gif"),
                    actual = imageInfo
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        ImageRequest(context, ResourceImages.animGif.uri) {
            colorSpace(ColorSpace.sRGB)
            onAnimationEnd { }
            onAnimationStart { }
            animatedTransformation(TranslucentAnimatedTransformation)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 480), actual = image.size)
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

        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 480), actual = image.size)
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
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)
        val element1 = SkiaGifDecoder(requestContext, dataSource)
        val element11 = SkiaGifDecoder(requestContext, dataSource)

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)
        val decoder = SkiaGifDecoder(requestContext, dataSource)
        assertTrue(actual = decoder.toString().contains("SkiaGifDecoder"))
        assertTrue(actual = decoder.toString().contains("@"))
    }

    @Test
    fun testFactoryConstructor() {
        Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "SkiaGifDecoder",
            actual = Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        // normal
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertTrue(actual = this is SkiaGifDecoder)
            }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(actual = this is SkiaGifDecoder)
            }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(actual = this is SkiaGifDecoder)
            }

        // disallowAnimatedImage true
        ImageRequest(context, ResourceImages.animGif.uri) {
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
                it.copy(mimeType = "image/gif")
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
            expected = "SkiaGifDecoder",
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