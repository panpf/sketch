package com.github.panpf.sketch.request

import com.github.panpf.sketch.request.internal.ImageResult

sealed interface LoadResult : ImageResult {
    val request: LoadRequest

    class Success(
        override val request: LoadRequest,
        val data: LoadData,
    ) : LoadResult

    class Error(
        override val request: LoadRequest,
        val throwable: Throwable,
    ) : LoadResult
}