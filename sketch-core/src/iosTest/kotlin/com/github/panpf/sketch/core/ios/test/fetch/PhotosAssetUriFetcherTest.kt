package com.github.panpf.sketch.core.ios.test.fetch

import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.fetch.PhotosAssetUriFetcher
import com.github.panpf.sketch.fetch.isPhotosAssetUri
import com.github.panpf.sketch.fetch.newPhotosAssetUri
import com.github.panpf.sketch.fetch.parseLocalIdentifier
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.preferFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.request.useSkiaForImagePhotosAsset
import com.github.panpf.sketch.test.singleton.getSketch
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
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
        val sketch = getSketch()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        PhotosAssetUriFetcher(
            localIdentifier,
            false,
            true,
            true,
            false,
            sketch.downloadCache,
            CachePolicy.WRITE_ONLY,
        )
        PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = sketch.downloadCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY,
        )
    }

    @Test
    fun testCompanion() {
        assertEquals("file", PhotosAssetUriFetcher.SCHEME)
        assertEquals("photos_asset", PhotosAssetUriFetcher.PATH_ROOT)
        assertEquals(expected = 30, actual = PhotosAssetUriFetcher.SORT_WEIGHT)
    }

    @Test
    fun testFetch() = runTest {
        val sketch = getSketch()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val result = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = sketch.downloadCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY,
        ).fetch()
        assertFalse(result.isSuccess)
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val localIdentifier2 = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/002"
        val diskCache = DiskCache.Builder(context, FileSystem.SYSTEM, "test_cache").build()
        val diskCache2 = DiskCache.Builder(context, FileSystem.SYSTEM, "test_cache2").build()
        val factory1 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory11 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory2 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier2,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory3 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = true,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory4 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = false,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory5 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = false,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory6 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = true,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory7 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache2,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        val factory8 = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = diskCache,
            downloadCachePolicy = CachePolicy.READ_ONLY
        )

        assertEquals(expected = factory1, actual = factory11)
        assertNotEquals(illegal = factory1, actual = factory2)
        assertNotEquals(illegal = factory1, actual = factory3)
        assertNotEquals(illegal = factory1, actual = factory4)
        assertNotEquals(illegal = factory1, actual = factory5)
        assertNotEquals(illegal = factory1, actual = factory6)
        assertNotEquals(illegal = factory1, actual = factory7)
        assertNotEquals(illegal = factory1, actual = factory8)
        assertNotEquals(illegal = factory2, actual = factory3)
        assertNotEquals(illegal = factory2, actual = factory4)
        assertNotEquals(illegal = factory2, actual = factory5)
        assertNotEquals(illegal = factory2, actual = factory6)
        assertNotEquals(illegal = factory2, actual = factory7)
        assertNotEquals(illegal = factory2, actual = factory8)
        assertNotEquals(illegal = factory3, actual = factory4)
        assertNotEquals(illegal = factory3, actual = factory5)
        assertNotEquals(illegal = factory3, actual = factory6)
        assertNotEquals(illegal = factory3, actual = factory7)
        assertNotEquals(illegal = factory3, actual = factory8)
        assertNotEquals(illegal = factory4, actual = factory5)
        assertNotEquals(illegal = factory4, actual = factory6)
        assertNotEquals(illegal = factory4, actual = factory7)
        assertNotEquals(illegal = factory4, actual = factory8)
        assertNotEquals(illegal = factory5, actual = factory6)
        assertNotEquals(illegal = factory5, actual = factory7)
        assertNotEquals(illegal = factory5, actual = factory8)
        assertNotEquals(illegal = factory6, actual = factory7)
        assertNotEquals(illegal = factory6, actual = factory8)
        assertNotEquals(illegal = factory7, actual = factory8)
        assertNotEquals(illegal = factory1, actual = null as Any?)
        assertNotEquals(illegal = factory1, actual = Any())

        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory2.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory3.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory4.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory5.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory6.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory1.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory3.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory4.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory5.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory6.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory2.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory4.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory5.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory6.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory3.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory4.hashCode(), actual = factory5.hashCode())
        assertNotEquals(illegal = factory4.hashCode(), actual = factory6.hashCode())
        assertNotEquals(illegal = factory4.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory4.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory5.hashCode(), actual = factory6.hashCode())
        assertNotEquals(illegal = factory5.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory5.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory6.hashCode(), actual = factory7.hashCode())
        assertNotEquals(illegal = factory6.hashCode(), actual = factory8.hashCode())
        assertNotEquals(illegal = factory7.hashCode(), actual = factory8.hashCode())
    }

    @Test
    fun testToString() {
        val sketch = getSketch()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val fetcher = PhotosAssetUriFetcher(
            localIdentifier = localIdentifier,
            preferredThumbnail = false,
            allowNetworkAccess = true,
            useSkiaForImagePhotosAsset = true,
            preferFileCacheForImagePhotosAsset = false,
            downloadCache = sketch.downloadCache,
            downloadCachePolicy = CachePolicy.WRITE_ONLY
        )
        assertEquals(
            expected = "PhotosAssetUriFetcher(" +
                    "localIdentifier='$localIdentifier', " +
                    "preferredThumbnail=false, " +
                    "allowNetworkAccess=true, " +
                    "useSkiaForImagePhotosAsset=true, " +
                    "preferFileCacheForImagePhotosAsset=false, " +
                    "downloadCache=${sketch.downloadCache}, " +
                    "downloadCachePolicy=WRITE_ONLY)",
            actual = fetcher.toString()
        )
    }

    @Test
    fun testFactoryConstructor() {
        PhotosAssetUriFetcher.Factory()
    }

    @Test
    fun testFactoryCreate() {
        val factory = PhotosAssetUriFetcher.Factory()
        val (context, sketch) = getTestContextAndSketch()
        val localIdentifier = "DB16113B-984A-4D12-B4D0-50FC46066781/L0/001"
        val imageUri = newPhotosAssetUri(localIdentifier)

        ImageRequest(context, "http://sample.com/sample.jpg")
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
                assertFalse(this.useSkiaForImagePhotosAsset)
                assertFalse(this.preferFileCacheForImagePhotosAsset)
                assertSame(sketch.downloadCache, this.downloadCache)
                assertEquals(CachePolicy.ENABLED, this.downloadCachePolicy)
            }

        ImageRequest(context, imageUri) {
            preferThumbnailForPhotosAsset(true)
            allowNetworkAccessPhotosAsset(true)
            useSkiaForImagePhotosAsset()
            preferFileCacheForImagePhotosAsset()
            downloadCachePolicy(CachePolicy.READ_ONLY)
        }.toRequestContext(sketch, Size.Empty)
            .let { factory.create(it) }
            .apply {
                assertNotNull(this)
                assertEquals(localIdentifier, this.localIdentifier)
                assertTrue(this.preferredThumbnail)
                assertTrue(this.allowNetworkAccess)
                assertTrue(this.useSkiaForImagePhotosAsset)
                assertTrue(this.preferFileCacheForImagePhotosAsset)
                assertSame(sketch.downloadCache, this.downloadCache)
                assertEquals(CachePolicy.READ_ONLY, this.downloadCachePolicy)
            }
    }

    @Test
    fun testFactoryEqualsAndHashCode() {
        val factory1 = PhotosAssetUriFetcher.Factory()
        val factory11 = PhotosAssetUriFetcher.Factory()
        assertEquals(expected = factory1, actual = factory11)
        assertEquals(expected = factory1.hashCode(), actual = factory11.hashCode())
    }

    @Test
    fun testFactoryToString() {
        assertEquals(
            expected = "PhotosAssetUriFetcher",
            actual = PhotosAssetUriFetcher.Factory().toString()
        )
    }
}