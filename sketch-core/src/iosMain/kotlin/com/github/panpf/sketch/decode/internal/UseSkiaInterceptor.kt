/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadAndWrite
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.isPreferredFileCacheForImagePhotosAsset
import com.github.panpf.sketch.request.isUseSkiaForImagePhotosAsset
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.PhotosAssetDataSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
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
 * UseSkiaInterceptor is an Interceptor that determines whether to use Skia to decode images from the iOS Photos library based on the MIME type and request options. If Skia is used for decoding, it replaces the original FetchResult's DataSource with a new DataSource that can be decoded by SkiaDecoder or SkiaAnimatedDecoder.
 *
 * @see com.github.panpf.sketch.core.ios.test.decode.internal.UseSkiaInterceptorTest
 */
class UseSkiaInterceptor : Interceptor {

    companion object {
        const val SORT_WEIGHT = 95  // Must be between FetcherInterceptor and DecoderInterceptor
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    override suspend fun intercept(chain: Interceptor.Chain): Result<ImageData> {
        val sketch = chain.sketch
        val request = chain.request
        val requestContext = chain.requestContext
        val fetchResult = requestContext.fetchResult
        val dataSource = fetchResult?.dataSource
        if (dataSource is PhotosAssetDataSource) {
            val mimeType = fetchResult.mimeType
            val useSkiaForImagePhotosAsset = request.isUseSkiaForImagePhotosAsset
            if (shouldUseSkia(mimeType, useSkiaForImagePhotosAsset)) {
                // Currently, both SkiaDecoder and SkiaAnimatedDecoder need to load all the image data into memory before decoding.
                // So it makes no sense to cache the original image data locally and then read it again.
                // If SkiaDecoder and SkiaAnimatedDecoder support streaming decoding later,
                // By default, locally cached files can be used first for decoding to avoid taking up too much memory.
                val preferredFileCacheForImagePhotosAsset =
                    request.isPreferredFileCacheForImagePhotosAsset
                val newDataSource = withContext(sketch.decodeTaskDispatcher) {
                    if (preferredFileCacheForImagePhotosAsset) {
                        val cachePolicy = request.downloadCachePolicy
                        val (cachePath, dataFrom) = getCacheFile(sketch, dataSource, cachePolicy)
                        val fileSystem = sketch.downloadCache.fileSystem
                        FileDataSource(cachePath, fileSystem, dataFrom)
                    } else {
                        val bytes = getBytes(dataSource)
                        ByteArrayDataSource(data = bytes, dataFrom = DataFrom.LOCAL)
                    }
                }
                val newFetchResult = FetchResult(newDataSource, mimeType)
                requestContext.fetchResult = newFetchResult
            }
        }
        return chain.proceed(request)
    }

    private fun shouldUseSkia(mimeType: String?, useSkiaForImagePhotosAsset: Boolean): Boolean {
        if (mimeType == null) return false
        // PhotosAssetDecoder does not support gif and animated webp, just use skia to decode it.
        if (mimeType == "image/gif" || mimeType == "image/webp") return true
        return useSkiaForImagePhotosAsset && mimeType.startsWith("image/")
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun getBytes(dataSource: PhotosAssetDataSource): ByteArray {
        return suspendCancellableCoroutine { continuation ->
            val buffer = Buffer()
            val options = PHAssetResourceRequestOptions().apply {
                this.networkAccessAllowed = dataSource.networkAccessAllowed
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

    private suspend fun getCacheFile(
        sketch: Sketch,
        dataSource: PhotosAssetDataSource,
        cachePolicy: CachePolicy
    ): Pair<Path, DataFrom> {
        if (!cachePolicy.isReadAndWrite) {
            throw Exception("Cache policy disabled read and write")
        }

        val cacheKey = dataSource.key
        val downloadCache = sketch.downloadCache
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
                    this.networkAccessAllowed = dataSource.networkAccessAllowed
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "UseSkiaInterceptor"
}