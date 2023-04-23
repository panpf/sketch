/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DiskCacheDataSource
import com.github.panpf.sketch.decode.internal.BitmapDecodeInterceptorChain
import com.github.panpf.sketch.decode.internal.DrawableDecodeInterceptorChain
import com.github.panpf.sketch.drawable.internal.tryToResizeDrawable
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.stateimage.internal.toSketchStateDrawable
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.target.LoadTarget
import com.github.panpf.sketch.util.asOrNull
import com.github.panpf.sketch.util.withContextRunCatching

class EngineRequestInterceptor : RequestInterceptor {

    override val key: String? = null
    override val sortWeight: Int = 100

    @MainThread
    override suspend fun intercept(chain: RequestInterceptor.Chain): ImageData =
        when (val request = chain.request) {
            is DisplayRequest -> display(chain.sketch, request, chain.requestContext)
            is LoadRequest -> load(chain.sketch, request, chain)
            is DownloadRequest -> download(chain.sketch, request)
            else -> throw UnsupportedOperationException("Unsupported ImageRequest: ${request::class.java}")
        }

    @MainThread
    private suspend fun display(
        sketch: Sketch,
        request: DisplayRequest,
        requestContext: RequestContext,
    ): DisplayData {
        // Why does Target.start() have to be executed after the memory cache check?
        // First, when the memory cache is valid, one callback can be reduced
        // Secondly, when RecyclerView executes notifyDataSetChanged(),
        // it can avoid the flickering phenomenon caused by the fast switching of the picture between placeholder and result
        request.target?.asOrNull<DisplayTarget>()?.let {
            val placeholderDrawable = request.placeholder
                ?.getDrawable(sketch, request, null)
                ?.tryToResizeDrawable(requestContext.request, requestContext.resizeSize)
                ?.toSketchStateDrawable()
            it.onStart(placeholderDrawable)
        }
        return withContextRunCatching(sketch.decodeTaskDispatcher) {
            DrawableDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = requestContext,
                fetchResult = null,
                interceptors = sketch.components.getDrawableDecodeInterceptorList(request),
                index = 0,
            ).proceed().let {
                DisplayData(
                    drawable = it.drawable,
                    imageInfo = it.imageInfo,
                    dataFrom = it.dataFrom,
                    transformedList = it.transformedList,
                    extras = it.extras
                )
            }
        }
    }

    @MainThread
    private suspend fun load(
        sketch: Sketch,
        request: LoadRequest,
        chain: RequestInterceptor.Chain
    ): LoadData {
        request.target?.asOrNull<LoadTarget>()?.onStart()
        return withContextRunCatching(sketch.decodeTaskDispatcher) {
            BitmapDecodeInterceptorChain(
                sketch = sketch,
                request = request,
                requestContext = chain.requestContext,
                fetchResult = null,
                interceptors = sketch.components.getBitmapDecodeInterceptorList(request),
                index = 0,
            ).proceed().let {
                LoadData(
                    bitmap = it.bitmap,
                    imageInfo = it.imageInfo,
                    dataFrom = it.dataFrom,
                    transformedList = it.transformedList,
                    extras = it.extras
                )
            }
        }
    }

    @MainThread
    private suspend fun download(sketch: Sketch, request: DownloadRequest): DownloadData {
        request.target?.asOrNull<DownloadTarget>()?.onStart()

        val fetcher = withContextRunCatching(sketch.decodeTaskDispatcher) {
            sketch.components.newFetcher(request)
        }
        if (fetcher !is HttpUriFetcher) {
            throw IllegalArgumentException("DownloadRequest only support HTTP and HTTPS uri: ${request.uriString}")
        }

        val fetchResult = withContextRunCatching(sketch.decodeTaskDispatcher) {
            fetcher.fetch()
        }
        val dataFrom = fetchResult.dataFrom
        return when (val source = fetchResult.dataSource) {
            is ByteArrayDataSource -> DownloadData(source.data, dataFrom)
            is DiskCacheDataSource -> DownloadData(source.snapshot, dataFrom)
            else -> throw UnsupportedOperationException("Unsupported DataSource for DownloadRequest: ${source::class.qualifiedName}")
        }
    }

    override fun toString(): String = "EngineRequestInterceptor(sortWeight=$sortWeight)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}