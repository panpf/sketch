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

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.internal.copyToWithActive
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.requiredWorkThread
import com.github.panpf.sketch.util.toUri
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

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

    private val dataKey = request.uriString
    private val contentTypeKey = "${request.uriString}_contentType"
    private val downloadCacheLockKey = request.uriString

    @WorkerThread
    override suspend fun fetch(): Result<FetchResult> {
        requiredWorkThread()
        val result = if (request.downloadCachePolicy.isReadOrWrite) {
            lockDownloadCache {
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
                return@withContext Result.failure(CancellationException())
            }

            // open connection
            val response = try {
                sketch.httpStack.getResponse(request, url)
            } catch (e: Throwable) {
                return@withContext Result.failure(e)
            }

            // intercept cancel
            if (!isActive) {
                return@withContext Result.failure(CancellationException())
            }

            // check code
            val httpCode = response.code
            if (httpCode != 200) {
                val httpMessage = response.message
                val message =
                    "HTTP status must be 200. $httpCode $httpMessage. ${request.uriString}"
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
            val dataSnapshot = downloadCache[dataKey] ?: return null
            val contentType = downloadCache[contentTypeKey]?.let { contentTypeSnapshot ->
                try {
                    contentTypeSnapshot.newInputStream()
                        .use { it.bufferedReader().readText() }
                        .takeIf { it.isNotEmpty() && it.isNotBlank() }
                        ?: throw IOException("contentType disk cache text empty")
                } catch (e: Throwable) {
                    e.printStackTrace()
                    contentTypeSnapshot.remove()
                    sketch.logger.w("HttpUriFetcher") {
                        "Read contentType disk cache failed, removed cache file. " +
                                "message='${e.message}', " +
                                "contentTypeKey=$contentTypeKey. " +
                                "'${request.uriString}'"
                    }
                    null
                }
            }
            val mimeType = getMimeType(request.uriString, contentType)
            val dataSource =
                DiskCacheDataSource(sketch, request, DOWNLOAD_CACHE, dataSnapshot)
            return Result.success(FetchResult(dataSource, mimeType))
        } catch (e: Throwable) {
            return Result.failure(e)
        }
    }

    private fun CoroutineScope.writeCache(
        response: Response,
        mimeType: String?
    ): Result<FetchResult>? {
        val downloadCache = sketch.downloadCache

        // Save image data
        val diskCacheEditor = downloadCache.edit(dataKey) ?: return null
        try {
            val contentLength = response.contentLength
            val readLength = response.content.use { inputStream ->
                diskCacheEditor.newOutputStream().buffered().use { outputStream ->
                    copyToWithActive(request, inputStream, outputStream, contentLength)
                }
            }
            // 'Transform-Encoding: chunked' contentLength is -1
            if (contentLength > 0 && readLength != contentLength) {
                throw IOException("readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}")
            }
            diskCacheEditor.commit()
        } catch (e: Throwable) {
            diskCacheEditor.abort()
            return Result.failure(e)
        }

        // Save contentType
        val contentType = response.contentType?.takeIf { it.isNotEmpty() && it.isNotBlank() }
        val contentTypeEditor =
            if (contentType != null) downloadCache.edit(contentTypeKey) else null
        if (contentTypeEditor != null) {
            try {
                contentTypeEditor.newOutputStream().bufferedWriter().use {
                    it.write(contentType)
                }
                contentTypeEditor.commit()
            } catch (e: Throwable) {
                e.printStackTrace()
                contentTypeEditor.abort()
            }
        }

        // Build FetchResult
        val snapshot = downloadCache[dataKey]
        if (snapshot == null) {
            return Result.failure(IOException("Disk cache loss after write. dataKey='$dataKey'. ${request.uriString}"))
        }
        val dataSource = if (request.downloadCachePolicy.readEnabled) {
            DiskCacheDataSource(sketch, request, NETWORK, snapshot)
        } else {
            try {
                snapshot.newInputStream()
                    .use { it.readBytes() }
                    .let { ByteArrayDataSource(sketch, request, NETWORK, it) }
            } catch (e: Throwable) {
                return Result.failure(e)
            }
        }
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    private fun CoroutineScope.readBytes(
        response: Response,
        mimeType: String?
    ): Result<FetchResult> {
        val contentLength = response.contentLength
        val byteArrayOutputStream = ByteArrayOutputStream()
        val readLength = byteArrayOutputStream.use { outputStream ->
            response.content.use { inputStream ->
                copyToWithActive(request, inputStream, outputStream, contentLength)
            }
        }
        if (contentLength > 0 && readLength != contentLength) {
            val message =
                "readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}"
            return Result.failure(IOException(message))
        }
        val bytes = byteArrayOutputStream.toByteArray()
        val dataSource = ByteArrayDataSource(sketch, request, NETWORK, bytes)
        val result = FetchResult(dataSource, mimeType)
        return Result.success(result)
    }

    private suspend fun <R> lockDownloadCache(block: suspend () -> R): R {
        val lock: Mutex = sketch.downloadCache.editLock(downloadCacheLockKey)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }
}

