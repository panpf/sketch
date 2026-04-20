package com.github.panpf.sketch.core.ios.test.request

import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.isNetworkAccessPhotosAssetAllowed
import com.github.panpf.sketch.request.isPreferredFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.isPreferredThumbnailForPhotosAsset
import com.github.panpf.sketch.request.isUseSkiaForImagePhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.request.preferredFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.useSkiaForImagePhotosAsset
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PhotosAssetExtensionsTest {

    @Test
    fun testPreferThumbnailForPhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset()
        }.apply {
            assertTrue(isPreferredThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset(null)
        }.apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset(true)
        }.apply {
            assertTrue(isPreferredThumbnailForPhotosAsset)
        }.newRequest {
            preferThumbnailForPhotosAsset(false)
        }.apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }

        ImageOptions().apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset()
        }.apply {
            assertTrue(isPreferredThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset(null)
        }.apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset(true)
        }.apply {
            assertTrue(isPreferredThumbnailForPhotosAsset)
        }.newOptions {
            preferThumbnailForPhotosAsset(false)
        }.apply {
            assertFalse(isPreferredThumbnailForPhotosAsset)
        }
    }

    @Test
    fun testAllowNetworkAccessPhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }.newRequest {
            allowNetworkAccessPhotosAsset()
        }.apply {
            assertTrue(isNetworkAccessPhotosAssetAllowed)
        }.newRequest {
            allowNetworkAccessPhotosAsset(null)
        }.apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }.newRequest {
            allowNetworkAccessPhotosAsset(true)
        }.apply {
            assertTrue(isNetworkAccessPhotosAssetAllowed)
        }.newRequest {
            allowNetworkAccessPhotosAsset(false)
        }.apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }

        ImageOptions().apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }.newOptions {
            allowNetworkAccessPhotosAsset()
        }.apply {
            assertTrue(isNetworkAccessPhotosAssetAllowed)
        }.newOptions {
            allowNetworkAccessPhotosAsset(null)
        }.apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }.newOptions {
            allowNetworkAccessPhotosAsset(true)
        }.apply {
            assertTrue(isNetworkAccessPhotosAssetAllowed)
        }.newOptions {
            allowNetworkAccessPhotosAsset(false)
        }.apply {
            assertFalse(isNetworkAccessPhotosAssetAllowed)
        }
    }

    @Test
    fun testUseSkiaForImagePhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset()
        }.apply {
            assertTrue(isUseSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset(null)
        }.apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset(true)
        }.apply {
            assertTrue(isUseSkiaForImagePhotosAsset)
        }.newRequest {
            useSkiaForImagePhotosAsset(false)
        }.apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }

        ImageOptions().apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset()
        }.apply {
            assertTrue(isUseSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset(null)
        }.apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset(true)
        }.apply {
            assertTrue(isUseSkiaForImagePhotosAsset)
        }.newOptions {
            useSkiaForImagePhotosAsset(false)
        }.apply {
            assertFalse(isUseSkiaForImagePhotosAsset)
        }
    }

    @Test
    fun testPreferredFileCacheForImagePhotosAsset() {
        val context = getTestContext()

        ImageRequest(context, "http://sample.com/sample.jpg").apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }.newRequest {
            preferredFileCacheForImagePhotosAsset()
        }.apply {
            assertTrue(isPreferredFileCacheForImagePhotosAsset)
        }.newRequest {
            preferredFileCacheForImagePhotosAsset(null)
        }.apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }.newRequest {
            preferredFileCacheForImagePhotosAsset(true)
        }.apply {
            assertTrue(isPreferredFileCacheForImagePhotosAsset)
        }.newRequest {
            preferredFileCacheForImagePhotosAsset(false)
        }.apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }

        ImageOptions().apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }.newOptions {
            preferredFileCacheForImagePhotosAsset()
        }.apply {
            assertTrue(isPreferredFileCacheForImagePhotosAsset)
        }.newOptions {
            preferredFileCacheForImagePhotosAsset(null)
        }.apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }.newOptions {
            preferredFileCacheForImagePhotosAsset(true)
        }.apply {
            assertTrue(isPreferredFileCacheForImagePhotosAsset)
        }.newOptions {
            preferredFileCacheForImagePhotosAsset(false)
        }.apply {
            assertFalse(isPreferredFileCacheForImagePhotosAsset)
        }
    }
}