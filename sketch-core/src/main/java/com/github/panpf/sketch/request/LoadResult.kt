package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.request.internal.ImageResult

sealed interface LoadResult : ImageResult {
    val request: LoadRequest

    class Success(
        override val request: LoadRequest,
        val bitmap: Bitmap,
        val info: ImageInfo,
        val from: DataFrom
    ) : LoadResult

    class Error(
        override val request: LoadRequest,
        val throwable: Throwable,
    ) : LoadResult
}