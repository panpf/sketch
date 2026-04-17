package com.github.panpf.sketch.blurhash.android.test.decode.internal

import android.graphics.ColorSpace
import android.os.Build
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
import com.github.panpf.sketch.util.toInfoString
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BlurHashDecodeHelperAndroidTest {

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

        ImageRequest(context, blurHashUri)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUri) {
            size(700, 500)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUri)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(2)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=100, height=150, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=100, height=150, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUriNoSize)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=100, height=100, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=100, height=100, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUriNoSize) {
            size(700, 500)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=100, height=100, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=100, height=100, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUriNoSize)
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(2)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=50, height=50, config=ARGB_8888, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=50, height=50, config=ARGB_8888)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUri) {
            colorType(ColorType.RGB_565)
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=RGB_565, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=RGB_565)",
                        actual = this.toInfoString()
                    )
                }
            }

        ImageRequest(context, blurHashUri) {
            colorType(ColorType.RGB_565)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }
        }
            .let { BlurHashDecodeHelper(it.toRequestContext(sketch), it.uri) }
            .decode(1)
            .let { (it as BitmapImage).bitmap }
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=RGB_565, colorSpace=DISPLAY_P3)",
                        actual = this.toInfoString()
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=RGB_565, colorSpace=SRGB)",
                        actual = this.toInfoString()
                    )
                } else {
                    assertEquals(
                        expected = "Bitmap(width=200, height=300, config=RGB_565)",
                        actual = this.toInfoString()
                    )
                }
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