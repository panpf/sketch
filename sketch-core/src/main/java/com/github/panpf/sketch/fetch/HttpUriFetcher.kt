package com.github.panpf.sketch.fetch

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
import com.github.panpf.sketch.request.internal.RequestDepthException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.cancellation.CancellationException

/**
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpgg' uri
 */
class HttpUriFetcher(
    val sketch: Sketch,
    val request: DownloadRequest,
    val url: String
) : Fetcher {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun fetch(): FetchResult {
        val diskCacheHelper = HttpDiskCacheHelper.from(sketch, request)
        diskCacheHelper?.lock?.lock()
        try {
            return diskCacheHelper?.read() ?: execute(diskCacheHelper)
        } finally {
            diskCacheHelper?.lock?.unlock()
        }
    }

    @Throws(IOException::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun execute(diskCacheHelper: HttpDiskCacheHelper?): FetchResult =
        withContext(sketch.networkTaskDispatcher) {
            val response = sketch.httpStack.getResponse(sketch, request, url)
            val responseCode = response.code
            if (responseCode != 200) {
                throw IOException("HTTP code error. code=$responseCode, message=${response.message}. ${request.uriString}")
            }

            val diskCacheEditor = diskCacheHelper?.newEditor()
            if (diskCacheEditor != null) {
                diskCacheHelper.write(response, diskCacheEditor, this@HttpUriFetcher, this)
            } else {
                writeToByteArray(response, this)
            }
        }

    @Throws(IOException::class)
    private fun writeToByteArray(
        response: HttpStack.Response,
        coroutineScope: CoroutineScope
    ): FetchResult {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.use { out ->
            response.content.use { input ->
                copyToWithActive(
                    inputStream = input,
                    out = out,
                    coroutineScope = coroutineScope,
                    contentLength = response.contentLength
                )
            }
        }
        return if (coroutineScope.isActive) {
            FetchResult(ByteArrayDataSource(byteArrayOutputStream.toByteArray(), DataFrom.NETWORK))
        } else {
            throw CancellationException()
        }
    }

    @Throws(IOException::class)
    private fun copyToWithActive(
        inputStream: InputStream,
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        coroutineScope: CoroutineScope,
        contentLength: Long,
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = inputStream.read(buffer)
        var lastNotifyTime = 0L
        val progressListenerDelegate = request.progressListener?.let {
            ProgressListenerDelegate(coroutineScope, it)
        }
        var lastUpdateProgressBytesCopied = 0L
        while (bytes >= 0 && coroutineScope.isActive) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            if (progressListenerDelegate != null && contentLength > 0) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastNotifyTime) > 300) {
                    lastNotifyTime = currentTime
                    val currentBytesCopied = bytesCopied
                    lastUpdateProgressBytesCopied = currentBytesCopied
                    progressListenerDelegate.onUpdateProgress(
                        request, contentLength, currentBytesCopied
                    )
                }
            }
            bytes = inputStream.read(buffer)
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

    private class HttpDiskCacheHelper(
        val diskCache: DiskCache,
        val encodedDiskCacheKey: String,
        val diskCachePolicy: CachePolicy,
        val request: DownloadRequest,
    ) {

        val lock: Mutex by lazy {
            diskCache.getOrCreateEditMutexLock(encodedDiskCacheKey)
        }

        fun read(): FetchResult? {
            if (diskCachePolicy.readEnabled) {
                val diskCacheEntry = diskCache[encodedDiskCacheKey]
                if (diskCacheEntry != null) {
                    return FetchResult(DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE))
                } else if (request.depth >= RequestDepth.LOCAL) {
                    throw RequestDepthException(request, request.depth, request.depthFrom)
                }
            }
            return null
        }

        fun newEditor(): DiskCache.Editor? =
            if (diskCachePolicy.writeEnabled) {
                diskCache.edit(encodedDiskCacheKey)
            } else {
                null
            }

        @Throws(IOException::class)
        fun write(
            response: HttpStack.Response,
            diskCacheEditor: DiskCache.Editor,
            fetcher: HttpUriFetcher,
            coroutineScope: CoroutineScope
        ): FetchResult = try {
            val readLength = response.content.use { input ->
                diskCacheEditor.newOutputStream().use { out ->
                    fetcher.copyToWithActive(
                        inputStream = input,
                        out = out,
                        coroutineScope = coroutineScope,
                        contentLength = response.contentLength
                    )
                }
            }
            if (!response.isContentChunked && readLength == response.contentLength) {
                diskCacheEditor.commit()
            } else {
                diskCacheEditor.abort()
            }

            if (coroutineScope.isActive) {
                val diskCacheEntry = diskCache[encodedDiskCacheKey]
                    ?: throw IOException("Disk cache loss after write. key: ${request.diskCacheKey}")
                if (diskCachePolicy.readEnabled) {
                    FetchResult(DiskCacheDataSource(diskCacheEntry, DataFrom.NETWORK))
                } else {
                    diskCacheEntry.newInputStream()
                        .use { it.readBytes() }
                        .run { FetchResult(ByteArrayDataSource(this, DataFrom.NETWORK)) }
                }
            } else {
                throw CancellationException()
            }
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }

        companion object {
            fun from(sketch: Sketch, request: DownloadRequest): HttpDiskCacheHelper? =
                if (request.diskCachePolicy.isReadOrWrite) {
                    val diskCache = sketch.diskCache
                    val encodedDiskCacheKey = diskCache.encodeKey(request.diskCacheKey)
                    val diskCachePolicy = request.diskCachePolicy
                    HttpDiskCacheHelper(
                        diskCache,
                        encodedDiskCacheKey,
                        diskCachePolicy,
                        request
                    )
                } else {
                    null
                }
        }
    }
}