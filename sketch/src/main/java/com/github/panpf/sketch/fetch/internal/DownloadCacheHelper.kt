package com.github.panpf.sketch.fetch.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.DataFrom.DISK_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import java.io.IOException

suspend fun <R> safeAccessDownloadCache(
    sketch: Sketch,
    request: ImageRequest,
    block: suspend (helper: DownloadCacheHelper?) -> R
): R =
    if (request.downloadCachePolicy.isReadOrWrite) {
        val helper = DownloadCacheHelper(sketch, request)
        val lock: Mutex = sketch.downloadDiskCache.editLock(helper.keys.lockKey)
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

    private val diskCache = sketch.downloadDiskCache
    val keys = DownloadCacheKeys(request)

    @WorkerThread
    fun read(): FetchResult? {
        requiredWorkThread()
        if (!request.downloadCachePolicy.readEnabled) return null
        val dataDiskCacheSnapshot = diskCache[keys.dataDiskCacheKey] ?: return null

        val contentType = diskCache[keys.contentTypeDiskCacheKey]?.let { snapshot ->
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
            DiskCacheDataSource(sketch, request, DISK_CACHE, dataDiskCacheSnapshot), mimeType
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
            diskCache.edit(keys.dataDiskCacheKey)
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
                diskCache.edit(keys.contentTypeDiskCacheKey)?.apply {
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

            return diskCache[keys.dataDiskCacheKey]
                ?: throw IOException("Disk cache loss after write. ${request.uriString}")
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }
    }
}