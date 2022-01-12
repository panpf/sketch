package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.LoadRequest

internal class BitmapDecodeInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<DecodeInterceptor<LoadRequest, BitmapDecodeResult>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: LoadRequest,
    override val dataSource: DataSource?,
) : DecodeInterceptor.Chain<LoadRequest, BitmapDecodeResult> {

    @WorkerThread
    override suspend fun proceed(request: LoadRequest): BitmapDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = BitmapDecodeInterceptorChain(
        initialRequest,
        interceptors,
        index,
        sketch,
        request,
        dataSource
    )
}
