package com.github.panpf.sketch.avif.ios.test.decode

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.AppleAvifDecoder
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.supportAvif
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision.LESS_PIXELS
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.createDecoderOrNull
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppleAvifDecoderTest {

    @Test
    fun testSupportAvif() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAvif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[AppleAvifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportAvif()
            supportAvif()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[AppleAvifDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testCompanion() {
        assertEquals(expected = "image/avif", actual = AppleAvifDecoder.MIME_TYPE)
        assertEquals(expected = 0, actual = AppleAvifDecoder.SORT_WEIGHT)
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()

        ImageRequest(context, ComposeResImageFiles.avif.uri)
            .createDecoderOrNull(sketch, factory)!!.apply {
                assertEquals(
                    expected = ImageInfo(1204, 800, "image/avif"),
                    actual = getImageInfo()
                )
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()

        ImageRequest(context, ComposeResImageFiles.avif.uri) {
            size(3000, 3000)
            precision(LESS_PIXELS)
        }.createDecoderOrNull(sketch, factory)!!.decode().apply {
            assertEquals(expected = ImageInfo(1204, 800, "image/avif"), actual = imageInfo)
            assertTrue(actual = image is BitmapImage)
            assertEquals(expected = Size(1204, 800), actual = image.size)
            assertEquals(expected = LOCAL, actual = dataFrom)
            assertNull(actual = transformeds)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()
        val request = ImageRequest(context, ComposeResImageFiles.avif.uri)
        val element1 = request.createDecoderOrNull(sketch, factory)!!
        val element11 = request.createDecoderOrNull(sketch, factory)!!

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()

        val decoder = ImageRequest(context, ComposeResImageFiles.avif.uri)
            .createDecoderOrNull(sketch, factory)!!
        assertTrue(
            actual = decoder.toString().contains("AppleAvifDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        AppleAvifDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "AppleAvifDecoder",
            actual = AppleAvifDecoder.Factory().key
        )
    }

    @Test
    fun testFactorySortWeight() {
        assertEquals(
            expected = 0,
            actual = AppleAvifDecoder.Factory().sortWeight
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()
        val jpegDataSource = ComposeResImageFiles.jpeg.toDataSource(context)

        assertNull(
            actual = ImageRequest(context, ComposeResImageFiles.jpeg.uri)
                .createDecoderOrNull(sketch, factory) {
                    it.copy(mimeType = "image/jpeg")
                }
        )

        assertNotNull(
            actual = ImageRequest(context, ComposeResImageFiles.avif.uri)
                .createDecoderOrNull(sketch, factory)
        )

        ImageRequest(context, ComposeResImageFiles.jpeg.uri)
            .createDecoderOrNull(sketch, factory) {
                it.copy(
                    dataSource = jpegDataSource,
                    mimeType = "image/avif"
                )
            }.apply {
                assertNotNull(actual = this)
                assertTrue(actual = this is AppleAvifDecoder)
            }
    }

    @Test
    fun testFactoryCreateByAvifUri() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = AppleAvifDecoder.Factory()
        val request = ImageRequest(context, "https://example.com/sample.avif?width=100")
        val requestContext = request.toRequestContext(sketch)
        val fetchResult = FetchResult(
            dataSource = ComposeResImageFiles.jpeg.toDataSource(context),
            mimeType = "image/jpeg"
        )

        factory.create(requestContext, fetchResult)!!.apply {
            assertTrue(actual = this is AppleAvifDecoder)
            decode().apply {
                assertEquals(expected = ImageInfo(1291, 1936, "image/avif"), actual = imageInfo)
                assertTrue(actual = image is BitmapImage)
                assertEquals(expected = Size(646, 968), actual = image.size)
                assertEquals(expected = LOCAL, actual = dataFrom)
            }
        }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = AppleAvifDecoder.Factory()
        val element11 = AppleAvifDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "AppleAvifDecoder",
            actual = AppleAvifDecoder.Factory().toString()
        )
    }

}
