package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.ImageRequest
import com.github.panpf.sketch.common.ImageResult
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult
import kotlinx.coroutines.withContext

class LoadEngineInterceptor : Interceptor<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, LoadResult>,
        extras: RequestExtras<LoadRequest, LoadResult>?
    ): LoadResult {
        val request = chain.request
        val componentRegistry = sketch.componentRegistry
        val fetchRequestExtras = extras as RequestExtras<ImageRequest, ImageResult>?
        val fetcher = componentRegistry.newFetcher(sketch, request, fetchRequestExtras)
        return withContext(sketch.decodeTaskDispatcher) {
            val fetchResult = fetcher.fetch()
            val decoder = componentRegistry.newDecoder(sketch, request, extras, fetchResult.source)
            val decodeResult = decoder.decode()
            LoadResult(decodeResult.bitmap, decodeResult.info, fetchResult.from)
        }
    }
}