package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class DecodeBitmapEngineInterceptor : Interceptor<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        sketch: Sketch,
        chain: Interceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult = withContext(sketch.decodeTaskDispatcher) {
        val request = chain.request
        val componentRegistry = sketch.componentRegistry
        val fetcher = componentRegistry.newFetcher(sketch, request)
        val source = withContext(sketch.decodeTaskDispatcher) {
            fetcher.fetch()
        }.source
        componentRegistry.newDecoder(sketch, request, source).decodeBitmap()
    }
}