package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadData>,
    ): LoadData {
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
                is DownloadData.Bytes -> ByteArrayDataSource(
                    downloadResult.data,
                    downloadResult.from
                )
                is DownloadData.Cache -> DiskCacheDataSource(
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
            LoadData(decodeResult.bitmap, decodeResult.info, source.from)
        }
    }
}