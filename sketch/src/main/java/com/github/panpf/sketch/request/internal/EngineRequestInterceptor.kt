package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.safeAccessMemoryCache
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.Depth.MEMORY
import com.github.panpf.sketch.request.DepthException
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
import kotlinx.coroutines.Dispatchers
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
    ): DisplayData {
        /* check memory cache */
        val cachedCountBitmap = safeAccessMemoryCache(sketch, request) { it?.read() }
        if (cachedCountBitmap != null) {
            val countDrawable = SketchCountBitmapDrawable(
                request.context.resources, cachedCountBitmap, MEMORY_CACHE
            ).apply {
                withContext(Dispatchers.Main) {
                    requestContext.pendingCountDrawable(this@apply, "displayBefore")
                }
            }
            val result = DrawableDecodeResult(
                drawable = countDrawable,
                imageInfo = cachedCountBitmap.imageInfo,
                imageExifOrientation = cachedCountBitmap.imageExifOrientation,
                dataFrom = MEMORY_CACHE,
                transformedList = null,
            )
            return result.toDisplayData()
        }
        val depth = request.depth
        if (depth >= MEMORY) {
            throw DepthException("Request depth limited to $depth. ${request.uriString}")
        }

        /* load */
        return withContext(sketch.decodeTaskDispatcher) {
            DrawableDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = requestContext,
                fetchResult = null,
                interceptors = sketch.components.drawableDecodeInterceptorList,
                index = 0,
            ).proceed().toDisplayData()
        }
    }

    @WorkerThread
    private suspend fun load(
        sketch: Sketch,
        request: LoadRequest,
        chain: RequestInterceptor.Chain
    ): LoadData {
        /* load */
        return withContext(sketch.decodeTaskDispatcher) {
            BitmapDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = chain.requestContext,
                fetchResult = null,
                interceptors = sketch.components.bitmapDecodeInterceptorList,
                index = 0,
            ).proceed().toLoadData()
        }
    }

    @WorkerThread
    private suspend fun download(sketch: Sketch, request: DownloadRequest): DownloadData {
        /* download */
        return withContext(sketch.decodeTaskDispatcher) {
            val fetcher = sketch.components.newFetcher(request)
            if (fetcher !is HttpUriFetcher) {
                throw IllegalArgumentException("Download only support HTTP and HTTPS uri: ${request.uriString}")
            }

            val fetchResult = fetcher.fetch()
            when (val source = fetchResult.dataSource) {
                is ByteArrayDataSource -> DownloadData.Bytes(source.data, fetchResult.dataFrom)
                is DiskCacheDataSource -> DownloadData.Cache(
                    source.diskCacheSnapshot,
                    fetchResult.dataFrom
                )
                else -> throw IllegalArgumentException("The unknown source: ${source::class.qualifiedName}")
            }
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