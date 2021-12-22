package com.github.panpf.sketch3.common.fetch

import android.net.Uri
import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.DataFrom
import com.github.panpf.sketch3.common.ImageRequest
import com.github.panpf.sketch3.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch3.download.DownloadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class HttpUriFetcher(
    private val sketch3: Sketch3,
    private val request: ImageRequest
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val diskCache = sketch3.diskCache
        val downloadRequest = request as DownloadRequest
        val realDiskCacheKey = diskCache.keyEncode(downloadRequest.diskCacheKey)
        val diskCachePolicy = downloadRequest.diskCachePolicy

//        launch
        coroutineScope {
            async {

            }
        }

        val diskCacheLock = if (diskCachePolicy.readEnabled || diskCachePolicy.writeEnabled) {
            diskCache.getEditLock(realDiskCacheKey)
        } else {
            null
        }
        diskCacheLock?.lock()
        try {
            if (diskCachePolicy.readEnabled) {
                val diskCacheEntry = diskCache[realDiskCacheKey]
                if (diskCacheEntry != null) {
                    return SourceResult(DiskCacheDataSource(diskCacheEntry), DataFrom.DISK_CACHE)
                }
            }

            // todo 支持下载分享

            withContext(Dispatchers.IO) {
                val response = sketch3.httpStack.getResponse(request.uri.toString())
//                diskCache.getEditLock()
            }
        } finally {
            diskCacheLock?.unlock()
        }

        return null
    }

    class Factory : Fetcher.Factory {
        override fun create(sketch3: Sketch3, request: ImageRequest): Fetcher? =
            if (isApplicable(request.uri)) HttpUriFetcher(sketch3, request) else null

        private fun isApplicable(data: Uri): Boolean =
            data.scheme == "http" || data.scheme == "https"
    }
}