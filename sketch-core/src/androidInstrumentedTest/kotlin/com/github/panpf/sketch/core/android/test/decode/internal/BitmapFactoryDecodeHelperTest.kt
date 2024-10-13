@file:Suppress("DEPRECATION")

package com.github.panpf.sketch.core.android.test.decode.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorSpace
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.ColorType
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.colorType
import com.github.panpf.sketch.createBitmap
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.internal.BitmapFactoryDecodeHelper
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSizeForRegion
import com.github.panpf.sketch.decode.internal.readImageInfo
import com.github.panpf.sketch.images.ResourceImageFile
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.images.toDataSource
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.colorSpace
import com.github.panpf.sketch.request.colorType
import com.github.panpf.sketch.request.preferQualityOverSpeed
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.test.utils.asOrThrow
import com.github.panpf.sketch.test.utils.assertSizeEquals
import com.github.panpf.sketch.test.utils.chunkingFour
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.test.utils.toRect
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.safeToSoftware
import com.github.panpf.sketch.util.size
import com.github.panpf.sketch.util.toAndroidRect
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BitmapFactoryDecodeHelperTest {

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
            .asOrThrow<BitmapImage>().bitmap
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
                    expected = ColorType.ARGB_8888,
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
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            imageFile.toDecodeHelper(context)
                .decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
            imageFile.toDecodeHelper(context) {
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }.decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                        actual = colorSpace,
                    )
                }
        }

        /*
         * config: preferQualityOverSpeed
         */
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            val bitmap = imageFile.toDecodeHelper(context) {
                preferQualityOverSpeed(true)
            }.decode(sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
            var preferSpeedBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferSpeedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferSpeedBitmap = imageFile.toDecodeHelper(
                    context = context,
                    dataSource = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                ) {
                    preferQualityOverSpeed(false)
                }.decode(sampleSize = 1)
                    .asOrThrow<BitmapImage>().bitmap
            }

            var preferQualityBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferQualityBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferQualityBitmap = imageFile.toDecodeHelper(
                    context = context,
                    dataSource = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                ) {
                    preferQualityOverSpeed(true)
                }.decode(sampleSize = 1)
                    .asOrThrow<BitmapImage>().bitmap
            }

            val preferSpeedSimilarity = bitmap.similarity(preferSpeedBitmap)
            val preferQualitySimilarity = bitmap.similarity(preferQualityBitmap)
            assertEquals(expected = 10, actual = preferSpeedSimilarity)
            assertEquals(expected = 0, actual = preferQualitySimilarity)
        }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ResourceImages.clockExifs.forEach { exifImageFile ->
            val bitmap = exifImageFile.toDecodeHelper(context)
                .decode(sampleSize = 1)
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
        assertFailsWith(ImageInvalidException::class) {
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
            sampleSize = 1
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
        val mergedBitmap = createBitmap(
            width = imageInfo.width,
            height = imageInfo.height,
            config = topLeftBitmap.colorType.safeToSoftware()
        ).apply {
            val canvas = Canvas(this)
            canvas.drawBitmap(
                /* bitmap = */ topLeftBitmap,
                /* src = */ topLeftBitmap.size.toRect().toAndroidRect(),
                /* dst = */ topLeftRect.toAndroidRect(),
                /* paint = */ null
            )
            canvas.drawBitmap(
                /* bitmap = */ topRightBitmap,
                /* src = */ topRightBitmap.size.toRect().toAndroidRect(),
                /* dst = */ topRightRect.toAndroidRect(),
                /* paint = */ null
            )
            canvas.drawBitmap(
                /* bitmap = */ bottomLeftBitmap,
                /* src = */ bottomLeftBitmap.size.toRect().toAndroidRect(),
                /* dst = */ bottomLeftRect.toAndroidRect(),
                /* paint = */ null
            )
            canvas.drawBitmap(
                /* bitmap = */ bottomRightBitmap,
                /* src = */ bottomRightBitmap.size.toRect().toAndroidRect(),
                /* dst = */ bottomRightRect.toAndroidRect(),
                /* paint = */ null
            )
        }
        sourceBitmap.similarity(mergedBitmap).apply {
            assertTrue(
                actual = this <= 6,
                message = "similarity=$this"
            )
        }

        /*
         * config: sampleSize
         */
        imageFile.toDecodeHelper(context)
            .decodeRegion(region = imageInfo.size.toRect(), sampleSize = 1)
            .asOrThrow<BitmapImage>().bitmap
            .apply {
                assertSizeEquals(
                    expected = imageFile.size,
                    actual = size,
                    delta = Size(1, 1)
                )
            }
        imageFile.toDecodeHelper(context)
            .decodeRegion(region = imageInfo.size.toRect(), sampleSize = 2)
            .asOrThrow<BitmapImage>().bitmap
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
                    expected = ColorType.ARGB_8888,
                    actual = colorType,
                )
            }
        imageFile.toDecodeHelper(context) {
            colorType(ColorType.RGB_565)
        }.decodeRegion(region = imageInfo.size.toRect(), sampleSize = 1)
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
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            imageFile.toDecodeHelper(context)
                .decodeRegion(region = imageInfo.size.toRect(), sampleSize = 1)
                .asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.SRGB),
                        actual = colorSpace,
                    )
                }
            imageFile.toDecodeHelper(context) {
                colorSpace(ColorSpace.Named.DISPLAY_P3)
            }.decodeRegion(
                region = imageInfo.size.toRect(),
                sampleSize = 1
            ).asOrThrow<BitmapImage>().bitmap
                .apply {
                    assertEquals(
                        expected = ColorSpace.get(ColorSpace.Named.DISPLAY_P3),
                        actual = colorSpace,
                    )
                }
        }

        /*
         * config: preferQualityOverSpeed
         */
        if (VERSION.SDK_INT <= VERSION_CODES.M) {
            val bitmap = imageFile.toDecodeHelper(context) {
                preferQualityOverSpeed(true)
            }.decodeRegion(
                region = imageInfo.size.toRect(),
                sampleSize = 1
            ).asOrThrow<BitmapImage>().bitmap
            var preferSpeedBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferSpeedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferSpeedBitmap = imageFile.toDecodeHelper(
                    context = context,
                    dataSource = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                ) {
                    preferQualityOverSpeed(false)
                }.decodeRegion(
                    region = imageInfo.size.toRect(),
                    sampleSize = 1
                ).asOrThrow<BitmapImage>().bitmap
            }

            var preferQualityBitmap = bitmap
            repeat(10) {
                val outputStream = ByteArrayOutputStream()
                preferQualityBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                preferQualityBitmap = imageFile.toDecodeHelper(
                    context = context,
                    dataSource = ByteArrayDataSource(outputStream.toByteArray(), LOCAL)
                ) {
                    preferQualityOverSpeed(true)
                }.decodeRegion(
                    region = imageInfo.size.toRect(),
                    sampleSize = 1
                ).asOrThrow<BitmapImage>().bitmap
            }

            val preferSpeedSimilarity = bitmap.similarity(preferSpeedBitmap)
            val preferQualitySimilarity = bitmap.similarity(preferQualityBitmap)
            assertEquals(expected = 10, actual = preferSpeedSimilarity)
            assertEquals(expected = 0, actual = preferQualitySimilarity)
        }

        /*
         * exif
         */
        var firstBitmap: Bitmap? = null
        ResourceImages.clockExifs.forEach { exifImageFile ->
            val bitmap = exifImageFile.toDecodeHelper(context)
                .decodeRegion(exifImageFile.size.toRect(), sampleSize = 1)
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
        // ImageInvalidException: Invalid image. width or height is 0. -1x-1
        assertFailsWith(ImageInvalidException::class) {
            val svgImageFile = ResourceImages.svg
            svgImageFile.toDecodeHelper(context).decodeRegion(
                region = svgImageFile.size.toRect(),
                sampleSize = 1
            )
        }
        // IllegalArgumentException: rectangle is outside the image
        assertFailsWith(IllegalArgumentException::class) {
            ResourceImages.jpeg.toDecodeHelper(context).decodeRegion(
                region = Size.Empty.toRect(),
                sampleSize = 1
            )
        }
    }

    @Test
    fun testToString() {
        val context = getTestContext()
        val request = ImageRequest(context, ResourceImages.jpeg.uri)
        val dataSource = ResourceImages.jpeg.toDataSource(context)
        val decodeHelper = BitmapFactoryDecodeHelper(request, dataSource)
        assertEquals(
            expected = "BitmapFactoryDecodeHelper(request=$request, dataSource=$dataSource)",
            actual = decodeHelper.toString()
        )
    }

    private fun ResourceImageFile.toDecodeHelper(
        context: PlatformContext,
        dataSource: DataSource? = null,
        configBlock: (ImageRequest.Builder.() -> Unit)? = null
    ): BitmapFactoryDecodeHelper {
        return BitmapFactoryDecodeHelper(
            request = ImageRequest(context, uri, configBlock),
            dataSource = dataSource ?: toDataSource(context)
        )
    }
}