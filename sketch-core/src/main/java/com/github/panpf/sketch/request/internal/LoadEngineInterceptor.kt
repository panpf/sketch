package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.ListenerInfo
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        listenerInfo: ListenerInfo<LoadRequest, LoadResult>?
    ): LoadResult {
        val request = chain.request
        val componentRegistry = sketch.componentRegistry
        val fetchRequestExtras = listenerInfo as ListenerInfo<ImageRequest, ImageResult>?
        val fetcher = componentRegistry.newFetcher(sketch, request, fetchRequestExtras)
        return withContext(sketch.decodeTaskDispatcher) {
            val fetchResult = fetcher.fetch()
            val decoder = componentRegistry.newDecoder(sketch, request, listenerInfo, fetchResult.source)
            val decodeResult = decoder.decode()
            LoadResult(decodeResult.bitmap, decodeResult.info, fetchResult.from)
        }
    }
}