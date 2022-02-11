package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.util.requiredWorkThread

internal class DrawableDecodeInterceptorChain constructor(
    val interceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DisplayRequest,
    override val requestExtras: RequestExtras,
    override val fetchResult: FetchResult?,
) : DecodeInterceptor.Chain<DisplayRequest, DrawableDecodeResult> {

    @WorkerThread
    override suspend fun proceed(): DrawableDecodeResult {
        requiredWorkThread()
        val interceptor = interceptors[index]
        val next = copy(index = index + 1)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int): DrawableDecodeInterceptorChain =
        DrawableDecodeInterceptorChain(
            interceptors, index, sketch, request, requestExtras, fetchResult
        )
}
