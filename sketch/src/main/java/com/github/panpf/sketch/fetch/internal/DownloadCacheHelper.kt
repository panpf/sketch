package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.DiskCache
import com.github.panpf.sketch.datasource.DataFrom.DISK_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.http.HttpStack.Response
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import java.io.IOException

class DownloadCacheKeys(uriString: String) {
    val dataDiskCacheKey = uriString
    val contentTypeDiskCacheKey = uriString + "_contentType"

    constructor(request: ImageRequest) : this(request.uriString)
}

class DownloadCacheHelper(val sketch: Sketch, val request: ImageRequest) {

    private val cacheKeys = DownloadCacheKeys(request)
    private val diskCache = sketch.diskCache

    val lock: Mutex by lazy {
        diskCache.editLock(cacheKeys.dataDiskCacheKey)
    }

    fun read(): FetchResult? {
        val dataDiskCacheSnapshot = diskCache[cacheKeys.dataDiskCacheKey]
        if (dataDiskCacheSnapshot != null) {
            val contentType =
                diskCache[cacheKeys.contentTypeDiskCacheKey]?.let { contentTypeSnapshot ->
                    try {
                        contentTypeSnapshot.newInputStream()
                            .use { it.bufferedReader().readText() }
                            .takeIf { it.isNotEmpty() && it.isNotBlank() }
                            ?: throw IOException("contentType disk cache text empty")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        contentTypeSnapshot.remove()
                        null
                    }
                }
            val mimeType = getMimeType(request.uriString, contentType)
            return FetchResult(
                DiskCacheDataSource(sketch, request, DISK_CACHE, dataDiskCacheSnapshot), mimeType
            )
        }

        return null
    }

    fun newEditor(): DiskCache.Editor? = diskCache.edit(cacheKeys.dataDiskCacheKey)

    @Throws(IOException::class)
    fun write(
        response: Response,
        diskCacheEditor: DiskCache.Editor,
        coroutineScope: CoroutineScope
    ): DiskCache.Snapshot {
        try {
            val contentLength = response.contentLength
            val readLength = response.content.use { inputStream ->
                diskCacheEditor.newOutputStream().use { outputStream ->
                    copyToWithActive(
                        request = request,
                        inputStream = inputStream,
                        outputStream = outputStream,
                        coroutineScope = coroutineScope,
                        contentLength = contentLength
                    )
                }
            }

            if (readLength == contentLength) {
                diskCacheEditor.commit()
            } else {
                diskCacheEditor.abort()
                throw IOException("readLength error. readLength=$readLength, contentLength=$contentLength. ${request.uriString}")
            }

            // save contentType
            val contentType = response.contentType?.takeIf { it.isNotEmpty() && it.isNotBlank() }
            if (contentType != null) {
                val contentTypeEditor = diskCache.edit(cacheKeys.contentTypeDiskCacheKey)
                if (contentTypeEditor != null) {
                    try {
                        contentTypeEditor.newOutputStream().bufferedWriter()
                            .use { it.write(contentType) }
                        contentTypeEditor.commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        contentTypeEditor.abort()
                    }
                }
            }

            return diskCache[cacheKeys.dataDiskCacheKey]
                ?: throw IOException("Disk cache loss after write. ${request.uriString}")
        } catch (e: IOException) {
            diskCacheEditor.abort()
            throw e
        }
    }
}