package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.util.SketchException

sealed interface LoadResult : ImageResult {
    val request: LoadRequest

    class Success constructor(
        override val request: LoadRequest,
        val data: LoadData,
    ) : LoadResult

    class Error constructor(
        override val request: LoadRequest,
        val exception: SketchException,
    ) : LoadResult
}