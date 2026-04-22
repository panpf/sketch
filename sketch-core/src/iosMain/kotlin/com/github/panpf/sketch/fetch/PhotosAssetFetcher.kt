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

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.fetchPhotosAsset
import com.github.panpf.sketch.util.resolveMimeType
import com.github.panpf.sketch.util.selectPrimaryResource
import okio.IOException

/**
 * Sample: 'file:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001'
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest.testNewPhotosAssetUri
 */
fun newPhotosAssetUri(localIdentifier: String): String =
    "${PhotosAssetFetcher.SCHEME}:///${PhotosAssetFetcher.PATH_ROOT}/$localIdentifier"

/**
 * Check if the uri is a photos asset uri
 *
 * Support 'file:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001' uri
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest.testIsPhotosAssetUri
 */
fun isPhotosAssetUri(uri: Uri): Boolean =
    PhotosAssetFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && PhotosAssetFetcher.PATH_ROOT
        .equals(uri.pathSegments.firstOrNull(), ignoreCase = true)

/**
 * Parse the local identifier from the photos asset uri
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest.testParseLocalIdentifier
 */
fun parseLocalIdentifier(uri: Uri): String? =
    if (isPhotosAssetUri(uri)) {
        uri.pathSegments.drop(1).joinToString("/")
    } else {
        null
    }

/**
 * PhotosAssetFetcher is a Fetcher that fetches photo assets from the iOS Photos library using their local identifiers. It supports fetching both the original and thumbnail versions of the assets, and can optionally use Skia for image processing.
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest
 */
class PhotosAssetFetcher private constructor(
    val localIdentifier: String,
    val preferredThumbnail: Boolean,
    val allowNetworkAccess: Boolean,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val PATH_ROOT = "photos_asset"
    }

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> = runCatching {
        val asset = fetchPhotosAsset(localIdentifier)
            ?: throw IOException("Not found PHAsset: '$localIdentifier'")
        val resource = selectPrimaryResource(asset, preferredThumbnail)
            ?: throw IOException("Not found PHAssetResource: '$localIdentifier'")
        val mimeType = resolveMimeType(resource) ?: resolveMimeType(asset)
        val dataSource = PhotosAssetDataSource(
            localIdentifier = localIdentifier,
            preferredThumbnail = preferredThumbnail,
            allowNetworkAccess = allowNetworkAccess,
            asset = asset,
            resource = resource,
        )
        FetchResult(dataSource, mimeType)
    }

    class Factory : Fetcher.Factory {

        override fun create(requestContext: RequestContext): PhotosAssetFetcher? {
            val request = requestContext.request
            val uri = request.uri
            val localIdentifier = parseLocalIdentifier(uri) ?: return null
            val preferredThumbnail = request.preferThumbnailForPhotosAsset ?: false
            val allowNetworkAccess = request.allowNetworkAccessPhotosAsset ?: false
            return PhotosAssetFetcher(
                localIdentifier = localIdentifier,
                preferredThumbnail = preferredThumbnail,
                allowNetworkAccess = allowNetworkAccess,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "PhotosAssetFetcher"
    }
}