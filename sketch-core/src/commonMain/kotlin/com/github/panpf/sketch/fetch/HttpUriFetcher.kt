/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
@file:Suppress("FoldInitializerAndIfToElvis", "UnnecessaryVariable")    // for debug

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.fetch.internal.writeAllWithProgress
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.IOException
import okio.Path
import okio.buffer
import okio.use

/**
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpg' uri
 */
open class HttpUriFetcher(
    val sketch: Sketch,
    val request: ImageRequest,
    val url: String
) : Fetcher {

    companion object {
        const val SCHEME = "http"
        const val SCHEME_HTTPS = "https"

        @Suppress("unused")
        @Deprecated("Use SCHEME_HTTPS instead", ReplaceWith("SCHEME_HTTPS"))
        const val SCHEME1 = SCHEME_HTTPS
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

    private val cacheKey = request.uriString
    private val downloadCacheLockKey = request.uriString

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        requiredWorkThread()
        val result = if (request.downloadCachePolicy.isReadOrWrite) {
            sketch.downloadCache.withLock(downloadCacheLockKey) {
                request.downloadCachePolicy
                    .takeIf { it.readEnabled }
                    ?.let { readCache() }
                    ?: executeFetch()
            }
        } else {
            executeFetch()
        }
        return result
    }

    private suspend fun executeFetch(): Result<FetchResult> {
        /* Check depth */
        val depth = request.depth
        if (depth >= Depth.LOCAL) {
            val message = "Request depth limited to $depth. ${request.uriString}"
            return Result.failure(DepthException(message))
        }

        /* execute download */
        return withContext(sketch.networkTaskDispatcher) {
            // intercept cancel
            if (!isActive) {
                return@withContext Result.failure(CancellationException("Canceled"))
            }

            // open connection
            val response = try {
                sketch.httpStack.getResponse(request, url)
            } catch (e: Throwable) {
                return@withContext Result.failure(e)
            }

            // intercept cancel
            if (!isActive) {
                return@withContext Result.failure(CancellationException("Canceled"))
            }

            // check code
            val httpCode = response.code
            if (httpCode != 200) {
                val httpMessage = response.message
                val message = "HTTP code error. $httpCode $httpMessage. ${request.uriString}"
                return@withContext Result.failure(IOException(message))
            }

            // Save to download cache
            val mimeType = getMimeType(request.uriString, response.contentType)
            if (request.downloadCachePolicy.writeEnabled) {
                val result = writeCache(response, mimeType)
                if (result != null) {
                    return@withContext result
                }
            }

            // Save to ByteArray
            val result = readBytes(response, mimeType)
            return@withContext result
        }
    }

    @WorkerThread
    private fun readCache(): Result<FetchResult>? {
        val downloadCache = sketch.downloadCache
        try {
            return downloadCache.openSnapshot(cacheKey)?.use { snapshot ->
                val contentType: String? = runCatching {
                    if (downloadCache.fileSystem.exists(snapshot.metadata)) {
                        downloadCache.fileSystem.source(snapshot.metadata).use {
                            it.buffer().readUtf8()
                        }.takeIf { it.isNotEmpty() && it.isNotBlank() }
                    } else {
                        null
                    }
                }.onFailure {
                    it.printStackTrace()
                    downloadCache.remove(cacheKey)
                    sketch.logger.w {
                        "HttpUriFetcher. Read contentType disk cache failed, removed cache file. " +
                                "message='${it.message}', " +
                                "cacheKey=$cacheKey. " +
                                "'${request.uriString}'"
                    }
                }.getOrNull()
                val mimeType = getMimeType(request.uriString, contentType)
                val dataSource = DiskCacheDataSource(
                    sketch = sketch,
                    request = request,
                    dataFrom = DOWNLOAD_CACHE,
                    fileSystem = downloadCache.fileSystem,
                    path = snapshot.data
                )
                Result.success(FetchResult(dataSource, mimeType))
            }
        } catch (e: Throwable) {
            return Result.failure(e)
        }
    }

    private suspend fun writeCache(
        response: Response,
        mimeType: String?
    ): Result<FetchResult>? {
        val downloadCache = sketch.downloadCache

        // Save image data
        val editor = downloadCache.openEditor(cacheKey) ?: return null
        val cachePath: Path = try {
            val contentLength = response.contentLength
            val readLength = response.content().use { content ->
                downloadCache.fileSystem.sink(editor.data).buffer().use { sink ->
                    sink.writeAllWithProgress(request, content, contentLength)
                }
            }
            // 'Transform-Encoding: chunked' contentLength is -1
            if (contentLength > 0 && readLength != contentLength) {
                throw IOException("readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}")
            }

            val contentType = response.contentType?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            if (contentType != null) {
                downloadCache.fileSystem.sink(editor.metadata).buffer().use { sink ->
                    sink.writeUtf8(contentType)
                }
            }

            editor.commitAndOpenSnapshot()?.use { it.data }
                ?: return Result.failure(IOException("Disk cache loss after write. dataKey='$cacheKey'. ${request.uriString}"))
        } catch (e: Throwable) {
            editor.abort()
            return Result.failure(e)
        }

        // Build FetchResult
        val dataSource = if (request.downloadCachePolicy.readEnabled) {
            DiskCacheDataSource(
                sketch = sketch,
                request = request,
                dataFrom = NETWORK,
                fileSystem = sketch.fileSystem,
                path = cachePath
            )
        } else {
            try {
                val byteArray = sketch.fileSystem.source(cachePath).buffer().readByteArray()
                ByteArrayDataSource(sketch, request, NETWORK, byteArray)
            } catch (e: Throwable) {
                return Result.failure(e)
            }
        }
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    private suspend fun readBytes(
        response: Response,
        mimeType: String?
    ): Result<FetchResult> {
        val contentLength = response.contentLength
        val buffer = Buffer()
        val readLength = response.content().use { content ->
            buffer.writeAllWithProgress(request, content, contentLength)
        }
        if (contentLength > 0 && readLength != contentLength) {
            val message =
                "readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}"
            return Result.failure(IOException(message))
        }
        val bytes = buffer.readByteArray()
        val dataSource = ByteArrayDataSource(sketch, request, NETWORK, bytes)
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): HttpUriFetcher? {
            val scheme = request.uriString.toUri().scheme
            return if (
                SCHEME.equals(scheme, ignoreCase = true)
                || SCHEME_HTTPS.equals(scheme, ignoreCase = true)
            ) {
                HttpUriFetcher(sketch, request, request.uriString)
            } else {
                null
            }
        }

        override fun toString(): String = "HttpUriFetcher"

        @Suppress("RedundantOverride")
        override fun equals(other: Any?): Boolean {
            // If you add construction parameters to this class, you need to change it here
            return super.equals(other)
        }

        @Suppress("RedundantOverride")
        override fun hashCode(): Int {
            // If you add construction parameters to this class, you need to change it here
            return super.hashCode()
        }
    }
}