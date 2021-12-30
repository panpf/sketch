package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.ListenerInfo
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ByteArrayDownloadResult
import com.github.panpf.sketch.request.DiskCacheDownloadResult
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.DownloadResult

class DownloadEngineInterceptor : Interceptor<DownloadRequest, DownloadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DownloadRequest, DownloadResult>,
        listenerInfo: ListenerInfo<DownloadRequest, DownloadResult>?
    ): DownloadResult {
        val request = chain.request

        val fetcher = sketch.componentRegistry.newFetcher(
            sketch, request, listenerInfo as ListenerInfo<ImageRequest, ImageResult>?
        )
        if (fetcher !is HttpUriFetcher) {
            throw IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uri}")
        }

        val fetchResult = fetcher.fetch()
        return when (val source = fetchResult.source) {
            is ByteArrayDataSource -> ByteArrayDownloadResult(source.data, fetchResult.from)
            is DiskCacheDataSource -> DiskCacheDownloadResult(source.diskCacheEntry, fetchResult.from)
            else -> throw IllegalArgumentException("The unknown source: ${source::class.qualifiedName}")
        }
    }
}