package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.LoadRequest

internal class BitmapDecodeInterceptorChain constructor(
    override val initialRequest: LoadRequest,
    val interceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: LoadRequest,
    override val fetchResult: FetchResult?,
) : DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun proceed(request: LoadRequest): BitmapDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: LoadRequest): BitmapDecodeInterceptorChain =
        BitmapDecodeInterceptorChain(
            initialRequest, interceptors, index, sketch, request, fetchResult
        )
}
