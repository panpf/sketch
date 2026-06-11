package com.github.panpf.sketch.core.ios.test.decode.internal

import com.github.panpf.sketch.asBitmap
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.UIImageDecodeHelper
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.similarity
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UIImageDecodeHelperTest {

    @Test
    fun testConstructor() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        UIImageDecodeHelper(dataSource = dataSource, mimeType = imageFile.mimeType)
        UIImageDecodeHelper(dataSource, imageFile.mimeType)
    }

    @Test
    fun testGetImageInfo() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        UIImageDecodeHelper(dataSource = dataSource, mimeType = imageFile.mimeType).apply {
            assertEquals(
                expected = ImageInfo(size = imageFile.size, mimeType = imageFile.mimeType),
                actual = getImageInfo()
            )
        }
    }

    @Test
    fun testIsSupportRegion() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        UIImageDecodeHelper(dataSource = dataSource, mimeType = imageFile.mimeType).apply {
            assertEquals(expected = true, actual = isSupportRegion())
        }
    }

    @Test
    fun testDecode() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        UIImageDecodeHelper(dataSource = dataSource, mimeType = imageFile.mimeType).apply {
            assertEquals(
                expected = imageFile.size,
                actual = decode(sampleSize = 1).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = imageFile.size, sampleSize = 2),
                actual = decode(sampleSize = 2).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = imageFile.size, sampleSize = 4),
                actual = decode(sampleSize = 4).size
            )
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val dataSource = imageFile.toDataSource(context)
        val fullRegion = Rect(0, 0, imageFile.size.width, imageFile.size.height)
        val region = Rect(200, 300, 703, 503)
        UIImageDecodeHelper(dataSource = dataSource, mimeType = imageFile.mimeType).apply {
            assertEquals(
                expected = imageFile.size,
                actual = decodeRegion(sampleSize = 1, region = fullRegion).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = imageFile.size, sampleSize = 2),
                actual = decodeRegion(sampleSize = 2, region = fullRegion).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = imageFile.size, sampleSize = 4),
                actual = decodeRegion(sampleSize = 4, region = fullRegion).size
            )

            val regionSize = Size(region.width(), region.height())
            assertEquals(
                expected = regionSize,
                actual = decodeRegion(sampleSize = 1, region = region).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = regionSize, sampleSize = 2),
                actual = decodeRegion(sampleSize = 2, region = region).size
            )

            assertEquals(
                expected = calculateSampledBitmapSize(imageSize = regionSize, sampleSize = 4),
                actual = decodeRegion(sampleSize = 4, region = region).size
            )

            val bitmap1 = decodeRegion(sampleSize = 1, region = region).asBitmap()
            val region2 = region.let {
                Rect(it)
                    .apply { offset(200, 200) }
            }
            val bitmap2 = decodeRegion(sampleSize = 1, region = region2).asBitmap()
            val similarity = bitmap1.similarity(bitmap2)
            assertTrue(
                similarity >= 5,
                "Similarity should be greater than or equal to 5, but was $similarity"
            )
        }

        // test exif orientation
        val bitmap1 = ComposeResImageFiles.clockExifNormal.let { imageFile ->
            val dataSource = imageFile.toDataSource(context)
            UIImageDecodeHelper(
                dataSource = dataSource,
                mimeType = imageFile.mimeType
            )
        }.decodeRegion(
            region = Rect(100, 200, 300, 300),
            sampleSize = 1
        ).asBitmap()
        val bitmap2 = ComposeResImageFiles.clockExifRotate90.let { imageFile ->
            val dataSource = imageFile.toDataSource(context)
            UIImageDecodeHelper(
                dataSource = dataSource,
                mimeType = imageFile.mimeType
            )
        }.decodeRegion(
            region = Rect(100, 200, 300, 300),
            sampleSize = 1
        ).asBitmap()
        assertEquals(bitmap1.size, bitmap2.size)
        val similarity = bitmap1.similarity(bitmap2)
        assertTrue(
            similarity <= 2,
            "Similarity should be less than or equal to 2, but was $similarity"
        )
    }
}