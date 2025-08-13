package com.github.panpf.sketch.blurhash.nonandroid.test.decode.internal

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.decode.internal.BlurHashDecodeHelper
import com.github.panpf.sketch.fetch.newBlurHashUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import org.jetbrains.skia.ColorSpace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurHashDecodeHelperNonAndroidTest {

    private val blurHashUri = newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj", 200, 300)
    private val blurHashUriNoSize = newBlurHashUri("LEHV6nWB2yk8pyo0adR*.7kCMdnj")

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, blurHashUri)
        val requestContext = request.toRequestContext(sketch)
        BlurHashDecodeHelper(requestContext, blurHashUri.toUri())
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, blurHashUri)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .apply {
                assertEquals(expected = 200, actual = imageInfo.width)
                assertEquals(expected = 300, actual = imageInfo.height)
                assertEquals(expected = "image/jpeg", actual = imageInfo.mimeType)
            }

        ImageRequest(context, blurHashUriNoSize)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .apply {
                assertEquals(expected = 100, actual = imageInfo.width)
                assertEquals(expected = 100, actual = imageInfo.height)
                assertEquals(expected = "image/jpeg", actual = imageInfo.mimeType)
            }
    }

    @Test
    fun testSupportRegin() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        ImageRequest(context, blurHashUri)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .apply {
                assertFalse(supportRegion)
            }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val screenSize = context.screenSize()

        ImageRequest(context, blurHashUri)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=${screenSize.width}, height=${screenSize.height}, colorType=RGBA_8888, colorSpace=sRGB)",
                    actual = this.toInfoString()
                )
            }

        ImageRequest(context, blurHashUri) {
            size(700, 500)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=700, height=500, colorType=RGBA_8888, colorSpace=sRGB)",
                    actual = this.toInfoString()
                )
            }

        ImageRequest(context, blurHashUri) {
            size(Size.Origin)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=200, height=300, colorType=RGBA_8888, colorSpace=sRGB)",
                    actual = this.toInfoString()
                )
            }

        ImageRequest(context, blurHashUriNoSize) {
            size(Size.Origin)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=100, height=100, colorType=RGBA_8888, colorSpace=sRGB)",
                    actual = this.toInfoString()
                )
            }

        ImageRequest(context, blurHashUri) {
            size(Size.Origin)
            colorType(ColorType.BGRA_8888)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=200, height=300, colorType=BGRA_8888, colorSpace=sRGB)",
                    actual = this.toInfoString()
                )
            }

        ImageRequest(context, blurHashUri) {
            size(Size.Origin)
            colorType(ColorType.BGRA_8888)
            colorSpace(ColorSpace.displayP3)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                assertEquals(
                    expected = "Bitmap(width=200, height=300, colorType=BGRA_8888, colorSpace=displayP3)",
                    actual = this.toInfoString()
                )
            }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, blurHashUri)
        val helper = BlurHashDecodeHelper(request.toRequestContext(sketch), request.uri)
        assertFailsWith(UnsupportedOperationException::class) {
            helper.decodeRegion(Rect(0, 0, 100, 100), sampleSize = 1)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, blurHashUri)
        val requestContext = request.toRequestContext(sketch)
        val element1 = BlurHashDecodeHelper(requestContext, blurHashUri.toUri())
        val element11 = BlurHashDecodeHelper(requestContext, blurHashUri.toUri())

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
        val helper = BlurHashDecodeHelper(requestContext, blurHashUri.toUri())

        assertTrue(
            actual = helper.toString().contains("BlurHashDecodeHelper"),
            message = helper.toString()
        )
        assertTrue(actual = helper.toString().contains("@"), message = helper.toString())
    }
}