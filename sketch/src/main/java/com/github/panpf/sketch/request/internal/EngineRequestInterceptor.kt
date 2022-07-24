package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.toDisplayData
import com.github.panpf.sketch.request.toLoadData
import kotlinx.coroutines.withContext

class EngineRequestInterceptor : RequestInterceptor {

    @WorkerThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): ImageData =
        when (val request = chain.request) {
            is DisplayRequest -> display(chain.sketch, request, chain.requestContext)
            is LoadRequest -> load(chain.sketch, request, chain)
            is DownloadRequest -> download(chain.sketch, request)
            else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
        }

    @WorkerThread
    private suspend fun display(
        sketch: Sketch,
        request: DisplayRequest,
        requestContext: RequestContext,
    ): DisplayData =
        withContext(sketch.decodeTaskDispatcher) {
            DrawableDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = requestContext,
                fetchResult = null,
                interceptors = sketch.components.drawableDecodeInterceptorList,
                index = 0,
            ).proceed().toDisplayData()
        }

    @WorkerThread
    private suspend fun load(
        sketch: Sketch,
        request: LoadRequest,
        chain: RequestInterceptor.Chain
    ): LoadData =
        withContext(sketch.decodeTaskDispatcher) {
            BitmapDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = chain.requestContext,
                fetchResult = null,
                interceptors = sketch.components.bitmapDecodeInterceptorList,
                index = 0,
            ).proceed().toLoadData()
        }

    @WorkerThread
    private suspend fun download(sketch: Sketch, request: DownloadRequest): DownloadData {
        val fetcher = sketch.components.newFetcher(request)
        if (fetcher !is HttpUriFetcher) {
            throw IllegalArgumentException("DownloadRequest only support HTTP and HTTPS uri: ${request.uriString}")
        }

        val fetchResult = fetcher.fetch()
        val dataFrom = fetchResult.dataFrom
        return when (val source = fetchResult.dataSource) {
            is ByteArrayDataSource -> DownloadData(source.data, dataFrom)
            is DiskCacheDataSource -> DownloadData(source.snapshot, dataFrom)
            else -> throw UnsupportedOperationException("Unsupported DataSource for DownloadRequest: ${source::class.qualifiedName}")
        }
    }

    override fun toString(): String = "EngineRequestInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}