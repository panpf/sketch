package com.github.panpf.sketch.fetch

import android.webkit.MimeTypeMap
import androidx.annotation.VisibleForTesting
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.CachePolicy.ENABLED
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
import com.github.panpf.sketch.util.getMimeTypeFromUrl
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

    companion object {
        const val SCHEME = "http"
        const val SCHEME1 = "https"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

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
            val contentType = response.contentType
            val mimeType = getMimeType(request.uriString, contentType)
            val bytes = byteArrayOutputStream.toByteArray()
            FetchResult(ByteArrayDataSource(sketch, request, DataFrom.NETWORK, bytes), mimeType)
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
                && (SCHEME.equals(request.uri.scheme, ignoreCase = true)
                        || SCHEME1.equals(request.uri.scheme, ignoreCase = true))
            ) {
                HttpUriFetcher(sketch, request, request.uriString)
            } else {
                null
            }
    }

    private class HttpDiskCacheHelper(
        val sketch: Sketch,
        val request: DownloadRequest,
        val diskCache: DiskCache,
        val encodedDataDiskCacheKey: String,
        val encodedContentTypeDiskCacheKey: String,
        val diskCachePolicy: CachePolicy,
    ) {

        val lock: Mutex by lazy {
            diskCache.getOrCreateEditMutexLock(encodedDataDiskCacheKey)
        }

        fun read(): FetchResult? {
            if (!diskCachePolicy.readEnabled) return null

            val dataDiskCacheEntry = diskCache[encodedDataDiskCacheKey]
            if (dataDiskCacheEntry != null) {
                val contentTypeDiskCacheEntry = diskCache[encodedContentTypeDiskCacheKey]
                val contentType = if (contentTypeDiskCacheEntry != null) {
                    // todo 貌似 contentType 是始终 null
                    try {
                        contentTypeDiskCacheEntry.newInputStream()
                            .use {
                                it.bufferedReader().readText()
                            }.takeIf {
                                it.isNotEmpty() && it.isNotBlank()
                            } ?: throw IOException("contentType disk cache text empty")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        contentTypeDiskCacheEntry.delete()
                        null
                    }
                } else {
                    null
                }
                val mimeType = getMimeType(request.uriString, contentType)
                return FetchResult(
                    DiskCacheDataSource(sketch, request, DataFrom.DISK_CACHE, dataDiskCacheEntry),
                    mimeType
                )
            }

            val requestDepth = request.depth
            if (requestDepth != null && requestDepth >= RequestDepth.LOCAL) {
                throw RequestDepthException(request, requestDepth, request.depthFrom)
            } else {
                return null
            }
        }

        fun newEditor(): DiskCache.Editor? =
            if (diskCachePolicy.writeEnabled) {
                diskCache.edit(encodedDataDiskCacheKey)
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
            val contentType = response.contentType
            if (!response.isContentChunked && readLength == response.contentLength) {
                diskCacheEditor.commit()
                if (contentType?.isNotEmpty() == true && contentType.isNotBlank()) {
                    val contentTypeEditor = diskCache.edit(encodedContentTypeDiskCacheKey)
                    contentTypeEditor?.newOutputStream()?.use {
                        it.bufferedWriter().write(contentType)
                    }
                }
            } else {
                diskCacheEditor.abort()
            }

            if (coroutineScope.isActive) {
                val mimeType = getMimeType(request.uriString, contentType)
                val diskCacheEntry = diskCache[encodedDataDiskCacheKey]
                    ?: throw IOException("Disk cache loss after write. key: ${request.networkContentDiskCacheKey}")
                if (diskCachePolicy.readEnabled) {
                    FetchResult(
                        DiskCacheDataSource(sketch, request, DataFrom.NETWORK, diskCacheEntry),
                        mimeType
                    )
                } else {
                    diskCacheEntry.newInputStream()
                        .use { it.readBytes() }
                        .run {
                            FetchResult(
                                ByteArrayDataSource(sketch, request, DataFrom.NETWORK, this),
                                mimeType
                            )
                        }
                }
            } else {
                throw CancellationException()
            }
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }

        companion object {
            fun from(sketch: Sketch, request: DownloadRequest): HttpDiskCacheHelper? {
                val cachePolicy = request.networkContentDiskCachePolicy ?: ENABLED
                return if (cachePolicy.isReadOrWrite) {
                    val diskCache = sketch.diskCache
                    val encodedDataDiskCacheKey =
                        diskCache.encodeKey(request.networkContentDiskCacheKey)
                    val encodedContentTypeDiskCacheKey =
                        diskCache.encodeKey(request.networkContentDiskCacheKey + "_contentType")
                    HttpDiskCacheHelper(
                        sketch = sketch,
                        request = request,
                        diskCache = diskCache,
                        encodedDataDiskCacheKey = encodedDataDiskCacheKey,
                        encodedContentTypeDiskCacheKey = encodedContentTypeDiskCacheKey,
                        diskCachePolicy = cachePolicy,
                    )
                } else {
                    null
                }
            }

        }
    }
}

/**
 * Parse the response's `content-type` header.
 *
 * "text/plain" is often used as a default/fallback MIME type.
 * Attempt to guess a better MIME type from the file extension.
 */
@VisibleForTesting
internal fun getMimeType(url: String, contentType: String?): String? {
    if (contentType == null || contentType.startsWith(HttpUriFetcher.MIME_TYPE_TEXT_PLAIN)) {
        MimeTypeMap.getSingleton().getMimeTypeFromUrl(url)?.let { return it }
    }
    return contentType?.substringBefore(';')
}