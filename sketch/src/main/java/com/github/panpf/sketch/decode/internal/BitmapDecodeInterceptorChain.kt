package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.decode.DecodeInterceptor
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.RequestExtras
import com.github.panpf.sketch.util.requiredWorkThread

internal class BitmapDecodeInterceptorChain constructor(
    override val sketch: Sketch,
    override val request: ImageRequest,
    override val requestExtras: RequestExtras,
    override val fetchResult: FetchResult?,
    private val interceptors: List<DecodeInterceptor<BitmapDecodeResult>>,
    private val index: Int,
) : DecodeInterceptor.Chain<BitmapDecodeResult> {

    @WorkerThread
    override suspend fun proceed(): BitmapDecodeResult {
        requiredWorkThread()
        val interceptor = interceptors[index]
        val next = copy(index = index + 1)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int): BitmapDecodeInterceptorChain =
        BitmapDecodeInterceptorChain(
            sketch, request, requestExtras, fetchResult, interceptors, index
        )
}
