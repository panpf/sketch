package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.RequestInterceptor

internal class LoadInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<RequestInterceptor<LoadRequest, LoadData>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: LoadRequest,
) : RequestInterceptor.Chain<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun proceed(request: LoadRequest): LoadData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: LoadRequest): LoadInterceptorChain =
        LoadInterceptorChain(initialRequest, interceptors, index, sketch, request)
}
