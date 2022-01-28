package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.util.SketchException

sealed interface DownloadResult : ImageResult {
    val request: DownloadRequest

    class Success constructor(
        override val request: DownloadRequest,
        val data: DownloadData,
        val from: DataFrom
    ) : DownloadResult

    class Error constructor(
        override val request: DownloadRequest,
        val exception: SketchException,
    ) : DownloadResult
}