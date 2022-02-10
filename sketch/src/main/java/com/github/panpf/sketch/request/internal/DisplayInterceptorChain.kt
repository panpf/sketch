package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DisplayData
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.RequestInterceptor

internal class DisplayInterceptorChain(
    override val initialRequest: DisplayRequest,
    val interceptors: List<RequestInterceptor<DisplayRequest, DisplayData>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DisplayRequest,
) : RequestInterceptor.Chain<DisplayRequest, DisplayData> {

    @MainThread
    override suspend fun proceed(request: DisplayRequest): DisplayData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: DisplayRequest): DisplayInterceptorChain =
        DisplayInterceptorChain(initialRequest, interceptors, index, sketch, request)
}
