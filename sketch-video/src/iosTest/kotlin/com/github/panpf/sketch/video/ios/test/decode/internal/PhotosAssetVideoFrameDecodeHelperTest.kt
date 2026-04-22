package com.github.panpf.sketch.video.ios.test.decode.internal

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.PhotosAssetVideoFrameDecodeHelper
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.util.Rect
import kotlinx.coroutines.test.runTest
import platform.Photos.PHAsset
import platform.Photos.PHAssetResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PhotosAssetVideoFrameDecodeHelperTest {

    @Test
    fun testConstructor() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        PhotosAssetVideoFrameDecodeHelper(request, dataSource, "video/mp4")
        PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decodeHelper = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertEquals(
            expected = ImageInfo(width = 0, height = 0, mimeType = "video/mp4"),
            actual = decodeHelper.imageInfo
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoderHelper2 = PhotosAssetVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoderHelper2.imageInfo
        }
    }

    @Test
    fun testSupportRegion() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        val decoderHelper = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFalse(decoderHelper.supportRegion)

        val decoderHelper2 = PhotosAssetVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoderHelper2.supportRegion
        }
    }

    @Test
    fun testDecode() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoderHelper = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoderHelper.decode(1)
        }

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoderHelper2 = PhotosAssetVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoderHelper2.decode(1)
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoderHelper = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(UnsupportedOperationException::class) {
            decoderHelper.decodeRegion(Rect(100, 200, 200, 100), 1)
        }

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoderHelper2 = PhotosAssetVideoFrameDecodeHelper(
            request = request.newRequest { preferVideoCover() },
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoderHelper2.decodeRegion(Rect(100, 200, 200, 100), 1)
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val element1 = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        val element11 = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoderHelper = PhotosAssetVideoFrameDecodeHelper(
            request = request,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertTrue(
            actual = decoderHelper.toString().contains("PhotosAssetVideoFrameDecodeHelper"),
            message = decoderHelper.toString()
        )
        assertTrue(
            actual = decoderHelper.toString().contains("@"),
            message = decoderHelper.toString()
        )
    }
}