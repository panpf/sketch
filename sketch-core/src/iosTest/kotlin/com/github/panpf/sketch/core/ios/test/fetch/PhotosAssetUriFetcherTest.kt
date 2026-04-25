package com.github.panpf.sketch.core.ios.test.fetch

import com.github.panpf.sketch.fetch.PhotosAssetFetcher
import com.github.panpf.sketch.fetch.isPhotosAssetUri
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.fetch.parseLocalIdentifier
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotosAssetUriFetcherTest {

    @Test
    fun testNewPhotosAssetUri() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        assertEquals(
            expected = "file:///photos_asset/$localIdentifier",
            actual = newPhotosAssetUri(localIdentifier)
        )
    }

    @Test
    fun testIsPhotosAssetUri() {
        assertTrue(actual = isPhotosAssetUri("file:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
        assertTrue(actual = isPhotosAssetUri("FILE:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
        assertTrue(actual = isPhotosAssetUri("file:///PHOTOS_ASSET/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
        assertFalse(actual = isPhotosAssetUri("fil:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
        assertFalse(actual = isPhotosAssetUri("file://authority/photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
        assertFalse(actual = isPhotosAssetUri("file:///photos_asset1/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001".toUri()))
    }

    @Test
    fun testParseLocalIdentifier() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        assertEquals(
            expected = localIdentifier,
            actual = parseLocalIdentifier("file:///photos_asset/$localIdentifier".toUri())
        )

        assertEquals(
            expected = null,
            actual = parseLocalIdentifier("fil:///photos_asset/$localIdentifier".toUri())
        )
    }

    @Test
    fun testConstructor() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        PhotosAssetFetcher(localIdentifier, false, true)
        PhotosAssetFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true
        )
    }

    @Test
    fun testCompanion() {
        assertEquals("file", PhotosAssetFetcher.SCHEME)
        assertEquals("photos_asset", PhotosAssetFetcher.PATH_ROOT)
        assertEquals(expected = 30, actual = PhotosAssetFetcher.SORT_WEIGHT)
    }

    @Test
    fun testFetch() = runTest {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val result = PhotosAssetFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true
        ).fetch()
        assertFalse(result.isSuccess)
    }

    @Test
    fun testEqualsAndHashCode() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val localIdentifier2 = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/002"
        val factory1 = PhotosAssetFetcher(localIdentifier, false, true)
        val factory11 = PhotosAssetFetcher(localIdentifier, false, true)
        val factory2 = PhotosAssetFetcher(localIdentifier2, false, true)
        val factory3 = PhotosAssetFetcher(localIdentifier, true, true)
        val factory4 = PhotosAssetFetcher(localIdentifier, false, false)

        assertEquals(expected = factory1, actual = factory11)
        assertNotEquals(illegal = factory1, actual = factory2)
        assertNotEquals(illegal = factory2, actual = factory3)
        assertNotEquals(illegal = factory2, actual = factory4)
        assertNotEquals(illegal = factory3, actual = factory4)
        assertNotEquals(illegal = factory1, actual = null as Any?)
        assertNotEquals(illegal = factory1, actual = Any())

        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory2.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory3.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory4.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory4.hashCode())
    }

    @Test
    fun testToString() {
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val fetcher = PhotosAssetFetcher(localIdentifier, false, true)
        assertEquals(
            expected = "PhotosAssetFetcher(localIdentifier='$localIdentifier', preferredThumbnail=false, allowNetworkAccess=true)",
            actual = fetcher.toString()
        )
    }

    @Test
    fun testFactoryConstructor() {
        PhotosAssetFetcher.Factory()
    }

    @Test
    fun testFactoryCreate() {
        val factory = PhotosAssetFetcher.Factory()
        val (context, sketch) = getTestContextAndSketch()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val imageUri = newPhotosAssetUri(localIdentifier)

        ImageRequest(context, "htt://sample.com/sample.jpg")
            .toRequestContext(sketch, Size.Empty)
            .let { factory.create(it) }
            .apply {
                assertNull(this)
            }

        ImageRequest(context, imageUri)
            .toRequestContext(sketch, Size.Empty)
            .let { factory.create(it) }
            .apply {
                assertNotNull(this)
                assertEquals(localIdentifier, this.localIdentifier)
                assertFalse(this.preferredThumbnail)
                assertFalse(this.allowNetworkAccess)
            }

        ImageRequest(context, imageUri) {
            preferThumbnailForPhotosAsset(true)
            allowNetworkAccessPhotosAsset(true)
        }.toRequestContext(sketch, Size.Empty)
            .let { factory.create(it) }
            .apply {
                assertNotNull(this)
                assertEquals(localIdentifier, this.localIdentifier)
                assertTrue(this.preferredThumbnail)
                assertTrue(this.allowNetworkAccess)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val factory1 = PhotosAssetFetcher.Factory()
        val factory11 = PhotosAssetFetcher.Factory()
        assertEquals(expected = factory1, actual = factory11)
        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "PhotosAssetFetcher",
            actual = PhotosAssetFetcher.Factory().toString()
        )
    }
}