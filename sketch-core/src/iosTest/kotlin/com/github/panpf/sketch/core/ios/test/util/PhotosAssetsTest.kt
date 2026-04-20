package com.github.panpf.sketch.core.ios.test.util

import com.github.panpf.sketch.images.Base64Images
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.bytes2nsData
import com.github.panpf.sketch.test.utils.readBytes
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.fetchPhotosAsset
import com.github.panpf.sketch.util.pixelSize
import com.github.panpf.sketch.util.preferredImageResourceTypeOrder
import com.github.panpf.sketch.util.preferredVideoResourceTypeOrder
import com.github.panpf.sketch.util.resolveMimeType
import com.github.panpf.sketch.util.resolveMimeTypeWithPHAssetResourceType
import com.github.panpf.sketch.util.selectPrimaryResource
import com.github.panpf.sketch.util.toBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.test.runTest
import platform.Photos.PHAsset
import platform.Photos.PHAssetMediaTypeAudio
import platform.Photos.PHAssetMediaTypeImage
import platform.Photos.PHAssetMediaTypeUnknown
import platform.Photos.PHAssetMediaTypeVideo
import platform.Photos.PHAssetResource
import platform.UIKit.UIImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PhotosAssetsTest {

    @Test
    fun testPreferredVideoResourceTypeOrder() {
        assertEquals(
            expected = listOf(6L, 2L, 10L, 9L),
            actual = preferredVideoResourceTypeOrder()
        )
    }

    @Test
    fun testPreferredImageResourceTypeOrder() {
        assertEquals(
            expected = listOf(1L, 4L, 5L, 8L),
            actual = preferredImageResourceTypeOrder(true)
        )
        assertEquals(
            expected = listOf(5L, 1L, 4L, 8L),
            actual = preferredImageResourceTypeOrder(false)
        )
    }

    @Test
    fun fetchPhotosAsset() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        assertNull(fetchPhotosAsset(localIdentifier))
    }

    @Test
    fun selectPrimaryResource() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val asset = PHAsset()
        assertNull(actual = selectPrimaryResource(asset = asset, preferredThumbnail = true))
    }

    @Test
    fun testResolveMimeTypeWithPHAssetResource() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val resource = PHAssetResource()
        assertNull(resolveMimeType(resource))
    }

    @Test
    fun testResolveMimeTypeWithUniformTypeIdentifierAndOriginalFilename() {
        // UTType
        assertEquals(
            expected = "image/heic",
            actual = resolveMimeType(uniformTypeIdentifier = "public.heic", originalFilename = null)
        )

        // uniformTypeIdentifier
        assertEquals(
            expected = "image/jpeg",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.jpeg", originalFilename = null)
        )
        assertEquals(
            expected = "image/jpeg",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.jpg", originalFilename = null)
        )
        assertEquals(
            expected = "image/png",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.png", originalFilename = null)
        )
        assertEquals(
            expected = "image/webp",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.webp", originalFilename = null)
        )
        assertEquals(
            expected = "image/gif",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.gif", originalFilename = null)
        )
        assertEquals(
            expected = "image/heic",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.heic", originalFilename = null)
        )
        assertEquals(
            expected = "image/heif",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.heif", originalFilename = null)
        )
        assertEquals(
            expected = "image/avif",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.avif", originalFilename = null)
        )
        assertEquals(
            expected = "image/x-adobe-dng",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.raw", originalFilename = null)
        )
        assertEquals(
            expected = "image/x-adobe-dng",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.dng", originalFilename = null)
        )
        assertEquals(
            expected = "image/tiff",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.tiff", originalFilename = null)
        )
        assertEquals(
            expected = "image/bmp",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.bmp", originalFilename = null)
        )
        assertEquals(
            expected = "image/ico",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.ico", originalFilename = null)
        )
        assertEquals(
            expected = "video/quicktime",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.mov", originalFilename = null)
        )
        assertEquals(
            expected = "video/quicktime",
            actual = resolveMimeType(
                uniformTypeIdentifier = "fake.quicktime",
                originalFilename = null
            )
        )
        assertEquals(
            expected = "video/mp4",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.mpeg-4", originalFilename = null)
        )
        assertEquals(
            expected = "video/mp4",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.mp4", originalFilename = null)
        )
        assertEquals(
            expected = "video/mkv",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.mkv", originalFilename = null)
        )
        assertEquals(
            expected = "video/avi",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.avi", originalFilename = null)
        )
        assertEquals(
            expected = "video/rmvb",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.rmvb", originalFilename = null)
        )
        assertEquals(
            expected = "video/rmvb",
            actual = resolveMimeType(uniformTypeIdentifier = "fake.rm", originalFilename = null)
        )
        assertEquals(
            expected = null,
            actual = resolveMimeType(uniformTypeIdentifier = "fake.fake", originalFilename = null)
        )

        // originalFilename
        assertEquals(
            expected = "image/jpeg",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.jpeg")
        )
        assertEquals(
            expected = "image/jpeg",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.jpg")
        )
        assertEquals(
            expected = "image/png",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.png")
        )
        assertEquals(
            expected = "image/webp",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.webp")
        )
        assertEquals(
            expected = "image/gif",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.gif")
        )
        assertEquals(
            expected = "image/heic",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.heic")
        )
        assertEquals(
            expected = "image/heif",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.heif")
        )
        assertEquals(
            expected = "image/avif",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.avif")
        )
        assertEquals(
            expected = "image/x-adobe-dng",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.dng")
        )
        assertEquals(
            expected = "image/tiff",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.tiff")
        )
        assertEquals(
            expected = "image/tiff",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.tif")
        )
        assertEquals(
            expected = "video/quicktime",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.mov")
        )
        assertEquals(
            expected = "video/mp4",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.mp4")
        )
        assertEquals(
            expected = "video/mp4",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.m4v")
        )
        assertEquals(
            expected = "video/mkv",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.mkv")
        )
        assertEquals(
            expected = "video/avi",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.avi")
        )
        assertEquals(
            expected = "video/rmvb",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.rmvb")
        )
        assertEquals(
            expected = "video/rmvb",
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.rm")
        )
        assertEquals(
            expected = null,
            actual = resolveMimeType(uniformTypeIdentifier = null, originalFilename = "test.fake")
        )
    }

    @Test
    fun testResolveMimeTypeWithPHAssetResourceType() {
        assertEquals(
            expected = "image/*",
            actual = resolveMimeTypeWithPHAssetResourceType(1L)
        )
        assertEquals(
            expected = "image/*",
            actual = resolveMimeTypeWithPHAssetResourceType(4L)
        )
        assertEquals(
            expected = "image/*",
            actual = resolveMimeTypeWithPHAssetResourceType(5L)
        )
        assertEquals(
            expected = "image/*",
            actual = resolveMimeTypeWithPHAssetResourceType(8L)
        )
        assertEquals(
            expected = "video/*",
            actual = resolveMimeTypeWithPHAssetResourceType(2L)
        )
        assertEquals(
            expected = "video/*",
            actual = resolveMimeTypeWithPHAssetResourceType(6L)
        )
        assertEquals(
            expected = "video/*",
            actual = resolveMimeTypeWithPHAssetResourceType(9L)
        )
        assertEquals(
            expected = "video/*",
            actual = resolveMimeTypeWithPHAssetResourceType(10L)
        )
        assertEquals(
            expected = null,
            actual = resolveMimeTypeWithPHAssetResourceType(0L)
        )
        assertEquals(
            expected = null,
            actual = resolveMimeTypeWithPHAssetResourceType(11L)
        )
        assertEquals(
            expected = null,
            actual = resolveMimeTypeWithPHAssetResourceType(3L)
        )
    }

    @Test
    fun testResolveMimeTypeWithPHAsset() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val asset = PHAsset()
        assertEquals(
            expected = null,
            actual = resolveMimeType(asset)
        )
    }

    @Test
    fun testResolveMimeTypeWithPHAssetMediaType() {
        assertEquals(
            expected = "image/*",
            actual = resolveMimeType(PHAssetMediaTypeImage)
        )
        assertEquals(
            expected = "video/*",
            actual = resolveMimeType(PHAssetMediaTypeVideo)
        )
        assertEquals(
            expected = "audio/*",
            actual = resolveMimeType(PHAssetMediaTypeAudio)
        )
        assertEquals(
            expected = null,
            actual = resolveMimeType(PHAssetMediaTypeUnknown)
        )
    }

    @Test
    fun testPixelSize() {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val asset = PHAsset()
        assertEquals(expected = Size(0, 0), actual = asset.pixelSize())
    }

    @Test
    @OptIn(ExperimentalForeignApi::class)
    fun testUIImageToBitmap() = runTest {
        // [Test not completed] Because the test environment cannot access the photo library, the test cannot be completed.
        val (_, sketch) = getTestContextAndSketch()
        val bytes = readBytes(sketch, Base64Images.KOTLIN_ICON)
        val nsData = bytes2nsData(bytes)
        val uiImage = UIImage(nsData)
        val newBitmap = uiImage.toBitmap()
        assertEquals(expected = 96, actual = newBitmap.imageInfo.width)
        assertEquals(expected = 48, actual = newBitmap.imageInfo.height)
    }
}