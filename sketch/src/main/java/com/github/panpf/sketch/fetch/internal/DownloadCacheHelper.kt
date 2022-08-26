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
package com.github.panpf.sketch.fetch.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.DOWNLOAD_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import java.io.IOException

// todo improve
suspend fun <R> lockDownloadCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: DownloadCacheHelper?) -> R
): R =
    if (request.downloadCachePolicy.isReadOrWrite) {
        val helper = DownloadCacheHelper(sketch, request)
        val lock: Mutex = sketch.downloadCache.editLock(helper.keys.lockKey)
        lock.lock()
        try {
            block(helper)
        } finally {
            lock.unlock()
        }
    } else {
        block(null)
    }

class DownloadCacheKeys constructor(request: ImageRequest) {
    val dataDiskCacheKey: String = request.uriString
    val contentTypeDiskCacheKey: String by lazy {
        request.uriString + "_contentType"
    }
    val lockKey: String = request.uriString
}

class DownloadCacheHelper(val sketch: Sketch, val request: ImageRequest) {

    private val downloadCache = sketch.downloadCache
    val keys = DownloadCacheKeys(request)

    @WorkerThread
    fun read(): FetchResult? {
        requiredWorkThread()
        if (!request.downloadCachePolicy.readEnabled) return null
        val dataDiskCacheSnapshot = downloadCache[keys.dataDiskCacheKey] ?: return null

        val contentType = downloadCache[keys.contentTypeDiskCacheKey]?.let { snapshot ->
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
            DiskCacheDataSource(sketch, request, DOWNLOAD_CACHE, dataDiskCacheSnapshot), mimeType
        )
    }

    @WorkerThread
    @Throws(IOException::class)
    fun write(
        response: Response,
        coroutineScope: CoroutineScope
    ): DiskCache.Snapshot? {
        requiredWorkThread()
        val diskCacheEditor = ifOrNull(request.downloadCachePolicy.writeEnabled) {
            downloadCache.edit(keys.dataDiskCacheKey)
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
                downloadCache.edit(keys.contentTypeDiskCacheKey)?.apply {
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

            return downloadCache[keys.dataDiskCacheKey]
                ?: throw IOException("Disk cache loss after write. ${request.uriString}")
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }
    }
}