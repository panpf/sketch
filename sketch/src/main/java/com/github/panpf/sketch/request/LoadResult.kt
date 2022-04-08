package com.github.panpf.sketch.request

import android.graphics.Bitmap
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.SketchException

sealed interface LoadResult : ImageResult {

    class Success constructor(
        override val request: ImageRequest,
        val bitmap: Bitmap,
        val imageInfo: ImageInfo,
        val dataFrom: DataFrom
    ) : LoadResult, ImageResult.Success

    class Error constructor(
        override val request: ImageRequest,
        override val exception: SketchException,
    ) : LoadResult, ImageResult.Error
}