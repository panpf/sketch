package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.request.DisplayRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrawableDecodeEngineInterceptor : DecodeInterceptor<DisplayRequest, DrawableDecodeResult> {

    @WorkerThread
    override suspend fun intercept(
        chain: DecodeInterceptor.Chain<DisplayRequest, DrawableDecodeResult>,
    ): DrawableDecodeResult = withContext(Dispatchers.IO) {
        val request = chain.request
        val componentRegistry = chain.sketch.componentRegistry
        val fetcher = componentRegistry.newFetcher(chain.sketch, request)
        val fetchResult = chain.fetchResult ?: fetcher.fetch()
        componentRegistry.newDrawableDecoder(chain.sketch, request, fetchResult).use {
            it.decode()
        }
    }

    override fun toString(): String = "DrawableDecodeEngineInterceptor"
}