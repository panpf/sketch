package com.github.panpf.sketch.core.android.test.decode.internal

import android.graphics.Bitmap
import android.graphics.ColorSpace
import com.github.panpf.sketch.BLURHASH_COLOR_TYPE
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.colorType
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AndroidBlurHashDecodeHelperTest {

    val testableBlurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    val testableBlurHashUri = "blurhash://$testableBlurHash&width=200&height=300"

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
                    expected = BLURHASH_COLOR_TYPE,
                    actual = colorType,
                )
            }
        this@AndroidBlurHashDecodeHelperTest.testableBlurHashUri.toDecodeHelper(context) {
            colorType(Bitmap.Config.RGB_565)
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = BLURHASH_COLOR_TYPE,
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
                val actual = colorSpace
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.SRGB),
                    actual = actual,
                )
            }
        testableBlurHashUri.toDecodeHelper(context) {
            colorSpace(FixedColorSpace(ColorSpace.Named.DISPLAY_P3.name))
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.get(ColorSpace.Named.SRGB),
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
            Size(200, 200)
        )
    }
}