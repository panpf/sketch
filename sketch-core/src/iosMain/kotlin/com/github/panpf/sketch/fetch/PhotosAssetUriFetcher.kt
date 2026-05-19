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
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadAndWrite
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.allowNetworkAccessPhotosAsset
import com.github.panpf.sketch.request.preferFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.preferThumbnailForPhotosAsset
import com.github.panpf.sketch.request.useSkiaForImagePhotosAsset
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.PhotosAssetDataSource
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.fetchPhotosAsset
import com.github.panpf.sketch.util.resolveMimeType
import com.github.panpf.sketch.util.selectPrimaryResource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Buffer
import okio.IOException
import okio.Path
import okio.buffer
import okio.use
import platform.Photos.PHAssetResourceManager
import platform.Photos.PHAssetResourceRequestOptions
import platform.darwin.ByteVar
import platform.posix.memcpy
import kotlin.coroutines.resumeWithException

/**
 * Sample: 'file:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001'
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest.testNewPhotosAssetUri
 */
fun newPhotosAssetUri(localIdentifier: String): String =
    "${PhotosAssetUriFetcher.SCHEME}:///${PhotosAssetUriFetcher.PATH_ROOT}/$localIdentifier"

/**
 * Check if the uri is a photos asset uri
 *
 * Support 'file:///photos_asset/DB16113B-984A-4D12-B4D0-50FC46066781/L0/001' uri
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest.testIsPhotosAssetUri
 */
fun isPhotosAssetUri(uri: Uri): Boolean =
    PhotosAssetUriFetcher.SCHEME.equals(uri.scheme, ignoreCase = true)
            && uri.authority?.takeIf { it.isNotEmpty() } == null
            && PhotosAssetUriFetcher.PATH_ROOT
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
 * PhotosAssetUriFetcher is a Fetcher that fetches photo assets from the iOS Photos library using their local identifiers. It supports fetching both the original and thumbnail versions of the assets, and can optionally use Skia for image processing.
 *
 * @see com.github.panpf.sketch.core.ios.test.fetch.PhotosAssetUriFetcherTest
 */
class PhotosAssetUriFetcher(
    val localIdentifier: String,
    val preferredThumbnail: Boolean,
    val allowNetworkAccess: Boolean,
    val useSkiaForImagePhotosAsset: Boolean,
    val preferFileCacheForImagePhotosAsset: Boolean,
    val downloadCache: DiskCache,
    val downloadCachePolicy: CachePolicy,
) : Fetcher {

    companion object {
        const val SCHEME = "file"
        const val PATH_ROOT = "photos_asset"
        const val SORT_WEIGHT = 30
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
        // Important. The processing of useSkiaForImagePhotosAsset must be done in PhotosAssetUriFetcher,
        // because other libraries may use Components.newFetcherOrThrow directly to obtain the DataSource
        val convertedDataSource = if (shouldUseSkia(mimeType, useSkiaForImagePhotosAsset)) {
            convertDataSource(dataSource)
        } else {
            dataSource
        }
        FetchResult(convertedDataSource, mimeType)
    }

    private fun shouldUseSkia(mimeType: String?, useSkiaForImagePhotosAsset: Boolean): Boolean {
        if (mimeType == null) return false
        // PhotosAssetDecoder does not support gif and animated webp, just use skia to decode it.
        if (mimeType == "image/gif" || mimeType == "image/webp") return true
        return useSkiaForImagePhotosAsset && mimeType.startsWith("image/")
    }

    private suspend fun convertDataSource(dataSource: PhotosAssetDataSource): DataSource {
        // Currently, both SkiaDecoder and SkiaAnimatedDecoder need to load all the image data into memory before decoding.
        // So it makes no sense to cache the original image data locally and then read it again.
        // If SkiaDecoder and SkiaAnimatedDecoder support streaming decoding later,
        // By default, locally cached files can be used first for decoding to avoid taking up too much memory.
        return if (preferFileCacheForImagePhotosAsset) {
            val cachePolicy = downloadCachePolicy
            val (cacheFilePath, dataFrom) = cacheDataSource(dataSource, cachePolicy)
            FileDataSource(cacheFilePath, downloadCache.fileSystem, dataFrom)
        } else {
            val bytes = readBytes(dataSource)
            ByteArrayDataSource(data = bytes, dataFrom = DataFrom.LOCAL)
        }
    }

    private suspend fun cacheDataSource(
        dataSource: PhotosAssetDataSource,
        cachePolicy: CachePolicy
    ): Pair<Path, DataFrom> {
        if (!cachePolicy.isReadAndWrite) {
            throw Exception("Cache policy disabled read and write")
        }

        val cacheKey = dataSource.key
        return downloadCache.withLock(cacheKey) {
            readCache(downloadCache, cacheKey)?.let { it to DataFrom.DOWNLOAD_CACHE }
                ?: (writeCache(downloadCache, cacheKey, dataSource) to DataFrom.LOCAL)
        }
    }

    @WorkerThread
    private fun readCache(downloadCache: DiskCache, cacheKey: String): Path? {
        return downloadCache.openSnapshot(cacheKey)?.use { it.data }
    }

    @OptIn(ExperimentalForeignApi::class)
    @WorkerThread
    private suspend fun writeCache(
        downloadCache: DiskCache,
        cacheKey: String,
        dataSource: PhotosAssetDataSource
    ): Path {
        val editor = downloadCache.openEditor(cacheKey)
            ?: throw IOException("Disk cache cannot be used")
        val sink = downloadCache.fileSystem.sink(editor.data).buffer()
        try {
            suspendCancellableCoroutine { continuation ->
                val options = PHAssetResourceRequestOptions().apply {
                    this.networkAccessAllowed = dataSource.allowNetworkAccess
                }
                PHAssetResourceManager.defaultManager().requestDataForAssetResource(
                    resource = dataSource.resource,
                    options = options,
                    dataReceivedHandler = { chunk ->
                        val byteVars = chunk?.bytes?.reinterpret<ByteVar>()
                        if (byteVars != null) {
                            val byteArray = ByteArray(chunk.length.toInt())
                            byteArray.usePinned { pinned ->
                                memcpy(pinned.addressOf(0), byteVars, chunk.length)
                            }
                            sink.write(byteArray)
                        }
                    },
                    completionHandler = { error ->
                        if (error == null) {
                            continuation.resumeWith(Result.success(true))
                        } else {
                            val message =
                                "Failed to write PHAssetResource '${dataSource.resource.originalFilename}' to cache: ${error.localizedDescription}"
                            continuation.resumeWithException(IOException(message))
                        }
                    },
                )
            }
            editor.commit()
            return editor.data
        } catch (t: Throwable) {
            editor.abort()
            throw t
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun readBytes(dataSource: PhotosAssetDataSource): ByteArray {
        return suspendCancellableCoroutine { continuation ->
            val buffer = Buffer()
            val options = PHAssetResourceRequestOptions().apply {
                this.networkAccessAllowed = dataSource.allowNetworkAccess
            }
            PHAssetResourceManager.defaultManager().requestDataForAssetResource(
                resource = dataSource.resource,
                options = options,
                dataReceivedHandler = { chunk ->
                    val byteVars = chunk?.bytes?.reinterpret<ByteVar>()
                    if (byteVars != null) {
                        val byteArray = ByteArray(chunk.length.toInt())
                        byteArray.usePinned { pinned ->
                            memcpy(pinned.addressOf(0), byteVars, chunk.length)
                        }
                        buffer.write(byteArray)
                    }
                },
                completionHandler = { error ->
                    val byteArray = buffer.readByteArray()
                    if (byteArray.isNotEmpty()) {
                        continuation.resumeWith(Result.success(byteArray))
                    } else {
                        val message =
                            "Failed get bytes for PHAssetResource '${dataSource.resource.originalFilename}': ${error?.localizedDescription}"
                        continuation.resumeWithException(IOException(message))
                    }
                },
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PhotosAssetUriFetcher
        if (localIdentifier != other.localIdentifier) return false
        if (preferredThumbnail != other.preferredThumbnail) return false
        if (allowNetworkAccess != other.allowNetworkAccess) return false
        if (useSkiaForImagePhotosAsset != other.useSkiaForImagePhotosAsset) return false
        if (preferFileCacheForImagePhotosAsset != other.preferFileCacheForImagePhotosAsset) return false
        if (downloadCache != other.downloadCache) return false
        if (downloadCachePolicy != other.downloadCachePolicy) return false
        return true
    }

    override fun hashCode(): Int {
        var result = localIdentifier.hashCode()
        result = 31 * result + preferredThumbnail.hashCode()
        result = 31 * result + allowNetworkAccess.hashCode()
        result = 31 * result + useSkiaForImagePhotosAsset.hashCode()
        result = 31 * result + preferFileCacheForImagePhotosAsset.hashCode()
        result = 31 * result + downloadCache.hashCode()
        result = 31 * result + downloadCachePolicy.hashCode()
        return result
    }

    override fun toString(): String {
        return "PhotosAssetUriFetcher(" +
                "localIdentifier='$localIdentifier', " +
                "preferredThumbnail=$preferredThumbnail, " +
                "allowNetworkAccess=$allowNetworkAccess, " +
                "useSkiaForImagePhotosAsset=$useSkiaForImagePhotosAsset, " +
                "preferFileCacheForImagePhotosAsset=$preferFileCacheForImagePhotosAsset, " +
                "downloadCache=$downloadCache, " +
                "downloadCachePolicy=$downloadCachePolicy)"
    }

    class Factory : Fetcher.Factory {

        override val sortWeight: Int = SORT_WEIGHT

        override fun create(requestContext: RequestContext): PhotosAssetUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            val localIdentifier = parseLocalIdentifier(uri) ?: return null
            val preferredThumbnail = request.preferThumbnailForPhotosAsset ?: false
            val allowNetworkAccess = request.allowNetworkAccessPhotosAsset ?: false
            val useSkiaForImagePhotosAsset = request.useSkiaForImagePhotosAsset ?: false
            val preferFileCacheForImagePhotosAsset =
                request.preferFileCacheForImagePhotosAsset ?: false
            val downloadCachePolicy = request.downloadCachePolicy
            return PhotosAssetUriFetcher(
                localIdentifier = localIdentifier,
                preferredThumbnail = preferredThumbnail,
                allowNetworkAccess = allowNetworkAccess,
                useSkiaForImagePhotosAsset = useSkiaForImagePhotosAsset,
                preferFileCacheForImagePhotosAsset = preferFileCacheForImagePhotosAsset,
                downloadCache = requestContext.sketch.downloadCache,
                downloadCachePolicy = downloadCachePolicy,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other != null && this::class == other::class
        }

        override fun hashCode(): Int = this::class.hashCode()

        override fun toString(): String = "PhotosAssetUriFetcher"
    }
}