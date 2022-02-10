package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadEngineInterceptor : RequestInterceptor<DownloadRequest, DownloadData> {

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<DownloadRequest, DownloadData>): DownloadData =
        withContext(Dispatchers.IO) {
            val sketch = chain.sketch
            val request = chain.request
            val fetcher = sketch.componentRegistry.newFetcher(sketch, request)
            if (fetcher !is HttpUriFetcher) {
                throw IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uriString}")
            }

            val fetchResult = fetcher.fetch()
            when (val source = fetchResult.dataSource) {
                is ByteArrayDataSource -> DownloadData.Bytes(source.data, fetchResult.from)
                is DiskCacheDataSource -> DownloadData.Cache(
                    source.diskCacheSnapshot,
                    fetchResult.from
                )
                else -> throw IllegalArgumentException("The unknown source: ${source::class.qualifiedName}")
            }
        }

    override fun toString(): String = "DownloadEngineInterceptor"
}