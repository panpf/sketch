package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.util.requiredMainThread

internal class RequestInterceptorChain(
    override val initialRequest: ImageRequest,
    val interceptors: List<RequestInterceptor>,
    val index: Int,
    override val request: ImageRequest,
    override val requestExtras: RequestExtras,
) : RequestInterceptor.Chain {

    @MainThread
    override suspend fun proceed(request: ImageRequest): ImageData {
        requiredMainThread()
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: ImageRequest): RequestInterceptorChain =
        RequestInterceptorChain(initialRequest, interceptors, index, request, requestExtras)
}
