package com.github.panpf.sketch.core.nonandroid.test.decode.internal

import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.decode.internal.SkiaDecodeHelper
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.readImageInfo
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.chunkingFour
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.size
import com.github.panpf.sketch.test.utils.toPreviewBitmap
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.size
import com.github.panpf.sketch.util.toSkiaRect
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SkiaDecodeHelperTest {

    @Test
    fun testDecode() {
        val context = getTestContext()

        /*
         * config: sampleSize
         */
        val imageFile = ResourceImages.jpeg
        imageFile.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
        imageFile.toDecodeHelper(context)
            .decode(sampleSize = 2)
            .apply {
                assertSizeEquals(
                    expected = calculateSampledBitmapSize(imageFile.size, 2),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * config: colorType
         */
        imageFile.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGBA_8888,
                    actual = colorType,
                )
            }
        imageFile.toDecodeHelper(context) {
            colorType(ColorType.RGB_565)
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGB_565,
                    actual = colorType,
                )
            }

        /*
         * config: colorSpace
         */
        imageFile.toDecodeHelper(context)
            .decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.sRGB,
                    actual = colorSpace,
                )
            }
        imageFile.toDecodeHelper(context) {
            colorSpace(ColorSpace.displayP3)
        }.decode(sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.displayP3,
                    actual = colorSpace,
                )
            }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ResourceImages.clockExifs.forEach { exifImageFile ->
            val bitmap = exifImageFile.toDecodeHelper(context).decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
            assertEquals(
                expected = exifImageFile.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            val firstBitmap1 = firstBitmap ?: bitmap.apply { firstBitmap = this }
            assertEquals(
                expected = firstBitmap1.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            assertEquals(
                expected = 0,
                actual = firstBitmap1.similarity(bitmap),
                message = "imageFile=${exifImageFile.uri}"
            )
        }

        /*
         * error
         */
        // IllegalArgumentException: Failed to Image::makeFromEncoded
        assertFailsWith(IllegalArgumentException::class) {
            ResourceImages.svg.toDecodeHelper(context).decode(sampleSize = 1)
        }
    }

    @Test
    fun testDecodeRegion() {
        val context = getTestContext()

        /*
         * srcRect
         */
        val imageFile = ResourceImages.jpeg
        val dataSource = imageFile.toDataSource(context)
        val imageInfo = dataSource.readImageInfo()

        val sourceBitmap = imageFile.toDecodeHelper(context).decodeRegion(
            region = imageInfo.size.toRect(),
            sampleSize = 1,
        ).asOrThrow<BitmapImage>().bitmap.apply {
            assertEquals(
                expected = imageInfo.size,
                actual = this.size,
            )
        }

        // Divide into four pieces
        val (topLeftRect, topRightRect, bottomLeftRect, bottomRightRect) =
            imageInfo.size.toRect().chunkingFour()
        val topLeftBitmap = imageFile.toDecodeHelper(context)
            .decodeRegion(region = topLeftRect, sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
        val topRightBitmap = imageFile.toDecodeHelper(context)
            .decodeRegion(region = topRightRect, sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
        val bottomLeftBitmap = imageFile.toDecodeHelper(context)
            .decodeRegion(region = bottomLeftRect, sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
        val bottomRightBitmap = imageFile.toDecodeHelper(context)
            .decodeRegion(region = bottomRightRect, sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
        listOf(
            topLeftBitmap to topLeftRect,
            topRightBitmap to topRightRect,
            bottomLeftBitmap to bottomLeftRect,
            bottomRightBitmap to bottomRightRect
        ).forEach { (bitmap, tileRect) ->
            assertSizeEquals(
                expected = calculateSampledBitmapSizeForRegion(
                    regionSize = tileRect.size,
                    sampleSize = 1,
                ),
                actual = bitmap.size,
                delta = Size(1, 1),
                message = "tileRect=$tileRect"
            )
        }
        listOf(
            topLeftBitmap.similarity(topRightBitmap),
            topLeftBitmap.similarity(bottomLeftBitmap),
            topLeftBitmap.similarity(bottomRightBitmap),
            topRightBitmap.similarity(bottomLeftBitmap),
            topRightBitmap.similarity(bottomRightBitmap),
            bottomLeftBitmap.similarity(bottomRightBitmap)
        ).forEachIndexed { index, similarity ->
            assertTrue(
                actual = similarity >= 4,
                message = "index=$index, similarity=$similarity"
            )
        }

        // Merge four pictures
        val mergedBitmap = SkiaBitmap(
            width = imageInfo.width,
            height = imageInfo.height,
            colorType = topLeftBitmap.colorType
        ).apply {
            val canvas = Canvas(this)
            canvas.drawImageRect(
                /* bitmap = */ SkiaImage.makeFromBitmap(topLeftBitmap),
                /* src = */ topLeftBitmap.size.toRect().toSkiaRect(),
                /* dst = */ topLeftRect.toSkiaRect(),
                /* paint = */ null
            )
            canvas.drawImageRect(
                /* bitmap = */  SkiaImage.makeFromBitmap(topRightBitmap),
                /* src = */ topRightBitmap.size.toRect().toSkiaRect(),
                /* dst = */ topRightRect.toSkiaRect(),
                /* paint = */ null
            )
            canvas.drawImageRect(
                /* bitmap = */  SkiaImage.makeFromBitmap(bottomLeftBitmap),
                /* src = */ bottomLeftBitmap.size.toRect().toSkiaRect(),
                /* dst = */ bottomLeftRect.toSkiaRect(),
                /* paint = */ null
            )
            canvas.drawImageRect(
                /* bitmap = */  SkiaImage.makeFromBitmap(bottomRightBitmap),
                /* src = */ bottomRightBitmap.size.toRect().toSkiaRect(),
                /* dst = */ bottomRightRect.toSkiaRect(),
                /* paint = */ null
            )
        }
        @Suppress("UNUSED_VARIABLE") val previewSourceBitmap =   // Preview for debugging
            sourceBitmap.toPreviewBitmap()
        @Suppress("UNUSED_VARIABLE") val mergedSourceBitmap =   // Preview for debugging
            mergedBitmap.toPreviewBitmap()
        @Suppress("UNUSED_VARIABLE") val topLeftSourceBitmap =   // Preview for debugging
            topLeftBitmap.toPreviewBitmap()
        @Suppress("UNUSED_VARIABLE") val topRightSourceBitmap =   // Preview for debugging
            topRightBitmap.toPreviewBitmap()
        @Suppress("UNUSED_VARIABLE") val bottomLeftSourceBitmap =   // Preview for debugging
            bottomLeftBitmap.toPreviewBitmap()
        @Suppress("UNUSED_VARIABLE") val bottomRightSourceBitmap =
            // Preview for debugging
            bottomRightBitmap.toPreviewBitmap()
        sourceBitmap.similarity(mergedBitmap).apply {
            assertTrue(
                actual = this <= 6,
                message = "similarity=$this"
            )
        }

        /*
         * config: sampleSize
         */
        imageFile.toDecodeHelper(context).decodeRegion(
            region = imageInfo.size.toRect(),
            sampleSize = 1
        ).asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
        imageFile.toDecodeHelper(context).decodeRegion(
            region = imageInfo.size.toRect(),
            sampleSize = 2
        ).asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = calculateSampledBitmapSize(imageFile.size, 2),
                    actual = size,
                    delta = Size(1, 1)
                )
            }

        /*
         * config: colorType
         */
        imageFile.toDecodeHelper(context)
            .decodeRegion(region = imageInfo.size.toRect(), sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGBA_8888,
                    actual = colorType,
                )
            }
        imageFile.toDecodeHelper(context) {
            colorType(ColorType.RGB_565)
        }.decodeRegion(imageInfo.size.toRect(), 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorType.RGB_565,
                    actual = colorType,
                )
            }

        /*
         * config: colorSpace
         */
        imageFile.toDecodeHelper(context)
            .decodeRegion(imageInfo.size.toRect(), 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.sRGB,
                    actual = colorSpace,
                )
            }
        imageFile.toDecodeHelper(context) {
            colorSpace(ColorSpace.displayP3)
        }.decodeRegion(region = imageInfo.size.toRect(), sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertEquals(
                    expected = ColorSpace.displayP3,
                    actual = colorSpace,
                )
            }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ResourceImages.clockExifs.forEach { exifImageFile ->
            val bitmap = exifImageFile.toDecodeHelper(context)
                .decodeRegion(exifImageFile.size.toRect(), 1)
                .asOrThrow<BitmapImage>().bitmap
            assertEquals(
                expected = exifImageFile.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            val firstBitmap1 = firstBitmap ?: bitmap.apply { firstBitmap = this }
            assertEquals(
                expected = firstBitmap1.size,
                actual = bitmap.size,
                message = "imageFile=${exifImageFile.uri}"
            )
            assertEquals(
                expected = 0,
                actual = firstBitmap1.similarity(bitmap),
                message = "imageFile=${exifImageFile.uri}"
            )
        }

        /*
         * error
         */
        // IllegalArgumentException: Failed to Image::makeFromEncoded
        assertFailsWith(IllegalArgumentException::class) {
            val svgImageFile = ResourceImages.svg
            svgImageFile.toDecodeHelper(context)
                .decodeRegion(svgImageFile.size.toRect(), 1)
        }
        // IllegalArgumentException: srcRect is empty
        assertFailsWith(IllegalArgumentException::class) {
            ResourceImages.jpeg.toDecodeHelper(context)
                .decodeRegion(Size.Empty.toRect(), 1)
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val dataSource = ResourceImages.jpeg.toDataSource(context)
        val decodeHelper = SkiaDecodeHelper(request, dataSource)
        assertEquals(
            expected = "SkiaDecodeHelper(request=$request, dataSource=$dataSource)",
            actual = decodeHelper.toString()
        )
    }

    private fun ResourceImageFile.toDecodeHelper(
        context: PlatformContext,
        configBlock: (ImageRequest.Builder.() -> Unit)? = null
    ): SkiaDecodeHelper {
        return SkiaDecodeHelper(
            request = ImageRequest(context, uri, configBlock),
            dataSource = toDataSource(context)
        )
    }
}