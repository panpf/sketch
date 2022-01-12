package com.github.panpf.sketch.decode.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.decode.DrawableDecodeResult
import com.github.panpf.sketch.request.DisplayRequest

internal class DrawableDecodeInterceptorChain(
    val initialRequest: DisplayRequest,
    val interceptors: List<DecodeInterceptor<DisplayRequest, DrawableDecodeResult>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DisplayRequest,
    override val dataSource: DataSource?,
) : DecodeInterceptor.Chain<DisplayRequest, DrawableDecodeResult> {

    @WorkerThread
    override suspend fun proceed(request: DisplayRequest): DrawableDecodeResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(
        index: Int = this.index,
        request: DisplayRequest = this.request,
    ) = DrawableDecodeInterceptorChain(
        initialRequest,
        interceptors,
        index,
        sketch,
        request,
        dataSource
    )
}
