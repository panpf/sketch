package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor

class BitmapEngineDecodeInterceptor : DecodeInterceptor<BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val sketch = chain.sketch
        val request = chain.request
        val componentRegistry = sketch.componentRegistry
        val fetcher = componentRegistry.newFetcher(sketch, request)
        val fetchResult = chain.fetchResult ?: fetcher.fetch()
        return componentRegistry
            .newBitmapDecoder(sketch, request, chain.requestExtras, fetchResult)
            .decode()
    }

    override fun toString(): String = "BitmapEngineDecodeInterceptor"
}