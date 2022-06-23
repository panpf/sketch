package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.isReadOrWrite
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.internal.DownloadCacheHelper
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.fetch.internal.writeToByteArray
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.ifOrNull
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpg' uri
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

    override suspend fun fetch(): FetchResult {
        requiredWorkThread()

        val downloadCachePolicy = request.downloadCachePolicy
        val diskCacheHelper = ifOrNull(downloadCachePolicy.isReadOrWrite) {
            DownloadCacheHelper(sketch = sketch, request = request)
        }

        diskCacheHelper?.lock?.lock()
        try {
            /* read cache */
            val resultFromCache = ifOrNull(downloadCachePolicy.readEnabled) {
                diskCacheHelper?.read()
            }
            if (resultFromCache != null) {
                return resultFromCache
            }

            /* verify depth */
            val depth = request.depth
            if (depth >= Depth.LOCAL) {
                throw DepthException(depth)
            }

            /* execute download */
            return withContext(sketch.networkTaskDispatcher) {
                // open connection
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
                val diskCacheEditor = ifOrNull(downloadCachePolicy.writeEnabled) {
                    diskCacheHelper?.newEditor()
                }
                val dataSource = if (diskCacheHelper != null && diskCacheEditor != null) {
                    val diskCacheSnapshot = diskCacheHelper.write(response, diskCacheEditor, this)
                    if (downloadCachePolicy.readEnabled) {
                        DiskCacheDataSource(sketch, request, NETWORK, diskCacheSnapshot)
                    } else {
                        diskCacheSnapshot.newInputStream()
                            .use { it.readBytes() }
                            .run { ByteArrayDataSource(sketch, request, NETWORK, this) }
                    }
                } else {
                    val byteArray = writeToByteArray(request, response, this)
                    ByteArrayDataSource(sketch, request, NETWORK, byteArray)
                }
                val mimeType = getMimeType(request.uriString, response.contentType)
                FetchResult(dataSource, mimeType)
            }
        } finally {
            diskCacheHelper?.lock?.unlock()
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