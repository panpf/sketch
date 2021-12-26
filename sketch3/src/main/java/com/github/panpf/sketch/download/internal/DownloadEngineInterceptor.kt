package com.github.panpf.sketch.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.ProgressListener
import com.github.panpf.sketch.common.datasource.ByteArrayDataSource
import com.github.panpf.sketch.common.datasource.DiskCacheDataSource
import com.github.panpf.sketch.common.fetch.HttpUriFetcher
import com.github.panpf.sketch.download.*

class DownloadEngineInterceptor : Interceptor<DownloadRequest, DownloadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<DownloadRequest, DownloadResult>,
        httpFetchProgressListener: ProgressListener<ImageRequest>?
    ): DownloadResult {
        val request = chain.request

        val fetcher =
            sketch.componentRegistry.newFetcher(sketch, request, httpFetchProgressListener)
        if (fetcher !is HttpUriFetcher) {
            return DownloadErrorResult(IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uri}"))
        }

        val fetchResult = fetcher.fetch()
            ?: return DownloadErrorResult(IllegalStateException("Unable fetch the result: ${request.uri}"))

        return when (val source = fetchResult.source) {
            is ByteArrayDataSource -> DownloadSuccessResult(
                ByteArrayDownloadData(source.data),
                fetchResult.from
            )
            is DiskCacheDataSource -> DownloadSuccessResult(
                DiskCacheDownloadData(source.diskCacheEntry),
                fetchResult.from
            )
            else -> DownloadErrorResult(IllegalArgumentException("The unknown source: ${source::class.qualifiedName}"))
        }
    }
}