package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.Interceptor

internal class DecodeDrawableInterceptorChain(
    val initialRequest: DisplayRequest,
    val interceptors: List<Interceptor<DisplayRequest, DrawableDecodeResult>>,
    val index: Int,
    override val request: DisplayRequest,
) : Interceptor.Chain<DisplayRequest, DrawableDecodeResult> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch,
        request: DisplayRequest,
    ): DrawableDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(
        index: Int = this.index,
        request: DisplayRequest = this.request,
    ) = DecodeDrawableInterceptorChain(initialRequest, interceptors, index, request)
}
