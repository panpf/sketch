package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestInterceptor

internal class DownloadInterceptorChain(
    override val initialRequest: DownloadRequest,
    val interceptors: List<RequestInterceptor<DownloadRequest, DownloadData>>,
    val index: Int,
    override val sketch: Sketch,
    override val request: DownloadRequest,
) : RequestInterceptor.Chain<DownloadRequest, DownloadData> {

    @MainThread
    override suspend fun proceed(request: DownloadRequest): DownloadData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(next)
    }

    private fun copy(index: Int, request: DownloadRequest): DownloadInterceptorChain =
        DownloadInterceptorChain(initialRequest, interceptors, index, sketch, request)
}
