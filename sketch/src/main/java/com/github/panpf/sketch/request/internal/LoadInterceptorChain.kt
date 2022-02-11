package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.util.requiredMainThread

internal class LoadInterceptorChain(
    override val initialRequest: LoadRequest,
    val interceptors: List<RequestInterceptor<LoadRequest, LoadData>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: LoadRequest,
    override val requestExtras: RequestExtras,
) : RequestInterceptor.Chain<LoadRequest, LoadData> {

    @MainThread
    override suspend fun proceed(request: LoadRequest): LoadData {
        requiredMainThread()
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: LoadRequest): LoadInterceptorChain =
        LoadInterceptorChain(initialRequest, interceptors, index, sketch, request, requestExtras)
}
