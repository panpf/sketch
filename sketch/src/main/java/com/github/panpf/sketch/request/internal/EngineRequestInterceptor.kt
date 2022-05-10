package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheHelper
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.internal.tryToResizeDrawable
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestDepth.MEMORY
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.toDisplayData
import com.github.panpf.sketch.request.toLoadData
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import kotlinx.coroutines.withContext

class EngineRequestInterceptor : RequestInterceptor {

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): ImageData {
        val request = chain.request
        val target = request.target
        return when (request) {
            is DisplayRequest -> {
                newBitmapMemoryCacheHelper(request)?.read()?.let { result ->
                    val drawable = result.drawable
                    if (drawable is SketchCountBitmapDrawable) {
                        val key = request.key
                        chain.requestExtras.putCountDrawablePendingManagerKey(key)
                        request.sketch.countDrawablePendingManager.mark(
                            "EngineRequestInterceptor",
                            key,
                            drawable
                        )
                    }
                    return result.toDisplayData()
                }

                if (target is DisplayTarget) {
                    val placeholderDrawable = request.placeholderImage
                        ?.getDrawable(request, null)
                        ?.tryToResizeDrawable(request)
                    target.onStart(placeholderDrawable)
                }

                val requestDepth = request.depth
                if (requestDepth >= MEMORY) {
                    throw RequestDepthException(request, requestDepth, request.depthFrom)
                }

                withContext(request.sketch.decodeTaskDispatcher) {
                    DrawableDecodeInterceptorChain(
                        interceptors = request.sketch.drawableDecodeInterceptors,
                        index = 0,
                        request = request,
                        requestExtras = chain.requestExtras,
                        fetchResult = null,
                    ).proceed().toDisplayData()
                }
            }
            is LoadRequest -> {
                if (target is LoadTarget) {
                    target.onStart()
                }
                withContext(request.sketch.decodeTaskDispatcher) {
                    BitmapDecodeInterceptorChain(
                        interceptors = request.sketch.bitmapDecodeInterceptors,
                        index = 0,
                        request = request,
                        requestExtras = chain.requestExtras,
                        fetchResult = null,
                    ).proceed().toLoadData()
                }
            }
            is DownloadRequest -> {
                if (target is DownloadTarget) {
                    target.onStart()
                }
                withContext(request.sketch.decodeTaskDispatcher) {
                    val fetcher = request.sketch.componentRegistry.newFetcher(request)
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
            }
            else -> {
                throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
            }
        }
    }

    override fun toString(): String = "EngineRequestInterceptor"
}