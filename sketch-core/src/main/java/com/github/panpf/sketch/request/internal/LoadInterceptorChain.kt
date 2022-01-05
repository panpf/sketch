package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadData
import com.github.panpf.sketch.request.LoadRequest

internal class LoadInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<Interceptor<LoadRequest, LoadData>>,
    val index: Int,
    override val request: LoadRequest,
) : Interceptor.Chain<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch,
        request: LoadRequest,
    ): LoadData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = LoadInterceptorChain(initialRequest, interceptors, index, request)
}
