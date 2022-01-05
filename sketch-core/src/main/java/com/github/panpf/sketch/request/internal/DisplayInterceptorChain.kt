package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.Interceptor

internal class DisplayInterceptorChain(
    val initialRequest: DisplayRequest,
    val interceptors: List<Interceptor<DisplayRequest, DisplayData>>,
    val index: Int,
    override val request: DisplayRequest,
) : Interceptor.Chain<DisplayRequest, DisplayData> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch,
        request: DisplayRequest,
    ): DisplayData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(
        index: Int = this.index,
        request: DisplayRequest = this.request,
    ) = DisplayInterceptorChain(initialRequest, interceptors, index, request)
}
