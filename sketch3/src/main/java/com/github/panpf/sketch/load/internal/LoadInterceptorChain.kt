package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.ListenerInfo
import com.github.panpf.sketch.load.LoadRequest
import com.github.panpf.sketch.load.LoadResult

internal class LoadInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<Interceptor<LoadRequest, LoadResult>>,
    val index: Int,
    override val request: LoadRequest,
) : Interceptor.Chain<LoadRequest, LoadResult> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch, request: LoadRequest,
        listenerInfo: ListenerInfo<LoadRequest, LoadResult>?
    ): LoadResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next, listenerInfo)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = LoadInterceptorChain(initialRequest, interceptors, index, request)
}
