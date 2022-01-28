package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor

internal class DisplayInterceptorChain(
    val initialRequest: DisplayRequest,
    val interceptors: List<RequestInterceptor<DisplayRequest, DisplayData>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DisplayRequest,
) : RequestInterceptor.Chain<DisplayRequest, DisplayData> {

    @WorkerThread
    override suspend fun proceed(request: DisplayRequest): DisplayData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(
        index: Int = this.index,
        request: DisplayRequest = this.request,
    ) = DisplayInterceptorChain(initialRequest, interceptors, index, sketch, request)
}
