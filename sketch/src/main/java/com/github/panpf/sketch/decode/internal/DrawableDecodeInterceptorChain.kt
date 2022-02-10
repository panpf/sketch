package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest

internal class DrawableDecodeInterceptorChain(
    override val initialRequest: DisplayRequest,
    val interceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DisplayRequest,
    override val fetchResult: FetchResult?,
) : DecodeInterceptor.Chain<DisplayRequest, DrawableDecodeResult> {

    @WorkerThread
    override suspend fun proceed(request: DisplayRequest): DrawableDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: DisplayRequest): DrawableDecodeInterceptorChain =
        DrawableDecodeInterceptorChain(
            initialRequest, interceptors, index, sketch, request, fetchResult
        )
}
