/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.request

internal const val PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY =
    "sketch#preferred_thumbnail_for_photos_asset"
internal const val NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY =
    "sketch#network_access_photos_asset_allowed"
internal const val USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY =
    "sketch#use_skia_for_image_photos_asset"
internal const val PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY =
    "sketch#preferred_file_cache_for_image_photos_asset"


/**
 * Whether to prefer the thumbnail of the Photos asset.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferThumbnailForPhotosAsset
 */
fun ImageRequest.Builder.preferThumbnailForPhotosAsset(preferred: Boolean? = true): ImageRequest.Builder =
    apply {
        if (preferred != null) {
            setExtra(key = PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY, value = preferred)
        } else {
            removeExtra(PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to prefer the thumbnail of the Photos asset.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferThumbnailForPhotosAsset
 */
fun ImageOptions.Builder.preferThumbnailForPhotosAsset(preferred: Boolean? = true): ImageOptions.Builder =
    apply {
        if (preferred != null) {
            setExtra(key = PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY, value = preferred)
        } else {
            removeExtra(PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to prefer the thumbnail of the Photos asset.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferThumbnailForPhotosAsset
 */
val ImageRequest.isPreferredThumbnailForPhotosAsset: Boolean
    get() = extras?.value<Boolean>(PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY) == true

/**
 * Whether to prefer the thumbnail of the Photos asset.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferThumbnailForPhotosAsset
 */
val ImageOptions.isPreferredThumbnailForPhotosAsset: Boolean
    get() = extras?.value<Boolean>(PREFERRED_THUMBNAIL_FOR_PHOTOS_ASSET_KEY) == true


/**
 * Whether to allow access to images on the network when loading photo resources, such as downloading photos from iCloud.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testAllowNetworkAccessPhotosAsset
 */
fun ImageRequest.Builder.allowNetworkAccessPhotosAsset(allowed: Boolean? = true): ImageRequest.Builder =
    apply {
        if (allowed != null) {
            setExtra(key = NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY, value = allowed)
        } else {
            removeExtra(NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY)
        }
    }

/**
 * Whether to allow access to images on the network when loading photo resources, such as downloading photos from iCloud.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testAllowNetworkAccessPhotosAsset
 */
fun ImageOptions.Builder.allowNetworkAccessPhotosAsset(allowed: Boolean? = true): ImageOptions.Builder =
    apply {
        if (allowed != null) {
            setExtra(key = NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY, value = allowed)
        } else {
            removeExtra(NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY)
        }
    }

/**
 * Whether to allow access to images on the network when loading photo resources, such as downloading photos from iCloud.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testAllowNetworkAccessPhotosAsset
 */
val ImageRequest.isNetworkAccessPhotosAssetAllowed: Boolean
    get() = extras?.value<Boolean>(NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY) == true

/**
 * Whether to allow access to images on the network when loading photo resources, such as downloading photos from iCloud.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testAllowNetworkAccessPhotosAsset
 */
val ImageOptions.isNetworkAccessPhotosAssetAllowed: Boolean
    get() = extras?.value<Boolean>(NETWORK_ACCESS_PHOTOS_ASSET_ALLOWED_KEY) == true


/**
 * Whether to use Skia to decode the image of the Photos asset.
 * By default, PhotosAssetDecoder is used for decoding. If set to true, Skia is used for decoding and supports GIF.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testUseSkiaForImagePhotosAsset
 */
fun ImageRequest.Builder.useSkiaForImagePhotosAsset(useSkia: Boolean? = true): ImageRequest.Builder =
    apply {
        if (useSkia != null) {
            setExtra(key = USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY, value = useSkia)
        } else {
            removeExtra(USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to use Skia to decode the image of the Photos asset.
 * By default, PhotosAssetDecoder is used for decoding. If set to true, Skia is used for decoding and supports GIF.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testUseSkiaForImagePhotosAsset
 */
fun ImageOptions.Builder.useSkiaForImagePhotosAsset(useSkia: Boolean? = true): ImageOptions.Builder =
    apply {
        if (useSkia != null) {
            setExtra(key = USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY, value = useSkia)
        } else {
            removeExtra(USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to use Skia to decode the image of the Photos asset.
 * By default, PhotosAssetDecoder is used for decoding. If set to true, Skia is used for decoding and supports GIF.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testUseSkiaForImagePhotosAsset
 */
val ImageRequest.isUseSkiaForImagePhotosAsset: Boolean
    get() = extras?.value<Boolean>(USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY) == true

/**
 * Whether to use Skia to decode the image of the Photos asset.
 * By default, PhotosAssetDecoder is used for decoding. If set to true, Skia is used for decoding and supports GIF.
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testUseSkiaForImagePhotosAsset
 */
val ImageOptions.isUseSkiaForImagePhotosAsset: Boolean
    get() = extras?.value<Boolean>(USE_SKIA_FOR_IMAGE_PHOTOS_ASSET_KEY) == true


/**
 * Whether to cache the image data of Photos resources into files first
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferredFileCacheForImagePhotosAsset
 */
fun ImageRequest.Builder.preferredFileCacheForImagePhotosAsset(preferredFileCache: Boolean? = true): ImageRequest.Builder =
    apply {
        if (preferredFileCache != null) {
            setExtra(
                key = PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY,
                value = preferredFileCache
            )
        } else {
            removeExtra(PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to cache the image data of Photos resources into files first
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferredFileCacheForImagePhotosAsset
 */
fun ImageOptions.Builder.preferredFileCacheForImagePhotosAsset(preferredFileCache: Boolean? = true): ImageOptions.Builder =
    apply {
        if (preferredFileCache != null) {
            setExtra(
                key = PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY,
                value = preferredFileCache
            )
        } else {
            removeExtra(PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY)
        }
    }

/**
 * Whether to cache the image data of Photos resources into files first
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferredFileCacheForImagePhotosAsset
 */
val ImageRequest.isPreferredFileCacheForImagePhotosAsset: Boolean
    get() = extras?.value<Boolean>(PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY) == true

/**
 * Whether to cache the image data of Photos resources into files first
 *
 * @see com.github.panpf.sketch.core.ios.test.request.PhotosAssetExtensionsTest.testPreferredFileCacheForImagePhotosAsset
 */
val ImageOptions.isPreferredFileCacheForImagePhotosAsset: Boolean
    get() = extras?.value<Boolean>(PREFERRED_FILE_CACHE_FOR_IMAGE_PHOTOS_ASSET_KEY) == true
