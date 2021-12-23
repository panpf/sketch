package com.github.panpf.sketch3.common.fetch

import android.net.Uri
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.ImageRequest
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
    private val request: ImageRequest
) : Fetcher {

    // To avoid the possibility of repeated downloads or repeated edits to the disk cache due to multithreaded concurrency,
    // these operations need to be performed in a single thread 'singleThreadTaskDispatcher'
    override suspend fun fetch(): FetchResult? = withContext(sketch3.singleThreadTaskDispatcher) {
        val diskCache = sketch3.diskCache
        val repeatTaskFilter = sketch3.repeatTaskFilter
        val httpStack = sketch3.httpStack
        val downloadRequest = request as DownloadRequest
        val encodedDiskCacheKey = diskCache.encodeKey(downloadRequest.diskCacheKey)
        val diskCachePolicy = downloadRequest.diskCachePolicy
        val repeatTaskFilterKey = request.uri.toString()

        // Avoid repeated downloads whenever disk cache is required
        if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            repeatTaskFilter.getHttpFetchTaskDeferred(repeatTaskFilterKey)?.await()
            diskCache.getEdiTaskDeferred(encodedDiskCacheKey)?.await()
        }
        if (diskCachePolicy.readEnabled) {
            val diskCacheEntry = diskCache[encodedDiskCacheKey]
            if (diskCacheEntry != null) {
                return@withContext FetchResult(
                    DataFrom.DISK_CACHE,
                    DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE),
                )
            }
        }

        // Create a download task Deferred and cache it for other tasks to filter repeated downloads
        @Suppress("BlockingMethodInNonBlockingContext")
        val downloadTaskDeferred: Deferred<FetchResult?> =
            async(sketch3.httpDownloadTaskDispatcher, start = CoroutineStart.LAZY) {
                val response = httpStack.getResponse(request.uri.toString())
                val responseCode = response.code
                if (responseCode == 200) {
                    val diskCacheEditor = if (diskCachePolicy.writeEnabled) {
                        diskCache.edit(encodedDiskCacheKey)
                    } else {
                        null
                    }
                    if (diskCacheEditor != null) {
                        val diskCacheEntry =
                            writeToDiskCache(
                                response, diskCacheEditor, diskCache, encodedDiskCacheKey, this
                            )
                        if (diskCacheEntry != null) {
                            if (diskCachePolicy.readEnabled) {
                                FetchResult(
                                    DataFrom.NETWORK,
                                    DiskCacheDataSource(diskCacheEntry, DataFrom.NETWORK),
                                )
                            } else {
                                val byteArray = diskCacheEntry.newInputStream().use {
                                    it.readBytes()
                                }
                                FetchResult(
                                    DataFrom.NETWORK,
                                    ByteArrayDataSource(byteArray, DataFrom.NETWORK),
                                )
                            }
                        } else {
                            null
                        }
                    } else {
                        val byteArray = writeToByteArray(response, this)
                        if (byteArray != null) {
                            FetchResult(
                                DataFrom.NETWORK,
                                ByteArrayDataSource(byteArray, DataFrom.NETWORK),
                            )
                        } else {
                            null
                        }
                    }
                } else {
                    throw IllegalStateException("HTTP code error. code=$responseCode, message=${response.message}. ${request.uri}")
                }
            }

        // 当前任务会写入缓存才需要让别人等
        if (diskCachePolicy.writeEnabled) {
            repeatTaskFilter.putHttpFetchTaskDeferred(repeatTaskFilterKey, downloadTaskDeferred)
            diskCache.putEdiTaskDeferred(encodedDiskCacheKey, downloadTaskDeferred)
        }
        val result = downloadTaskDeferred.await()
        if (diskCachePolicy.writeEnabled) {
            repeatTaskFilter.removeHttpFetchTaskDeferred(repeatTaskFilterKey)
            diskCache.removeEdiTaskDeferred(repeatTaskFilterKey)
        }
        return@withContext result
    }

    private fun writeToDiskCache(
        response: HttpStack.Response,
        diskCacheEditor: DiskCache.Editor,
        diskCache: DiskCache,
        diskCacheKey: String, coroutineScope: CoroutineScope
    ): DiskCache.Entry? = try {
        val readLength = response.content.use { input ->
            diskCacheEditor.newOutputStream().use { out ->
                input.copyToWithActive(out, coroutineScope = coroutineScope)
            }
        }
        if (coroutineScope.isActive) {
            diskCacheEditor.commit()
            diskCache[diskCacheKey]
        } else if (!response.isContentChunked && readLength == response.contentLength) {
            diskCacheEditor.commit()
            diskCache[diskCacheKey]
        } else {
            diskCacheEditor.abort()
            null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        diskCacheEditor.abort()
        null
    }

    private fun writeToByteArray(
        response: HttpStack.Response,
        coroutineScope: CoroutineScope
    ): ByteArray? = try {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.use { out ->
            response.content.use { input ->
                input.copyToWithActive(out, coroutineScope = coroutineScope)
            }
        }
        if (coroutineScope.isActive) {
            byteArrayOutputStream.toByteArray()
        } else {
            null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    private fun InputStream.copyToWithActive(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        coroutineScope: CoroutineScope,
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0 && coroutineScope.isActive) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = read(buffer)
        }
        return bytesCopied
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch3: Sketch3, request: ImageRequest): HttpUriFetcher? =
            if (isApplicable(request.uri)) HttpUriFetcher(sketch3, request) else null

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}