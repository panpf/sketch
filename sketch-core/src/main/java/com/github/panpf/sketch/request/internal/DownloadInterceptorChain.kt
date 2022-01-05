package com.github.panpf.sketch.request.internal

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.Interceptor
import com.github.panpf.sketch.request.DownloadRequest

internal class DownloadInterceptorChain(
    val initialRequest: DownloadRequest,
    val interceptors: List<Interceptor<DownloadRequest, DownloadData>>,
    val index: Int,
    override val request: DownloadRequest,
) : Interceptor.Chain<DownloadRequest, DownloadData> {

    @WorkerThread
    override suspend fun proceed(sketch: Sketch, request: DownloadRequest): DownloadData {
        val interceptor = interceptors[index]
        val next = copy(index = index + 1, request = request)
        return interceptor.intercept(sketch, next)
    }

    private fun copy(index: Int = this.index, request: DownloadRequest = this.request) =
        DownloadInterceptorChain(initialRequest, interceptors, index, request)
}
