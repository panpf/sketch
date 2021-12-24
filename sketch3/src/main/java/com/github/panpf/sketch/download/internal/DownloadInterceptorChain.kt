package com.github.panpf.sketch.download.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.common.Interceptor
import com.github.panpf.sketch.download.DownloadRequest
import com.github.panpf.sketch.download.DownloadResult

internal class DownloadInterceptorChain(
    val initialRequest: DownloadRequest,
    val interceptors: List<Interceptor<DownloadRequest, DownloadResult>>,
    val index: Int,
    override val request: DownloadRequest,
) : Interceptor.Chain<DownloadRequest, DownloadResult> {

    @WorkerThread
    override suspend fun proceed(sketch: Sketch, request: DownloadRequest): DownloadResult {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(
        index: Int = this.index,
        request: DownloadRequest = this.request,
    ) = DownloadInterceptorChain(initialRequest, interceptors, index, request)
}
