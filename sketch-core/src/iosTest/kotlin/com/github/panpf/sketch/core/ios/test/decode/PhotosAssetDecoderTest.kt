package com.github.panpf.sketch.core.ios.test.decode

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.PhotosAssetDecoder
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import platform.Photos.PHAsset
import platform.Photos.PHAssetResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotosAssetDecoderTest {

    @Test
    fun testCompanion() {
        assertEquals(
            expected = 60,
            actual = PhotosAssetDecoder.SORT_WEIGHT
        )
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        PhotosAssetDecoder(requestContext, dataSource, "image/jpeg")
        PhotosAssetDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecoder(
            requestContext = requestContext,
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
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        assertFailsWith(ImageInvalidException::class) {
            decoder.decode()
        }
    }

    @Test
    fun testEqualsAndHashCode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val element1 = PhotosAssetDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        val element11 = PhotosAssetDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )

        assertNotEquals(illegal = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())
        assertNotEquals(illegal = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testToString() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )
        val decoder = PhotosAssetDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "image/jpeg"
        )
        assertTrue(
            actual = decoder.toString().contains("PhotosAssetDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        PhotosAssetDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "PhotosAssetDecoder",
            actual = PhotosAssetDecoder.Factory().key
        )
    }

    @Test
    fun testFactorySortWeight() {
        assertEquals(
            expected = 60,
            actual = PhotosAssetDecoder.Factory().sortWeight
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = PhotosAssetDecoder.Factory()

        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val request = ImageRequest(context, newPhotosAssetUri(localIdentifier))
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        assertNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = FileDataSource(
                        path = "".toPath(),
                        fileSystem = sketch.fileSystem
                    ),
                    mimeType = "image/jpeg"
                )
            )
        )

        assertNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = dataSource,
                    mimeType = null
                )
            )
        )

        assertNotNull(
            actual = factory.create(
                requestContext = requestContext,
                fetchResult = FetchResult(
                    dataSource = dataSource,
                    mimeType = "image/jpeg"
                )
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = PhotosAssetDecoder.Factory()
        val element11 = PhotosAssetDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "PhotosAssetDecoder",
            actual = PhotosAssetDecoder.Factory().toString()
        )
    }
}