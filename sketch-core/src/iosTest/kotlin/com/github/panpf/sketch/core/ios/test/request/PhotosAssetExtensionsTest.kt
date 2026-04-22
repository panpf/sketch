package com.github.panpf.sketch.core.ios.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.preferFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.request.useSkiaForImagePhotosAsset
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhotosAssetExtensionsTest {

    @Test
    fun testPreferThumbnailForPhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertNull(preferThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset()
        }.apply {
            assertTrue(preferThumbnailForPhotosAsset!!)
        }.newRequest {
            preferThumbnailForPhotosAsset(null)
        }.apply {
            assertNull(preferThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset(true)
        }.apply {
            assertTrue(preferThumbnailForPhotosAsset!!)
        }.newRequest {
            preferThumbnailForPhotosAsset(false)
        }.apply {
            assertFalse(preferThumbnailForPhotosAsset!!)
        }

        ImageOptions().apply {
            assertNull(preferThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset()
        }.apply {
            assertTrue(preferThumbnailForPhotosAsset!!)
        }.newOptions {
            preferThumbnailForPhotosAsset(null)
        }.apply {
            assertNull(preferThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset(true)
        }.apply {
            assertTrue(preferThumbnailForPhotosAsset!!)
        }.newOptions {
            preferThumbnailForPhotosAsset(false)
        }.apply {
            assertFalse(preferThumbnailForPhotosAsset!!)
        }
    }

    @Test
    fun testAllowNetworkAccessPhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertNull(allowNetworkAccessPhotosAsset)
        }.newRequest {
            allowNetworkAccessPhotosAsset()
        }.apply {
            assertTrue(allowNetworkAccessPhotosAsset!!)
        }.newRequest {
            allowNetworkAccessPhotosAsset(null)
        }.apply {
            assertNull(allowNetworkAccessPhotosAsset)
        }.newRequest {
            allowNetworkAccessPhotosAsset(true)
        }.apply {
            assertTrue(allowNetworkAccessPhotosAsset!!)
        }.newRequest {
            allowNetworkAccessPhotosAsset(false)
        }.apply {
            assertFalse(allowNetworkAccessPhotosAsset!!)
        }

        ImageOptions().apply {
            assertNull(allowNetworkAccessPhotosAsset)
        }.newOptions {
            allowNetworkAccessPhotosAsset()
        }.apply {
            assertTrue(allowNetworkAccessPhotosAsset!!)
        }.newOptions {
            allowNetworkAccessPhotosAsset(null)
        }.apply {
            assertNull(allowNetworkAccessPhotosAsset)
        }.newOptions {
            allowNetworkAccessPhotosAsset(true)
        }.apply {
            assertTrue(allowNetworkAccessPhotosAsset!!)
        }.newOptions {
            allowNetworkAccessPhotosAsset(false)
        }.apply {
            assertFalse(allowNetworkAccessPhotosAsset!!)
        }
    }

    @Test
    fun testUseSkiaForImagePhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertNull(useSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset()
        }.apply {
            assertTrue(useSkiaForImagePhotosAsset!!)
        }.newRequest {
            useSkiaForImagePhotosAsset(null)
        }.apply {
            assertNull(useSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset(true)
        }.apply {
            assertTrue(useSkiaForImagePhotosAsset!!)
        }.newRequest {
            useSkiaForImagePhotosAsset(false)
        }.apply {
            assertFalse(useSkiaForImagePhotosAsset!!)
        }

        ImageOptions().apply {
            assertNull(useSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset()
        }.apply {
            assertTrue(useSkiaForImagePhotosAsset!!)
        }.newOptions {
            useSkiaForImagePhotosAsset(null)
        }.apply {
            assertNull(useSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset(true)
        }.apply {
            assertTrue(useSkiaForImagePhotosAsset!!)
        }.newOptions {
            useSkiaForImagePhotosAsset(false)
        }.apply {
            assertFalse(useSkiaForImagePhotosAsset!!)
        }
    }

    @Test
    fun testPreferFileCacheForImagePhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertNull(preferFileCacheForImagePhotosAsset)
        }.newRequest {
            preferFileCacheForImagePhotosAsset()
        }.apply {
            assertTrue(preferFileCacheForImagePhotosAsset!!)
        }.newRequest {
            preferFileCacheForImagePhotosAsset(null)
        }.apply {
            assertNull(preferFileCacheForImagePhotosAsset)
        }.newRequest {
            preferFileCacheForImagePhotosAsset(true)
        }.apply {
            assertTrue(preferFileCacheForImagePhotosAsset!!)
        }.newRequest {
            preferFileCacheForImagePhotosAsset(false)
        }.apply {
            assertFalse(preferFileCacheForImagePhotosAsset!!)
        }

        ImageOptions().apply {
            assertNull(preferFileCacheForImagePhotosAsset)
        }.newOptions {
            preferFileCacheForImagePhotosAsset()
        }.apply {
            assertTrue(preferFileCacheForImagePhotosAsset!!)
        }.newOptions {
            preferFileCacheForImagePhotosAsset(null)
        }.apply {
            assertNull(preferFileCacheForImagePhotosAsset)
        }.newOptions {
            preferFileCacheForImagePhotosAsset(true)
        }.apply {
            assertTrue(preferFileCacheForImagePhotosAsset!!)
        }.newOptions {
            preferFileCacheForImagePhotosAsset(false)
        }.apply {
            assertFalse(preferFileCacheForImagePhotosAsset!!)
        }
    }
}