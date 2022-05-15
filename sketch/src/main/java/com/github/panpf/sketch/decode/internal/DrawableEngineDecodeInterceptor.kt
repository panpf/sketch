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
        val components = chain.sketch.components
        val fetcher = components.newFetcher(request)
        val fetchResult = chain.fetchResult ?: fetcher.fetch()
        return components
            .newDrawableDecoder(request, chain.requestExtras, fetchResult)
            .decode()
    }

    override fun toString(): String = "DrawableEngineDecodeInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}