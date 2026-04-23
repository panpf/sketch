package com.github.panpf.sketch.core.ios.test.decode.internal

import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.PhotosAssetDecodeHelper
import com.github.panpf.sketch.source.PhotosAssetDataSource
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

class PhotosAssetDecodeHelperTest {

    @Test
    fun testConstructor() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        PhotosAssetDecodeHelper(dataSource, "image/jpeg")
        PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        assertEquals(
            expected = ImageInfo(width = 0, height = 0, mimeType = "image/jpeg"),
            actual = decoder.getImageInfo()
        )
    }

    @Test
    fun testSupportRegion() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        assertFalse(decoder.isSupportRegion())
    }

    @Test
    fun testDecode() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            decoder.decode(1)
        }
    }

    @Test
    fun testDecodeRegion() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        assertFailsWith(UnsupportedOperationException::class) {
            decoder.decodeRegion(Rect(100, 200, 200, 100), 1)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val element1 = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        val element11 = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecodeHelper(
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        assertTrue(
            actual = decoder.toString().contains("PhotosAssetDecodeHelper"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }
}