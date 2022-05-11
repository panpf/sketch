package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor

class BitmapEngineDecodeInterceptor : DecodeInterceptor<BitmapDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<BitmapDecodeResult>,
    ): BitmapDecodeResult {
        val request = chain.request
        val components = chain.sketch.components
        val fetcher = components.newFetcher(request)
        val fetchResult = chain.fetchResult ?: fetcher.fetch()
        return components
            .newBitmapDecoder(request, chain.requestExtras, fetchResult)
            .decode()
    }

    override fun toString(): String = "BitmapEngineDecodeInterceptor"
}