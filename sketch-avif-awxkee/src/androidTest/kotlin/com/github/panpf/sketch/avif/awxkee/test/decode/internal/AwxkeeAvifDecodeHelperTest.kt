package com.github.panpf.sketch.avif.awxkee.test.decode.internal

import android.os.Build
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.AwxkeeAvifDecodeHelper
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.size
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Rect
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AwxkeeAvifDecodeHelperTest {

    @Test
    fun testConstructor() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        val request = ImageRequest(context, imageFile.uri)
        val dataSource = imageFile.toDataSource(context)
        AwxkeeAvifDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = imageFile.mimeType
        )
        AwxkeeAvifDecodeHelper(request, dataSource, imageFile.mimeType)
    }

    @Test
    fun testGetImageInfo() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val context = getTestContext()

        val avifImageFile = ComposeResImageFiles.avif
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, avifImageFile.uri),
            dataSource = avifImageFile.toDataSource(context),
            mimeType = avifImageFile.mimeType
        ).apply {
            assertEquals(
                expected = ImageInfo(size = avifImageFile.size, mimeType = avifImageFile.mimeType),
                actual = getImageInfo()
            )
        }

        val heifImageFile = ComposeResImageFiles.heic
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, heifImageFile.uri),
            dataSource = heifImageFile.toDataSource(context),
            mimeType = heifImageFile.mimeType
        ).apply {
            assertEquals(
                expected = ImageInfo(size = heifImageFile.size, mimeType = heifImageFile.mimeType),
                actual = getImageInfo()
            )
        }

        val jpegImageFile = ComposeResImageFiles.jpeg
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, jpegImageFile.uri),
            dataSource = jpegImageFile.toDataSource(context),
            mimeType = jpegImageFile.mimeType
        ).apply {
            assertFailsWith(Exception::class) {
                getImageInfo()
            }
        }
    }

    @Test
    fun testIsSupportRegion() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, imageFile.uri),
            dataSource = imageFile.toDataSource(context),
            mimeType = imageFile.mimeType
        ).apply {
            assertEquals(expected = false, actual = isSupportRegion())
        }
    }

    @Test
    fun testDecode() = runTest {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, imageFile.uri),
            dataSource = imageFile.toDataSource(context),
            mimeType = imageFile.mimeType
        ).apply {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return@runTest
        val context = getTestContext()
        val imageFile = ComposeResImageFiles.avif
        AwxkeeAvifDecodeHelper(
            request = ImageRequest(context, imageFile.uri),
            dataSource = imageFile.toDataSource(context),
            mimeType = imageFile.mimeType
        ).apply {
            assertFailsWith(UnsupportedOperationException::class) {
                decodeRegion(sampleSize = 1, region = Rect(200, 300, 703, 503))
            }
        }
    }
}