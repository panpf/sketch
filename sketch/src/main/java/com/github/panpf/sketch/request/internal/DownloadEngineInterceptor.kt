package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestInterceptor

class DownloadEngineInterceptor : RequestInterceptor<DownloadRequest, DownloadData> {

    @WorkerThread
    override suspend fun intercept(chain: RequestInterceptor.Chain<DownloadRequest, DownloadData>): DownloadData {
        val sketch = chain.sketch
        val request = chain.request

        val fetcher = sketch.componentRegistry.newFetcher(sketch, request)
        if (fetcher !is HttpUriFetcher) {
            throw IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uriString}")
        }

        val fetchResult = fetcher.fetch()
        return when (val source = fetchResult.dataSource) {
            is ByteArrayDataSource -> DownloadData.Bytes(source.data, fetchResult.from)
            is DiskCacheDataSource -> DownloadData.Cache(source.diskCacheEntry, fetchResult.from)
            else -> throw IllegalArgumentException("The unknown source: ${source::class.qualifiedName}")
        }
    }
}