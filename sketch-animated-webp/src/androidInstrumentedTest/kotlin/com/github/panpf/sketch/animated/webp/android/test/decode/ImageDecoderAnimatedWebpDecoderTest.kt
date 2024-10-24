package com.github.panpf.sketch.animated.webp.android.test.decode

import android.graphics.ColorSpace.Named.SRGB
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder
import com.github.panpf.sketch.decode.ImageDecoderAnimatedWebpDecoder.Factory
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.supportImageDecoderAnimatedWebp
import com.github.panpf.sketch.drawable.AnimatableDrawable
import com.github.panpf.sketch.drawable.ScaledAnimatedImageDrawable
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
class ImageDecoderAnimatedWebpDecoderTest {

    @Test
    fun testSupportAnimatedWebp() {
        if (VERSION.SDK_INT < VERSION_CODES.P) return

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
            supportImageDecoderAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[ImageDecoderAnimatedWebpDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportImageDecoderAnimatedWebp()
            supportImageDecoderAnimatedWebp()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetcherFactoryList=[]," +
                        "decoderFactoryList=[ImageDecoderAnimatedWebpDecoder,ImageDecoderAnimatedWebpDecoder]," +
                        "requestInterceptorList=[]," +
                        "decodeInterceptorList=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testConstructor() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)

        ImageDecoderAnimatedWebpDecoder(requestContext, dataSource)
        ImageDecoderAnimatedWebpDecoder(requestContext = requestContext, dataSource = dataSource)
    }

    @Test
    fun testImageInfo() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

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
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        ImageRequest(context, ResourceImages.animWebp.uri) {
            colorSpace(SRGB)
            onAnimationEnd { }
            onAnimationStart { }
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(480, 270), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(expected = null, actual = this.transformeds)
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<AnimatableDrawable>().drawable!!
                .asOrThrow<ScaledAnimatedImageDrawable>()
                .drawable
            assertEquals(expected = -1, actual = animatedImageDrawable.repeatCount)
        }

        ImageRequest(context, ResourceImages.animWebp.uri) {
            repeatCount(3)
            size(300, 300)
        }.decode(sketch, factory).apply {
            assertEquals(expected = ImageInfo(480, 270, "image/webp"), actual = this.imageInfo)
            assertEquals(expected = Size(240, 135), actual = image.size)
            assertEquals(expected = LOCAL, actual = this.dataFrom)
            assertEquals(
                expected = listOf(createInSampledTransformed(2)),
                actual = this.transformeds
            )
            val animatedImageDrawable = image.getDrawableOrThrow()
                .asOrThrow<AnimatableDrawable>().drawable!!
                .asOrThrow<ScaledAnimatedImageDrawable>()
                .drawable
            assertEquals(expected = 3, actual = animatedImageDrawable.repeatCount)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val request = ImageRequest(context, ResourceImages.animWebp.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = ResourceImages.animWebp.toDataSource(context)
        val element1 = ImageDecoderAnimatedWebpDecoder(requestContext, dataSource)
        val element11 = ImageDecoderAnimatedWebpDecoder(requestContext, dataSource)

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
        val decoder = ImageDecoderAnimatedWebpDecoder(requestContext, dataSource)
        assertTrue(
            actual = decoder.toString().contains("ImageDecoderAnimatedWebpDecoder"),
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
            expected = "ImageDecoderAnimatedWebpDecoder",
            actual = Factory().key
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        if (VERSION.SDK_INT < VERSION_CODES.P) return@runTest

        val (context, sketch) = getTestContextAndSketch()
        val factory = Factory()

        // normal
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertTrue(this is ImageDecoderAnimatedWebpDecoder)
            }

        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = null)
            }.apply {
                assertTrue(this is ImageDecoderAnimatedWebpDecoder)
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

        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
            }.apply {
                assertNull(this)
            }

        // mimeType error
        ImageRequest(context, ResourceImages.animWebp.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/jpeg")
            }.apply {
                assertTrue(this is ImageDecoderAnimatedWebpDecoder)
            }

        // Disguised, mimeType; data error
        ImageRequest(context, ResourceImages.png.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(mimeType = "image/webp")
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
            expected = "ImageDecoderAnimatedWebpDecoder",
            actual = Factory().toString()
        )
    }
}