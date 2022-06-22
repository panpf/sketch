package com.github.panpf.sketch.fetch

import androidx.annotation.VisibleForTesting
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.http.HttpStack
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ProgressListenerDelegate
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
    val request: ImageRequest,
    val url: String
) : Fetcher {

    companion object {
        const val SCHEME = "http"
        const val SCHEME1 = "https"
        const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun fetch(): FetchResult {
        val diskCacheHelper = DownloadCacheHelper.from(sketch, request)
        diskCacheHelper?.lock?.lock()
        try {
            val result = diskCacheHelper?.read()
            if (result != null) {
                return result
            }

            val depth = request.depth
            if (depth >= Depth.LOCAL) {
                throw DepthException(depth)
            }

            return execute(diskCacheHelper)
        } finally {
            diskCacheHelper?.lock?.unlock()
        }
    }

    @Throws(IOException::class)
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun execute(diskCacheHelper: DownloadCacheHelper?): FetchResult =
        withContext(sketch.networkTaskDispatcher) {
            val response = sketch.httpStack.getResponse(request, url)
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

    private class DownloadCacheHelper(
        val sketch: Sketch,
        val request: ImageRequest,
        val diskCache: DiskCache,
        val dataDiskCacheKey: String,
        val contentTypeDiskCacheKey: String,
        val diskCachePolicy: CachePolicy,
    ) {

        val lock: Mutex by lazy {
            diskCache.editLock(dataDiskCacheKey)
        }

        fun read(): FetchResult? {
            if (!diskCachePolicy.readEnabled) return null

            val dataDiskCacheSnapshot = diskCache[dataDiskCacheKey]
            if (dataDiskCacheSnapshot != null) {
                val contentTypeDiskCacheSnapshot = diskCache[contentTypeDiskCacheKey]
                val contentType = if (contentTypeDiskCacheSnapshot != null) {
                    try {
                        contentTypeDiskCacheSnapshot.newInputStream()
                            .use {
                                it.bufferedReader().readText()
                            }.takeIf {
                                it.isNotEmpty() && it.isNotBlank()
                            } ?: throw IOException("contentType disk cache text empty")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        contentTypeDiskCacheSnapshot.remove()
                        null
                    }
                } else {
                    null
                }
                val mimeType = getMimeType(request.uriString, contentType)
                return FetchResult(
                    DiskCacheDataSource(
                        sketch,
                        request,
                        DataFrom.DISK_CACHE,
                        dataDiskCacheSnapshot
                    ),
                    mimeType
                )
            }

            return null
        }

        fun newEditor(): DiskCache.Editor? =
            if (diskCachePolicy.writeEnabled) {
                diskCache.edit(dataDiskCacheKey)
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
            val isContentChunked: Boolean by lazy {
                var transferEncodingValue = response.getHeaderField("Transfer-Encoding")
                if (transferEncodingValue != null) {
                    transferEncodingValue = transferEncodingValue.trim { it <= ' ' }
                }
                "chunked".equals(transferEncodingValue, ignoreCase = true)
            }
            if (!isContentChunked && readLength == response.contentLength) {
                diskCacheEditor.commit()
                if (contentType?.isNotEmpty() == true && contentType.isNotBlank()) {
                    val contentTypeEditor = diskCache.edit(contentTypeDiskCacheKey)
                    contentTypeEditor?.newOutputStream()?.bufferedWriter()?.use {
                        it.write(contentType)
                    }
                }
            } else {
                diskCacheEditor.abort()
            }

            if (coroutineScope.isActive) {
                val mimeType = getMimeType(request.uriString, contentType)
                val diskCacheSnapshot = diskCache[dataDiskCacheKey]
                    ?: throw IOException("Disk cache loss after write. key: ${request.uriString}")
                if (diskCachePolicy.readEnabled) {
                    FetchResult(
                        DiskCacheDataSource(sketch, request, DataFrom.NETWORK, diskCacheSnapshot),
                        mimeType
                    )
                } else {
                    diskCacheSnapshot.newInputStream()
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
            fun from(sketch: Sketch, request: ImageRequest): DownloadCacheHelper? {
                val cachePolicy = request.downloadCachePolicy
                return if (cachePolicy.isReadOrWrite) {
                    val diskCache = sketch.diskCache
                    val dataDiskCacheKey = request.uriString
                    val contentTypeDiskCacheKey = request.uriString + "_contentType"
                    DownloadCacheHelper(
                        sketch = sketch,
                        request = request,
                        diskCache = diskCache,
                        dataDiskCacheKey = dataDiskCacheKey,
                        contentTypeDiskCacheKey = contentTypeDiskCacheKey,
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
        getMimeTypeFromUrl(url)?.let { return it }
    }
    return contentType?.substringBefore(';')
}