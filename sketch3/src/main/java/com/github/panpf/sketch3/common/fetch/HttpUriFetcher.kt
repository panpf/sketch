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

class HttpUriFetcher(
    private val sketch3: Sketch3,
    private val request: ImageRequest
) : Fetcher {

    override suspend fun fetch(): FetchResult? = withContext(Dispatchers.IO) {
        val diskCache = sketch3.diskCache
        val repeatTaskFilter = sketch3.repeatTaskFilter
        val httpStack = sketch3.httpStack
        val downloadRequest = request as DownloadRequest
        val encodedDiskCacheKey = diskCache.encodeKey(downloadRequest.diskCacheKey)
        val diskCachePolicy = downloadRequest.diskCachePolicy
        val repeatTaskFilterKey = request.uri.toString()

        // Avoid repeated downloads whenever disk cache is required
        if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            val httpFetchTaskDeferred =
                repeatTaskFilter.getHttpFetchTaskDeferred(repeatTaskFilterKey)
            httpFetchTaskDeferred?.await()
        }

        // Disk cache is preferred
        if (diskCachePolicy.readEnabled) {
            val diskCacheEntry = diskCache[encodedDiskCacheKey]
            if (diskCacheEntry != null) {
                return@withContext FetchResult(
                    DiskCacheDataSource(diskCacheEntry, DataFrom.DISK_CACHE),
                    DataFrom.DISK_CACHE
                )
            }
        }

        // Create a download task Deferred and cache it for other tasks to filter repeated downloads
        val downloadTaskDeferred: Deferred<FetchResult?> =
            async(Dispatchers.IO, start = CoroutineStart.LAZY) {
                val response = httpStack.getResponse(request.uri.toString())
                val diskCacheEditor = if (diskCachePolicy.writeEnabled) {
                    diskCache.edit(encodedDiskCacheKey)
                } else {
                    null
                }
                if (diskCacheEditor != null) {
                    val diskCacheEntry =
                        writeToDiskCache(response, diskCacheEditor, diskCache, encodedDiskCacheKey)
                    if (diskCacheEntry != null) {
                        FetchResult(
                            DiskCacheDataSource(diskCacheEntry, DataFrom.NETWORK),
                            DataFrom.NETWORK
                        )
                    } else {
                        null
                    }
                } else {
                    val byteArray = writeToByteArray(response)
                    if (byteArray != null) {
                        FetchResult(
                            ByteArrayDataSource(byteArray, DataFrom.NETWORK),
                            DataFrom.NETWORK
                        )
                    } else {
                        null
                    }
                }
            }
        repeatTaskFilter.putHttpFetchTaskDeferred(repeatTaskFilterKey, downloadTaskDeferred)
        val result = downloadTaskDeferred.await()
        repeatTaskFilter.removeHttpFetchTaskDeferred(repeatTaskFilterKey)
        result
    }

    private fun writeToDiskCache(
        response: HttpStack.Response,
        diskCacheEditor: DiskCache.Editor,
        diskCache: DiskCache,
        diskCacheKey: String
    ): DiskCache.Entry? = try {
        response.content.use { input ->
            diskCacheEditor.newOutputStream().use { out ->
                input.copyTo(out)
            }
        }
        diskCacheEditor.commit()
        diskCache[diskCacheKey]
    } catch (e: IOException) {
        e.printStackTrace()
        diskCacheEditor.abort()
        null
    }

    private fun writeToByteArray(response: HttpStack.Response): ByteArray? = try {
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.use { out ->
            response.content.use { input ->
                input.copyTo(out)
            }
        }
        byteArrayOutputStream.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch3: Sketch3, request: ImageRequest): Fetcher? =
            if (isApplicable(request.uri)) HttpUriFetcher(sketch3, request) else null

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}