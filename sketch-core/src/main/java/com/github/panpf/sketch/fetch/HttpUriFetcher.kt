package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.DownloadException
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.DataFrom
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestDepth
import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpgg' uri
 */
class HttpUriFetcher(
    val sketch: Sketch,
    val request: DownloadRequest,
    val url: String
) : Fetcher {

    // To avoid the possibility of repeated downloads or repeated edits to the disk cache due to multithreaded concurrency,
    // these operations need to be performed in a single thread 'singleThreadTaskDispatcher'
    override suspend fun fetch(): FetchResult {
        val diskCache = sketch.diskCache
        val httpStack = sketch.httpStack
        val encodedDiskCacheKey = diskCache.encodeKey(request.diskCacheKey)
        val diskCachePolicy = request.diskCachePolicy

        val diskCacheEditLock = if (diskCachePolicy.isReadOrWrite) {
            diskCache.getOrCreateEditMutexLock(encodedDiskCacheKey)
        } else {
            null
        }
        diskCacheEditLock?.lock()
        try {
            if (diskCachePolicy.readEnabled) {
                val diskCacheEntry = diskCache[encodedDiskCacheKey]
                if (diskCacheEntry != null) {
                    return FetchResult(DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE))
                } else if (request.depth >= RequestDepth.LOCAL) {
                    throw DownloadException("Request depth only to LOCAL")
                }
            }

            return withContext(sketch.networkTaskDispatcher) {
                executeHttpDownload(
                    httpStack, diskCachePolicy, diskCache, encodedDiskCacheKey, this
                )
            } ?: throw CancellationException()
        } finally {
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
        val response = httpStack.getResponse(sketch, request, url)
        val responseCode = response.code
        if (responseCode != 200) {
            throw IOException("HTTP code error. code=$responseCode, message=${response.message}. ${request.uriString}")
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
        val progressListenerDelegate = request.networkProgressListener?.let {
            ProgressListenerDelegate(coroutineScope, it)
        }
        var lastUpdateProgressBytesCopied = 0L
        while (bytes >= 0 && coroutineScope.isActive) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            if (progressListenerDelegate != null && contentLength > 0) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastNotifyTime) > 1000) {
                    lastNotifyTime = currentTime
                    val currentBytesCopied = bytesCopied
                    lastUpdateProgressBytesCopied = currentBytesCopied
                    progressListenerDelegate.onUpdateProgress(
                        request, contentLength, currentBytesCopied
                    )
                }
            }
            bytes = read(buffer)
        }
        if (coroutineScope.isActive
            && progressListenerDelegate != null
            && contentLength > 0
            && bytesCopied > 0
            && lastUpdateProgressBytesCopied != bytesCopied
        ) {
            progressListenerDelegate.onUpdateProgress(request, contentLength, bytesCopied)
        }
        return bytesCopied
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch: Sketch, request: ImageRequest): HttpUriFetcher? =
            if (request is DownloadRequest
                && (request.uri.scheme == "http" || request.uri.scheme == "https")
            ) {
                HttpUriFetcher(sketch, request, request.uriString)
            } else {
                null
            }
    }
}