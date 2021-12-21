package com.github.panpf.sketch3.download.internal

import com.github.panpf.sketch3.common.Interceptor
import com.github.panpf.sketch3.download.DownloadRequest
import com.github.panpf.sketch3.download.DownloadResult

class DownloadEngineInterceptor : Interceptor<DownloadRequest, DownloadResult> {
    override fun intercept(chain: Interceptor.Chain<DownloadRequest, DownloadResult>): DownloadResult {
        TODO("Not yet implemented")
    }
}