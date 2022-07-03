package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.NETWORK
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.internal.copyToWithActive
import com.github.panpf.sketch.fetch.internal.getMimeType
import com.github.panpf.sketch.fetch.internal.safeAccessDownloadCache
import com.github.panpf.sketch.request.Depth
import com.github.panpf.sketch.request.DepthException
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.requiredWorkThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
        return safeAccessDownloadCache(sketch, request) { diskCacheHelper ->
            /* read cache */
            val resultFromCache = diskCacheHelper?.read()
            if (resultFromCache != null) {
                return@safeAccessDownloadCache resultFromCache
            }

            /* verify depth */
            val depth = request.depth
            if (depth >= Depth.LOCAL) {
                throw DepthException("Request depth limited to $depth. ${request.uriString}")
            }

            /* execute download */
            withContext(sketch.networkTaskDispatcher) {
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
                val diskCacheSnapshot = diskCacheHelper?.write(response, this)
                val dataSource = if (diskCacheSnapshot != null) {
                    if (request.downloadCachePolicy.readEnabled) {
                        DiskCacheDataSource(sketch, request, NETWORK, diskCacheSnapshot)
                    } else {
                        diskCacheSnapshot.newInputStream()
                            .use { it.readBytes() }
                            .run { ByteArrayDataSource(sketch, request, NETWORK, this) }
                    }
                } else {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    byteArrayOutputStream.use { out ->
                        response.content.use { input ->
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