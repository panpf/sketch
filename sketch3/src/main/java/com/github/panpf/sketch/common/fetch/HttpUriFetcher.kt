package com.github.panpf.sketch.common.fetch

import android.net.Uri
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.DataFrom
import com.github.panpf.sketch.common.DownloadableRequest
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ListenerInfo
import com.github.panpf.sketch.common.cache.CachePolicy
import com.github.panpf.sketch.common.cache.DiskCache
import com.github.panpf.sketch.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch.common.http.HttpStack
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class HttpUriFetcher(
    private val sketch: Sketch,
    private val request: DownloadableRequest,
    private val listenerInfo: ListenerInfo<ImageRequest, ImageResult>?
) : Fetcher {

    // To avoid the possibility of repeated downloads or repeated edits to the disk cache due to multithreaded concurrency,
    // these operations need to be performed in a single thread 'singleThreadTaskDispatcher'
    override suspend fun fetch(): FetchResult {
        val diskCache = sketch.diskCache
        val repeatTaskFilter = sketch.repeatTaskFilter
        val httpStack = sketch.httpStack
        val encodedDiskCacheKey = diskCache.encodeKey(request.diskCacheKey)
        val diskCachePolicy = request.diskCachePolicy
        val repeatTaskFilterKey = request.uri.toString()

        // Avoid repeated downloads whenever disk cache is required
        val repeatTaskLock = if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            repeatTaskFilter.getOrCreateHttpFetchMutexLock(repeatTaskFilterKey)
        } else {
            null
        }
        val diskCacheEditLock = if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            diskCache.getOrCreateEditMutexLock(encodedDiskCacheKey)
        } else {
            null
        }
        repeatTaskLock?.lock()
        diskCacheEditLock?.lock()
        try {
            if (diskCachePolicy.readEnabled) {
                val diskCacheEntry = diskCache[encodedDiskCacheKey]
                if (diskCacheEntry != null) {
                    return FetchResult(
                        DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE)
                    )
                }
            }

            return withContext(sketch.httpDownloadTaskDispatcher) {
                executeHttpDownload(
                    httpStack, diskCachePolicy, diskCache, encodedDiskCacheKey, this
                )
            } ?: throw CancellationException()
        } finally {
            repeatTaskLock?.unlock()
            diskCacheEditLock?.unlock()
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
        val progressListener = listenerInfo?.httpFetchProgressListener
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
                    @Suppress("DeferredResultUnused")
                    coroutineScope.async(Dispatchers.Main) {
                        progressListener.onUpdateProgress(
                            request,
                            contentLength,
                            currentBytesCopied
                        )
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
            @Suppress("DeferredResultUnused")
            coroutineScope.async(Dispatchers.Main) {
                progressListener.onUpdateProgress(request, contentLength, bytesCopied)
            }
        }
        return bytesCopied
    }

    class Factory : Fetcher.Factory {
        override fun create(
            sketch: Sketch,
            request: ImageRequest,
            listenerInfo: ListenerInfo<ImageRequest, ImageResult>?
        ): HttpUriFetcher? =
            if (request is DownloadableRequest && isApplicable(request.uri)) {
                HttpUriFetcher(sketch, request, listenerInfo)
            } else {
                null
            }

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}