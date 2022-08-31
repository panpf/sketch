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
package com.github.panpf.sketch.fetch

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
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
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.requiredWorkThread
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
        const val SCHEME1 = "https"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

    @WorkerThread
    override suspend fun fetch(): FetchResult {
        requiredWorkThread()
        return if (request.downloadCachePolicy.isReadOrWrite) {
            lockDownloadCache {
                ifOrNull(request.downloadCachePolicy.readEnabled) { readCache() }
                    ?: executeFetch()
            }
        } else {
            executeFetch()
        }
    }

    private suspend fun executeFetch(): FetchResult {
        /* verify depth */
        val depth = request.depth
        if (depth >= Depth.LOCAL) {
            throw DepthException("Request depth limited to $depth. ${request.uriString}")
        }

        /* execute download */
        return withContext(sketch.networkTaskDispatcher) {
            // open connection
            // Currently running on a limited number of IO contexts, so this warning can be ignored
            @Suppress("BlockingMethodInNonBlockingContext")
            val response = sketch.httpStack.getResponse(request, url)

            // intercept Cancel
            if (!isActive) {
                throw CancellationException()
            }

            // check response
            val responseCode = response.code
            if (responseCode != 200) {
                throw IOException("HTTP code error. code=$responseCode, message=${response.message}. ${request.uriString}")
            }
            val isContentChunked =
                response.getHeaderField("Transfer-Encoding")?.let { transferEncoding ->
                    "chunked".equals(transferEncoding.trim { it <= ' ' }, ignoreCase = true)
                } ?: false
            if (isContentChunked) {
                throw IOException("Not supported 'chunked' for 'Transfer-Encoding'. ${request.uriString}")
            }

            // write to disk or byte array
            val diskCacheSnapshot = ifOrNull(request.downloadCachePolicy.writeEnabled) {
                writeCache(response, this)
            }
            val dataSource = if (diskCacheSnapshot != null) {
                if (request.downloadCachePolicy.readEnabled) {
                    DiskCacheDataSource(sketch, request, NETWORK, diskCacheSnapshot)
                } else {
                    // Currently running on a limited number of IO contexts, so this warning can be ignored
                    @Suppress("BlockingMethodInNonBlockingContext")
                    diskCacheSnapshot.newInputStream()
                        .use { it.readBytes() }
                        .let { ByteArrayDataSource(sketch, request, NETWORK, it) }
                }
            } else {
                val byteArrayOutputStream = ByteArrayOutputStream()
                byteArrayOutputStream.use { out ->
                    response.content.use { input ->
                        // Currently running on a limited number of IO contexts, so this warning can be ignored
                        @Suppress("BlockingMethodInNonBlockingContext")
                        copyToWithActive(
                            request = request,
                            inputStream = input,
                            outputStream = out,
                            coroutineScope = this@withContext,
                            contentLength = response.contentLength
                        )
                    }
                }
                val byteArray = byteArrayOutputStream.toByteArray()
                ByteArrayDataSource(sketch, request, NETWORK, byteArray)
            }
            val mimeType = getMimeType(request.uriString, response.contentType)
            FetchResult(dataSource, mimeType)
        }
    }

    private suspend fun <R> lockDownloadCache(
        block: suspend () -> R
    ): R {
        val lock: Mutex = sketch.downloadCache.editLock(request.downloadCacheLockKey)
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    @WorkerThread
    private fun readCache(): FetchResult? {
        val downloadCache = sketch.downloadCache
        val dataKey = request.downloadCacheDataKey
        val contentTypeKey = request.downloadCacheContentTypeKey

        val dataDiskCacheSnapshot = downloadCache[dataKey] ?: return null
        val contentType = downloadCache[contentTypeKey]?.let { snapshot ->
            try {
                snapshot.newInputStream()
                    .use { it.bufferedReader().readText() }
                    .takeIf { it.isNotEmpty() && it.isNotBlank() }
                    ?: throw IOException("contentType disk cache text empty")
            } catch (e: Exception) {
                e.printStackTrace()
                snapshot.remove()
                null
            }
        }
        val mimeType = getMimeType(request.uriString, contentType)
        return FetchResult(
            DiskCacheDataSource(
                sketch = sketch,
                request = request,
                dataFrom = DOWNLOAD_CACHE,
                snapshot = dataDiskCacheSnapshot
            ),
            mimeType
        )
    }

    @WorkerThread
    @Throws(IOException::class)
    private fun writeCache(
        response: Response,
        coroutineScope: CoroutineScope
    ): DiskCache.Snapshot? {
        val downloadCache = sketch.downloadCache
        val dataKey = request.downloadCacheDataKey
        val contentTypeKey = request.downloadCacheContentTypeKey

        val diskCacheEditor = ifOrNull(request.downloadCachePolicy.writeEnabled) {
            downloadCache.edit(dataKey)
        } ?: return null
        try {
            val contentLength = response.contentLength
            val readLength = response.content.use { inputStream ->
                diskCacheEditor.newOutputStream().buffered().use { outputStream ->
                    copyToWithActive(
                        request = request,
                        inputStream = inputStream,
                        outputStream = outputStream,
                        coroutineScope = coroutineScope,
                        contentLength = contentLength
                    )
                }
            }

            if (readLength == contentLength) {
                diskCacheEditor.commit()
            } else {
                diskCacheEditor.abort()
                throw IOException("readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}")
            }

            // save contentType
            val contentType = response.contentType?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            if (contentType != null) {
                downloadCache.edit(contentTypeKey)?.apply {
                    try {
                        newOutputStream().bufferedWriter().use {
                            it.write(contentType)
                        }
                        commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        abort()
                    }
                }
            }

            return downloadCache[dataKey]
                ?: throw IOException("Disk cache loss after write. ${request.uriString}")
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): HttpUriFetcher? =
            if (
                SCHEME.equals(request.uri.scheme, ignoreCase = true)
                || SCHEME1.equals(request.uri.scheme, ignoreCase = true)
            ) {
                HttpUriFetcher(sketch, request, request.uriString)
            } else {
                null
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

val ImageRequest.downloadCacheDataKey: String
    get() = uriString

val ImageRequest.downloadCacheContentTypeKey: String
    get() = "${uriString}_contentType"

val ImageRequest.downloadCacheLockKey: String
    get() = uriString