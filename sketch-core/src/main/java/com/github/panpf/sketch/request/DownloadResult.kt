package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageResult

sealed interface DownloadResult : ImageResult {
    val request: DownloadRequest

    class Success(
        override val request: DownloadRequest,
        val data: DownloadData,
        val from: DataFrom
    ) : DownloadResult

    class Error(
        override val request: DownloadRequest,
        val throwable: Throwable,
    ) : DownloadResult
}