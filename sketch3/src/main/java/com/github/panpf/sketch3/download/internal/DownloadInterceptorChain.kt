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
) : Interceptor.Chain<DownloadRequest, DownloadResult> {

    override suspend fun proceed(sketch3: Sketch3, request: DownloadRequest): DownloadResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch3, next)
    }

    private fun copy(
        index: Int = this.index,
        request: DownloadRequest = this.request,
    ) = DownloadInterceptorChain(initialRequest, interceptors, index, request)
}
