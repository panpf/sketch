package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadOptions
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class DefaultOptionsDownloadInterceptor(
    private val defaultDownloadOptions: DownloadOptions?
) : RequestInterceptor<DownloadRequest, DownloadData> {
    override suspend fun intercept(chain: Chain<DownloadRequest, DownloadData>): DownloadData {
        val request = if (defaultDownloadOptions?.isEmpty() == true) {
            chain.request.newDownloadRequest {
                options(defaultDownloadOptions, requestFirst = true)
            }
        } else {
            chain.request
        }
        return chain.proceed(request)
    }

    override fun toString(): String = "DefaultOptionsDownloadInterceptor"
}