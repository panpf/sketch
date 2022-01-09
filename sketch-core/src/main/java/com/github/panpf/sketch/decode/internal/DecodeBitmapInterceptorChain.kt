package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.Interceptor
import com.github.panpf.sketch.request.LoadRequest

internal class DecodeBitmapInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<Interceptor<LoadRequest, BitmapDecodeResult>>,
    val index: Int,
    override val request: LoadRequest,
) : Interceptor.Chain<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch,
        request: LoadRequest,
    ): BitmapDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = DecodeBitmapInterceptorChain(initialRequest, interceptors, index, request)
}
