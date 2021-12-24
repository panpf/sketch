package com.github.panpf.sketch3.common.fetch

import android.net.Uri
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.ImageRequest
import com.github.panpf.sketch3.common.cache.CachePolicy
import com.github.panpf.sketch3.common.cache.disk.DiskCache
import com.github.panpf.sketch3.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch3.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch3.common.http.HttpStack
import com.github.panpf.sketch3.download.DownloadRequest
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class HttpUriFetcher(
    private val sketch3: Sketch3,
    private val request: DownloadRequest,
) : Fetcher {

    // To avoid the possibility of repeated downloads or repeated edits to the disk cache due to multithreaded concurrency,
    // these operations need to be performed in a single thread 'singleThreadTaskDispatcher'
    override suspend fun fetch(): FetchResult? {
        val diskCache = sketch3.diskCache
        val repeatTaskFilter = sketch3.repeatTaskFilter
        val httpStack = sketch3.httpStack
        val downloadRequest = request
        val encodedDiskCacheKey = diskCache.encodeKey(downloadRequest.diskCacheKey)
        val diskCachePolicy = downloadRequest.diskCachePolicy
        val repeatTaskFilterKey = request.uri.toString()

        // Avoid repeated downloads whenever disk cache is required
        if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            repeatTaskFilter.getActiveHttpFetchTaskDeferred(repeatTaskFilterKey)?.await()
            diskCache.getActiveEdiTaskDeferred(encodedDiskCacheKey)?.await()
        }
        if (diskCachePolicy.readEnabled) {
            val diskCacheEntry = diskCache[encodedDiskCacheKey]
            if (diskCacheEntry != null) {
                return FetchResult(
                    DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE)
                )
            }
        }

        // Create a download task Deferred and cache it for other tasks to filter repeated downloads
        @Suppress("BlockingMethodInNonBlockingContext")
        val downloadTaskDeferred: Deferred<FetchResult?> =
//            try {
            coroutineScope {
                async(sketch3.httpDownloadTaskDispatcher, start = CoroutineStart.LAZY) {
                    executeHttpDownload(
                        httpStack, diskCachePolicy, diskCache, encodedDiskCacheKey, this
                    )
                }
            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw e
//        }

        if (diskCachePolicy.writeEnabled) {
            repeatTaskFilter.putHttpFetchTaskDeferred(repeatTaskFilterKey, downloadTaskDeferred)
            diskCache.putEdiTaskDeferred(encodedDiskCacheKey, downloadTaskDeferred)
        }
        try {
            return downloadTaskDeferred.await()
        } finally {
            // Cancel and exception both go here
            if (diskCachePolicy.writeEnabled) {
                repeatTaskFilter.removeHttpFetchTaskDeferred(repeatTaskFilterKey)
                diskCache.removeEdiTaskDeferred(encodedDiskCacheKey)
            }
        }
    }

    @Throws(IOException::class)
    private fun executeHttpDownload(
        httpStack: HttpStack,
        diskCachePolicy: CachePolicy,
        diskCache: DiskCache,
        encodedDiskCacheKey: String,
        coroutineScope: CoroutineScope,
    ): FetchResult? {
        val response = httpStack.getResponse(request.uri.toString())
        val responseCode = response.code
        if (responseCode != 200) {
            throw IOException("HTTP code error. code=$responseCode, message=${response.message}. ${request.uri}")
        }

        val diskCacheEditor = if (diskCachePolicy.writeEnabled) {
            diskCache.edit(encodedDiskCacheKey)
        } else {
            null
        }
        return if (diskCacheEditor != null) {
            writeToDiskCache(
                response, diskCacheEditor, diskCache, encodedDiskCacheKey, coroutineScope
            )?.run {
                if (diskCachePolicy.readEnabled) {
                    FetchResult(DiskCacheDataSource(this, DataFrom.NETWORK))
                } else {
                    this.newInputStream()
                        .use { it.readBytes() }
                        .run { FetchResult(ByteArrayDataSource(this, DataFrom.NETWORK)) }
                }
            }
        } else {
            writeToByteArray(response, coroutineScope)?.run {
                FetchResult(ByteArrayDataSource(this, DataFrom.NETWORK))
            }
        }
    }

    @Throws(IOException::class)
    private fun writeToDiskCache(
        response: HttpStack.Response,
        diskCacheEditor: DiskCache.Editor,
        diskCache: DiskCache,
        diskCacheKey: String,
        coroutineScope: CoroutineScope,
    ): DiskCache.Entry? = try {
        val readLength = response.content.use { input ->
            diskCacheEditor.newOutputStream().use { out ->
                input.copyToWithActive(
                    out,
                    coroutineScope = coroutineScope,
                    contentLength = response.contentLength
                )
            }
        }
        if (coroutineScope.isActive) {
            diskCacheEditor.commit()
            diskCache[diskCacheKey].apply {
                if (this == null) {
                    throw IOException("Disk cache loss after write. key: $diskCacheKey")
                }
            }
        } else if (!response.isContentChunked && readLength == response.contentLength) {
            diskCacheEditor.commit()
            diskCache[diskCacheKey].apply {
                if (this == null) {
                    throw IOException("Disk cache loss after write. key: $diskCacheKey")
                }
            }
        } else {
            diskCacheEditor.abort()
            null
        }
    } catch (e: IOException) {
        diskCacheEditor.abort()
        throw e
    }

    private fun writeToByteArray(
        response: HttpStack.Response,
        coroutineScope: CoroutineScope
    ): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.use { out ->
            response.content.use { input ->
                input.copyToWithActive(
                    out,
                    coroutineScope = coroutineScope,
                    contentLength = response.contentLength
                )
            }
        }
        return if (coroutineScope.isActive) {
            byteArrayOutputStream.toByteArray()
        } else {
            null
        }
    }

    private fun InputStream.copyToWithActive(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        coroutineScope: CoroutineScope,
        contentLength: Long,
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        var lastNotifyTime = 0L
        val progressListener = request.progressListener
        var lastUpdateProgressBytesCopied = 0L
        while (bytes >= 0 && coroutineScope.isActive) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            if (progressListener != null && contentLength > 0) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastNotifyTime) > 1000) {
                    lastNotifyTime = currentTime
                    val currentBytesCopied = bytesCopied
                    lastUpdateProgressBytesCopied = currentBytesCopied
                    coroutineScope.async(Dispatchers.Main) {
                        progressListener.onUpdateDownloadProgress(contentLength, currentBytesCopied)
                    }
                }
            }
            bytes = read(buffer)
        }
        if (coroutineScope.isActive
            && progressListener != null
            && contentLength > 0
            && bytesCopied > 0
            && lastUpdateProgressBytesCopied != bytesCopied
        ) {
            coroutineScope.async(Dispatchers.Main) {
                progressListener.onUpdateDownloadProgress(contentLength, bytesCopied)
            }
        }
        return bytesCopied
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch3: Sketch3, request: ImageRequest): HttpUriFetcher? =
            if (request is DownloadRequest && isApplicable(request.uri)) {
                HttpUriFetcher(sketch3, request)
            } else {
                null
            }

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}