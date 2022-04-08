package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult

class DrawableEngineDecodeInterceptor : DecodeInterceptor<DrawableDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<DrawableDecodeResult>,
    ): DrawableDecodeResult {
        val request = chain.request
        val componentRegistry = chain.sketch.componentRegistry
        val fetcher = componentRegistry.newFetcher(chain.sketch, request)
        val fetchResult = chain.fetchResult ?: fetcher.fetch()
        return componentRegistry
            .newDrawableDecoder(chain.sketch, request, chain.requestExtras, fetchResult)
            .decode()
    }

    override fun toString(): String = "DrawableEngineDecodeInterceptor"
}