package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.LoadResult

internal class LoadInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<Interceptor<LoadRequest, LoadResult>>,
    val index: Int,
    override val request: LoadRequest,
) : Interceptor.Chain<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch,
        request: LoadRequest,
        httpFetchProgressListenerDelegate: ProgressListenerDelegate<LoadRequest>?
    ): LoadResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next, httpFetchProgressListenerDelegate)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = LoadInterceptorChain(initialRequest, interceptors, index, request)
}
