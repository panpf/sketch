package com.github.panpf.sketch.core.android.test.decode.internal

import com.github.panpf.sketch.BLURHASH_COLOR_TYPE
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.decode.FixedColorSpace
import com.github.panpf.sketch.decode.internal.Blurhash2DecodeHelper
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.Blurhash2DataSource
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

class SkiaBlurhash2DecodeHelperTest {

    val testableBlurhash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
    val testableBlurhashUri = "blurhash://$testableBlurhash&width=200&height=300"

    @Test
    fun testDecode() {

        val context = getTestContext()

        testableBlurhashUri.toDecodeHelper(context)
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
        testableBlurhashUri.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = BLURHASH_COLOR_TYPE,
                    actual = colorType,
                )
            }
        testableBlurhashUri.toDecodeHelper(context) {
            colorType(ColorType.RGB_565)
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
        testableBlurhashUri.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.sRGB,
                    actual = colorSpace,
                )
            }
        testableBlurhashUri.toDecodeHelper(context) {
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
    ): Blurhash2DecodeHelper {
        val request = ImageRequest(context, this, block)
        return Blurhash2DecodeHelper(
            request,
            Blurhash2DataSource(this, DataFrom.NETWORK),
        )
    }
}