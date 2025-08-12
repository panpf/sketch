package com.github.panpf.sketch.blurhash.nonandroid.test.decode.internal

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.FixedColorSpace
import com.github.panpf.sketch.decode.internal.BlurHashDecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.BlurHashDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Size
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SkiaBlurHashDecodeHelperTest {

    val testableBlurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    val testableBlurHashUri = "blurhash://$testableBlurHash?width=200&height=300"

    @Test
    fun testDecode() {

        val context = getTestContext()

        testableBlurHashUri.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = Size(width = 200, height = 300),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * colorType is ignored
         */
        testableBlurHashUri.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGBA_8888,
                    actual = colorType,
                )
            }
        testableBlurHashUri.toDecodeHelper(context) {
            colorType(ColorType.RGB_565)
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGBA_8888,
                    actual = colorType,
                )
            }

        /*
         * config: colorSpace is ignored
         */
        testableBlurHashUri.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.sRGB,
                    actual = colorSpace,
                )
            }
        this@SkiaBlurHashDecodeHelperTest.testableBlurHashUri.toDecodeHelper(context) {
            colorSpace(FixedColorSpace("Display P3"))
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.sRGB,
                    actual = colorSpace,
                )
            }

        /*
         * error
         */
        // IllegalArgumentException: Failed to Image::makeFromEncoded
        assertFailsWith(IllegalArgumentException::class) {
            "malformed".toDecodeHelper(context).decode(sampleSize = 1)
        }
    }

    private fun String.toDecodeHelper(
        context: PlatformContext,
        block: (ImageRequest.Builder.() -> Unit)? = null
    ): BlurHashDecodeHelper {
        val request = ImageRequest(context, this, block)
        return BlurHashDecodeHelper(
            request,
            BlurHashDataSource(this, DataFrom.NETWORK),
        )
    }
}