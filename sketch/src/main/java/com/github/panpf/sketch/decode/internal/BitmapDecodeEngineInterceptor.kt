package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest
import kotlinx.coroutines.withContext

class BitmapDecodeEngineInterceptor : DecodeInterceptor<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val sketch = chain.sketch
        return withContext(sketch.decodeTaskDispatcher) {
            val request = chain.request
            val componentRegistry = sketch.componentRegistry
            val fetcher = componentRegistry.newFetcher(sketch, request)
            val fetchResult = chain.fetchResult ?: fetcher.fetch()
            componentRegistry.newBitmapDecoder(sketch, request, fetchResult).use {
                it.decode()
            }
        }
    }

    override fun toString(): String = "BitmapDecodeEngineInterceptor"
}