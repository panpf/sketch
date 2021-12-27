package com.github.panpf.sketch.load.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.common.RequestExtras
import com.github.panpf.sketch.load.LoadData
import com.github.panpf.sketch.load.LoadRequest

internal class LoadInterceptorChain(
    val initialRequest: LoadRequest,
    val interceptors: List<Interceptor<LoadRequest, LoadData>>,
    val index: Int,
    override val request: LoadRequest,
) : Interceptor.Chain<LoadRequest, LoadData> {

    @WorkerThread
    override suspend fun proceed(
        sketch: Sketch, request: LoadRequest,
        extras: RequestExtras<LoadRequest, LoadData>?
    ): LoadData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next, extras)
    }

    private fun copy(
        index: Int = this.index,
        request: LoadRequest = this.request,
    ) = LoadInterceptorChain(initialRequest, interceptors, index, request)
}
