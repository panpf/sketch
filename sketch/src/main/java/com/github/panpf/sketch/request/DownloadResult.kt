package com.github.panpf.sketch.request

import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.util.SketchException

sealed interface DownloadResult : ImageResult {

    data class Success constructor(
        override val request: DownloadRequest,
        val data: DownloadData,
        val dataFrom: DataFrom = data.dataFrom
    ) : DownloadResult, ImageResult.Success

    data class Error constructor(
        override val request: DownloadRequest,
        override val exception: SketchException,
    ) : DownloadResult, ImageResult.Error
}