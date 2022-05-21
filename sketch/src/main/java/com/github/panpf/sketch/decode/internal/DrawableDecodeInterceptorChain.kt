package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.util.requiredWorkThread

internal class DrawableDecodeInterceptorChain constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val requestContext: RequestContext,
    override val fetchResult: FetchResult?,
    private val interceptors: List<DecodeInterceptor<DrawableDecodeResult>>,
    private val index: Int,
) : DecodeInterceptor.Chain<DrawableDecodeResult> {

    @WorkerThread
    override suspend fun proceed(): DrawableDecodeResult {
        requiredWorkThread()
        val interceptor = interceptors[index]
        val next = copy(index = index + 1)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int): DrawableDecodeInterceptorChain =
        DrawableDecodeInterceptorChain(
            sketch, request, requestContext, fetchResult, interceptors, index
        )
}
