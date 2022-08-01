package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.util.requiredMainThread

internal class RequestInterceptorChain(
    override val sketch: Sketch,
    override val initialRequest: ImageRequest,
    override val request: ImageRequest,
    override val requestContext: RequestContext,
    private val interceptors: List<RequestInterceptor>,
    private val index: Int,
) : RequestInterceptor.Chain {

    @MainThread
    override suspend fun proceed(request: ImageRequest): ImageData {
        requiredMainThread()
        requestContext.addRequest(request)
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: ImageRequest): RequestInterceptorChain =
        RequestInterceptorChain(
            sketch, initialRequest, request, requestContext, interceptors, index
        )
}
