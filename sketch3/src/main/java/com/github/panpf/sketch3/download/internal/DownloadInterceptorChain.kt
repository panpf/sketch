package com.github.panpf.sketch3.download.internal

import com.github.panpf.sketch3.Sketch3
import com.github.panpf.sketch3.common.Interceptor
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult

internal class DownloadInterceptorChain(
    val initialRequest: DownloadRequest,
    val interceptors: List<Interceptor<DownloadRequest, DownloadResult>>,
    val index: Int,
    override val request: DownloadRequest,
//    val eventListener: EventListener,
//    val isPlaceholderCached: Boolean
) : Interceptor.Chain<DownloadRequest, DownloadResult> {

//    override fun withSize(size: Size) = copy(size = size)

    override suspend fun proceed(sketch3: Sketch3, request: DownloadRequest): DownloadResult {
//        if (index > 0) {
//            checkRequest(request, interceptors[index - 1])
//        }
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        val result = interceptor.intercept(sketch3, next)
//        checkRequest(result.request, interceptor)
        return result
    }

//    private fun checkRequest(request: DownloadRequest, interceptor: Interceptor) {
//        check(request.context === initialRequest.context) {
//            "Interceptor '$interceptor' cannot modify the request's context."
//        }
//        check(request.data !== NullRequestData) {
//            "Interceptor '$interceptor' cannot set the request's data to null."
//        }
//        check(request.target === initialRequest.target) {
//            "Interceptor '$interceptor' cannot modify the request's target."
//        }
//        check(request.lifecycle === initialRequest.lifecycle) {
//            "Interceptor '$interceptor' cannot modify the request's lifecycle."
//        }
//        check(request.sizeResolver === initialRequest.sizeResolver) {
//            "Interceptor '$interceptor' cannot modify the request's size resolver. " +
//                "Use `Interceptor.Chain.withSize` instead."
//        }
//    }

    private fun copy(
        index: Int = this.index,
        request: DownloadRequest = this.request,
//        size: Size = this.size
    ) = DownloadInterceptorChain(initialRequest, interceptors, index, request)
}
