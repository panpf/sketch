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

@file:Suppress(
    "FoldInitializerAndIfToElvis",
    "UnnecessaryVariable",
    "RedundantConstructorKeyword"
)

package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.cache.downloadCacheKey
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.fetch.internal.copyToWithProgress
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.httpHeaders
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.source.DataFrom.NETWORK
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.util.Uri
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.IOException
import okio.Path
import okio.buffer
import okio.use

/**
 * Check if the uri is a http or https uri
 *
 * @see com.github.panpf.sketch.http.core.common.test.fetch.HttpUriFetcherTest.testIsHttpUri
 */
fun isHttpUri(uri: Uri): Boolean =
    HttpUriFetcher.SCHEME_HTTP.equals(uri.scheme, ignoreCase = true)
            || HttpUriFetcher.SCHEME_HTTPS.equals(uri.scheme, ignoreCase = true)

/**
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpg' uri
 *
 * @see com.github.panpf.sketch.http.core.common.test.fetch.HttpUriFetcherTest
 */
open class HttpUriFetcher constructor(
    val sketch: Sketch,
    val httpStack: HttpStack,
    val request: ImageRequest,
) : Fetcher {

    companion object {
        const val SCHEME_HTTP = "http"
        const val SCHEME_HTTPS = "https"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

    private val downloadCacheKey = request.downloadCacheKey
    private val downloadCacheLockKey = request.downloadCacheKey

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        requiredWorkThread()
        val result = if (request.downloadCachePolicy.isReadOrWrite) {
            sketch.downloadCache.withLock(downloadCacheLockKey) {
                readCache() ?: executeFetch()
            }
        } else {
            executeFetch()
        }
        return result
    }

    private suspend fun executeFetch(): Result<FetchResult> {
        /* Check depth */
        val depth = request.depthHolder.depth
        if (depth >= Depth.LOCAL) {
            val message = "Request depth limited to $depth. ${request.uri}"
            return Result.failure(DepthException(message))
        }

        /* execute download */
        return withContext(sketch.networkTaskDispatcher) {
            // intercept cancel
            if (!isActive) {
                return@withContext Result.failure(CancellationException("Canceled"))
            }

            // open connection
            val url = request.uri.toString()
            return@withContext try {
                httpStack.request(url, request.httpHeaders, request.extras) { response ->
                    // intercept cancel
                    if (!isActive) {
                        return@request Result.failure(CancellationException("Canceled"))
                    }

                    // check code
                    val httpCode = response.code
                    if (httpCode != 200) {
                        val httpMessage = response.message
                        val message = "HTTP code error. $httpCode $httpMessage. ${request.uri}"
                        return@request Result.failure(IOException(message))
                    }

                    // Save to download cache
                    val coroutineScope: CoroutineScope = this@withContext
                    val mimeType = getMimeType(url, response.contentType)
                    if (request.downloadCachePolicy.writeEnabled) {
                        val result = writeCache(coroutineScope, response, mimeType)
                        if (result != null) {
                            return@request result
                        }
                    }

                    // Save to ByteArray
                    val result = readBytes(coroutineScope, response, mimeType)
                    return@request result
                }
            } catch (e: Throwable) {
                return@withContext Result.failure(e)
            }
        }
    }

    @WorkerThread
    private fun readCache(): Result<FetchResult>? {
        if (!request.downloadCachePolicy.readEnabled) return null
        val url = request.uri.toString()
        val downloadCache = sketch.downloadCache
        try {
            return downloadCache.openSnapshot(downloadCacheKey)?.use { snapshot ->
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
                    downloadCache.remove(downloadCacheKey)
                    sketch.logger.w {
                        "HttpUriFetcher. Read contentType disk cache failed, removed cache file. " +
                                "message='${it.message}', " +
                                "cacheKey=$downloadCacheKey. " +
                                "'${request.uri}'"
                    }
                }.getOrNull()
                val mimeType = getMimeType(url, contentType)
                val dataSource = FileDataSource(
                    path = snapshot.data,
                    fileSystem = sketch.fileSystem,
                    dataFrom = DOWNLOAD_CACHE,
                )
                Result.success(FetchResult(dataSource, mimeType))
            }
        } catch (e: Throwable) {
            return Result.failure(e)
        }
    }

    private suspend fun writeCache(
        coroutineScope: CoroutineScope,
        response: Response,
        mimeType: String?
    ): Result<FetchResult>? {
        val downloadCache = sketch.downloadCache

        // Save image data
        val editor = downloadCache.openEditor(downloadCacheKey) ?: return null
        val cachePath: Path = try {
            val contentLength = response.contentLength
            val readLength = response.content().use { content ->
                downloadCache.fileSystem.sink(editor.data).buffer().use { sink ->
                    copyToWithProgress(
                        coroutineScope = coroutineScope,
                        logger = sketch.logger,
                        sink = sink,
                        content = content,
                        request = request,
                        contentLength = contentLength
                    )
                }
            }
            // 'Transform-Encoding: chunked' contentLength is -1
            if (contentLength > 0 && readLength != contentLength) {
                throw IOException("readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uri}")
            }

            val contentType = response.contentType?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            if (contentType != null) {
                downloadCache.fileSystem.sink(editor.metadata).buffer().use { sink ->
                    sink.writeUtf8(contentType)
                }
            }

            editor.commitAndOpenSnapshot()?.use { it.data }
                ?: return Result.failure(IOException("Disk cache loss after write. dataKey='$downloadCacheKey'. ${request.uri}"))
        } catch (e: Throwable) {
            editor.abort()
            return Result.failure(e)
        }

        // Build FetchResult
        val dataSource = if (request.downloadCachePolicy.readEnabled) {
            FileDataSource(
                path = cachePath,
                fileSystem = sketch.fileSystem,
                dataFrom = NETWORK,
            )
        } else {
            try {
                val byteArray = sketch.fileSystem.source(cachePath).buffer().readByteArray()
                ByteArrayDataSource(data = byteArray, dataFrom = NETWORK)
            } catch (e: Throwable) {
                return Result.failure(e)
            }
        }
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    private suspend fun readBytes(
        coroutineScope: CoroutineScope,
        response: Response,
        mimeType: String?
    ): Result<FetchResult> {
        val contentLength = response.contentLength
        val buffer = Buffer()
        val readLength = response.content().use { content ->
            copyToWithProgress(
                coroutineScope = coroutineScope,
                logger = sketch.logger,
                sink = buffer,
                content = content,
                request = request,
                contentLength = contentLength
            )
        }
        if (contentLength > 0 && readLength != contentLength) {
            val message =
                "readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uri}"
            return Result.failure(IOException(message))
        }
        val bytes = buffer.readByteArray()
        val dataSource = ByteArrayDataSource(data = bytes, dataFrom = NETWORK)
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HttpUriFetcher
        if (sketch != other.sketch) return false
        if (httpStack != other.httpStack) return false
        if (request != other.request) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + httpStack.hashCode()
        result = 31 * result + request.hashCode()
        return result
    }

    override fun toString(): String {
        return "HttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request)"
    }

    open class Factory(val httpStack: HttpStack) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri)) return null
            return HttpUriFetcher(
                sketch = requestContext.sketch,
                httpStack = httpStack,
                request = request,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Factory
            if (httpStack != other.httpStack) return false
            return true
        }

        override fun hashCode(): Int {
            return httpStack.hashCode()
        }

        override fun toString(): String = "HttpUriFetcher(httpStack=$httpStack)"
    }
}