package com.github.panpf.sketch.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch.common.fetch.HttpUriFetcher
import com.github.panpf.sketch.download.ByteArrayDownloadResult
import com.github.panpf.sketch.download.DiskCacheDownloadResult
import com.github.panpf.sketch.download.DownloadResult
import com.github.panpf.sketch.download.DownloadRequest

class DownloadEngineInterceptor : Interceptor<DownloadRequest, DownloadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DownloadRequest, DownloadResult>,
        extras: RequestExtras<DownloadRequest, DownloadResult>?
    ): DownloadResult {
        val request = chain.request

        val fetcher = sketch.componentRegistry.newFetcher(
            sketch, request, extras as RequestExtras<ImageRequest, ImageResult>?
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