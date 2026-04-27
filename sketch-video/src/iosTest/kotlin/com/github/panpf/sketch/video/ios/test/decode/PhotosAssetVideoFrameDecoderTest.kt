package com.github.panpf.sketch.video.ios.test.decode

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.decode.DecodeException
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.decode.PhotosAssetVideoFrameDecoder
import com.github.panpf.sketch.decode.supportPhotosAssetVideoFrame
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.preferVideoCover
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

class PhotosAssetVideoFrameDecoderTest {

    @Test
    fun testSupportPhotosAssetVideoFrame() {
        ComponentRegistry().apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportPhotosAssetVideoFrame()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[PhotosAssetVideoFrameDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }

        ComponentRegistry {
            supportPhotosAssetVideoFrame()
            supportPhotosAssetVideoFrame()
        }.apply {
            assertEquals(
                expected = "ComponentRegistry(" +
                        "fetchers=[]," +
                        "decoders=[PhotosAssetVideoFrameDecoder]," +
                        "interceptors=[]," +
                        "disabledFetchers=[]," +
                        "disabledDecoders=[]," +
                        "disabledInterceptors=[]" +
                        ")",
                actual = toString()
            )
        }
    }

    @Test
    fun testCompanion() {
        assertEquals(
            expected = 30,
            actual = PhotosAssetVideoFrameDecoder.SORT_WEIGHT
        )
    }

    @Test
    fun testConstructor() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val request = ImageRequest(context, ComposeResImageFiles.svg.uri)
        val requestContext = request.toRequestContext(sketch)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001",
            preferredThumbnail = true,
            allowNetworkAccess = false,
            asset = PHAsset(),
            resource = PHAssetResource()
        )

        PhotosAssetVideoFrameDecoder(requestContext, dataSource, "video/mp4")
        PhotosAssetVideoFrameDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
    }

    @Test
    fun testImageInfo() = runTest {
        val (context, sketch) = getTestContextAndSketch()

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
        val decoder = PhotosAssetVideoFrameDecoder(
            requestContext = request.toRequestContext(sketch),
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertEquals(
            expected = ImageInfo(width = 0, height = 0, mimeType = "video/mp4"),
            actual = decoder.getImageInfo()
        )

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val decoder2 = PhotosAssetVideoFrameDecoder(
            requestContext = request.newRequest { preferVideoCover() }.toRequestContext(sketch),
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertFailsWith(DecodeException::class) {
            decoder2.getImageInfo()
        }
    }

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

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
        assertFailsWith(ImageInvalidException::class) {
            PhotosAssetVideoFrameDecoder(
                requestContext = request.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).decode()
        }

        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        assertFailsWith(DecodeException::class) {
            PhotosAssetVideoFrameDecoder(
                requestContext = request.newRequest { preferVideoCover() }.toRequestContext(sketch),
                dataSource = dataSource,
                mimeType = "video/mp4"
            ).decode()
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
        val element1 = PhotosAssetVideoFrameDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        val element11 = PhotosAssetVideoFrameDecoder(
            requestContext = requestContext,
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
        val decoder = PhotosAssetVideoFrameDecoder(
            requestContext = requestContext,
            dataSource = dataSource,
            mimeType = "video/mp4"
        )
        assertTrue(
            actual = decoder.toString().contains("PhotosAssetVideoFrameDecoder"),
            message = decoder.toString()
        )
        assertTrue(actual = decoder.toString().contains("@"), message = decoder.toString())
    }

    @Test
    fun testFactoryConstructor() {
        PhotosAssetVideoFrameDecoder.Factory()
    }

    @Test
    fun testFactoryKey() {
        assertEquals(
            expected = "PhotosAssetVideoFrameDecoder",
            actual = PhotosAssetVideoFrameDecoder.Factory().key
        )
    }

    @Test
    fun testFactorySortWeight() {
        assertEquals(
            expected = 30,
            actual = PhotosAssetVideoFrameDecoder.Factory().sortWeight
        )
    }

    @Test
    fun testFactoryCreate() = runTest {
        val (context, sketch) = getTestContextAndSketch()
        val factory = PhotosAssetVideoFrameDecoder.Factory()

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
                        path = "/sdcard/sample_rotation.mp4".toPath(),
                        fileSystem = sketch.fileSystem
                    ),
                    mimeType = "video/mp4"
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
                    mimeType = "video/mp4"
                )
            )
        )
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val element1 = PhotosAssetVideoFrameDecoder.Factory()
        val element11 = PhotosAssetVideoFrameDecoder.Factory()

        assertEquals(expected = element1, actual = element11)
        assertNotEquals(illegal = element1, actual = null as Any?)
        assertNotEquals(illegal = element1, actual = Any())

        assertEquals(expected = element1.hashCode(), actual = element11.hashCode())
    }

    @Test
    fun testFactoryToString() = runTest {
        assertEquals(
            expected = "PhotosAssetVideoFrameDecoder",
            actual = PhotosAssetVideoFrameDecoder.Factory().toString()
        )
    }
}