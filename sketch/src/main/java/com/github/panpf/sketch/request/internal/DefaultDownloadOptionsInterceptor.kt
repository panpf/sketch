package com.github.panpf.sketch.request.internal

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.request.DownloadOptions
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.request.RequestInterceptor.Chain

class DefaultDownloadOptionsInterceptor(
    private val defaultDownloadOptions: DownloadOptions?
) : RequestInterceptor<DownloadRequest, DownloadData> {

    @MainThread
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

    override fun toString(): String = "DefaultDownloadOptionsInterceptor"
}