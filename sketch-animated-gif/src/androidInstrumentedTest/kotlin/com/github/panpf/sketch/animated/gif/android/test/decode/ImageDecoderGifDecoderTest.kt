package com.github.panpf.sketch.animated.gif.android.test.decode

import android.graphics.ColorSpace.Named.SRGB
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder.Factory
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportImageDecoderGif
import com.github.panpf.sketch.drawable.ScaledAnimatableDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
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
import com.github.panpf.sketch.test.utils.getDrawableOrThrow
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageDecoderGifDecoderTest {

    @Test
    fun testSupportImageDecoderGif() {
        if (VERSION.SDK_INT < VERSION_CODES.P) return

        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportImageDecoderGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[ImageDecoderGifDecoder]," +
                        "interceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportImageDecoderGif()
            supportImageDecoderGif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[ImageDecoderGifDecoder,ImageDecoderGifDecoder]," +
                        "interceptors=[]" +
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

        ImageDecoderGifDecoder(requestContext, dataSource)
        ImageDecoderGifDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

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
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        ImageRequest(context, ResourceImages.animGif.uri) {
            colorSpace(SRGB)
            onAnimationEnd { }
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 480), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            assertTrue(image is DrawableImage)
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<ScaledAnimatableDrawable>()
                .drawable as AnimatedImageDrawable
            assertEquals(expected = -1, actual = animatedImageDrawable.repeatCount)
        }

        ImageRequest(context, ResourceImages.animGif.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 480, "image/gif"), actual = this.imageInfo)
            assertEquals(expected = Size(240, 240), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(
                expected = listOf(createInSampledTransformed(2)),
                actual = this.transformeds
            )
            assertTrue(image is DrawableImage)
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<ScaledAnimatableDrawable>()
                .drawable as AnimatedImageDrawable
            assertEquals(expected = 3, actual = animatedImageDrawable.repeatCount)
        }

        ImageRequest(context, ResourceImages.singleFrameGif.uri)
            .decode(sketch, factory)
            .apply {
                assertEquals(expected = ImageInfo(500, 667, "image/gif"), actual = this.imageInfo)
                assertEquals(expected = Size(500, 667), actual = image.size)
                assertEquals(expected = LOCAL, actual = this.dataFrom)
                assertTrue(image is BitmapImage)
            }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animGif.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animGif.toDataSource(context)
        val element1 = ImageDecoderGifDecoder(requestContext, dataSource)
        val element11 = ImageDecoderGifDecoder(requestContext, dataSource)

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
        val decoder = ImageDecoderGifDecoder(requestContext, dataSource)
        assertTrue(
            actual = decoder.toString().contains("ImageDecoderGifDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "ImageDecoderGifDecoder",
            actual = Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        // normal
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/gif")
            }.apply {
                assertTrue(this is ImageDecoderGifDecoder)
            }

        // no mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is ImageDecoderGifDecoder)
            }

        // Disguised mimeType
        ImageRequest(context, ResourceImages.animGif.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is ImageDecoderGifDecoder)
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
            expected = "ImageDecoderGifDecoder",
            actual = Factory().toString()
        )
    }
}