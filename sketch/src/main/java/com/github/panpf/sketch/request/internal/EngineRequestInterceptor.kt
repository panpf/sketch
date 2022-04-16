package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.newBitmapMemoryCacheHelper
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.drawable.internal.toResizeDrawable
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
        val sketch = chain.sketch
        val request = chain.request
        val target = request.target
        return when (request) {
            is DisplayRequest -> {
                newBitmapMemoryCacheHelper(sketch, request)?.read()?.let { result ->
                    val drawable = result.drawable
                    if (drawable is SketchCountBitmapDrawable) {
                        val key = request.key
                        chain.requestExtras.putCountDrawablePendingManagerKey(key)
                        sketch.countDrawablePendingManager.mark(
                            "EngineRequestInterceptor",
                            key,
                            drawable
                        )
                    }
                    return result.toDisplayData()
                }

                val requestDepth = request.depth
                if (requestDepth >= MEMORY) {
                    throw RequestDepthException(request, requestDepth, request.depthFrom)
                }

                if (target is DisplayTarget) {
                    val placeholderDrawable = request.placeholderImage
                        ?.getDrawable(sketch, request, null)
                        ?.let {
                            if (request.resizeApplyToResultDrawable == true) {
                                it.toResizeDrawable(request.resize)
                            } else {
                                it
                            }
                        }
                    target.onStart(placeholderDrawable)
                }

                withContext(sketch.decodeTaskDispatcher) {
                    DrawableDecodeInterceptorChain(
                        interceptors = sketch.drawableDecodeInterceptors,
                        index = 0,
                        sketch = sketch,
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
                withContext(chain.sketch.decodeTaskDispatcher) {
                    BitmapDecodeInterceptorChain(
                        interceptors = sketch.bitmapDecodeInterceptors,
                        index = 0,
                        sketch = sketch,
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
                withContext(chain.sketch.decodeTaskDispatcher) {
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
            }
            else -> {
                throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
            }
        }
    }

    override fun toString(): String = "EngineRequestInterceptor"
}