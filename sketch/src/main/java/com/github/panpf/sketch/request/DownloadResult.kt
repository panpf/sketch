package com.github.panpf.sketch.request

import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.util.SketchException

sealed interface DownloadResult : ImageResult {

    data class Success constructor(
        override val request: ImageRequest,
        val data: DownloadData,
        val dataFrom: DataFrom
    ) : DownloadResult, ImageResult.Success

    data class Error constructor(
        override val request: ImageRequest,
        override val exception: SketchException,
    ) : DownloadResult, ImageResult.Error
}