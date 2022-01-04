package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ByteArrayDownloadResult
import com.github.panpf.sketch.request.DiskCacheDownloadResult
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
    ): LoadResult {
        val request = chain.request
        val componentRegistry = sketch.componentRegistry
        val fetcher =
            componentRegistry.newFetcher(sketch, request)
        val source = if (fetcher is HttpUriFetcher) {
            val downloadRequest = request.newDownloadRequest()
            val downloadResult = DownloadInterceptorChain(
                initialRequest = downloadRequest,
                interceptors = sketch.downloadInterceptors,
                index = 0,
                request = downloadRequest,
            ).proceed(sketch, downloadRequest)
            when (downloadResult) {
                is ByteArrayDownloadResult -> ByteArrayDataSource(
                    downloadResult.data,
                    downloadResult.from
                )
                is DiskCacheDownloadResult -> DiskCacheDataSource(
                    downloadResult.diskCacheEntry,
                    downloadResult.from
                )
            }
        } else {
            withContext(sketch.decodeTaskDispatcher) {
                fetcher.fetch()
            }.source
        }
        return withContext(sketch.decodeTaskDispatcher) {
            val decoder = componentRegistry.newDecoder(sketch, request, source)
            val decodeResult = decoder.decode()
            LoadResult(decodeResult.bitmap, decodeResult.info, source.from)
        }
    }
}