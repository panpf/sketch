package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.util.SketchException

sealed interface LoadResult : ImageResult {
    val request: LoadRequest

    class Success constructor(
        override val request: LoadRequest,
        val bitmap: Bitmap,
        val imageInfo: ImageInfo,
        val dataFrom: DataFrom
    ) : LoadResult

    class Error constructor(
        override val request: LoadRequest,
        val exception: SketchException,
    ) : LoadResult
}