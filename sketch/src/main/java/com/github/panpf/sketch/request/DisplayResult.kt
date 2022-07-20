package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.util.SketchException

sealed interface DisplayResult : ImageResult {

    val drawable: Drawable?

    data class Success constructor(
        override val request: DisplayRequest,
        override val drawable: Drawable,
        val imageInfo: ImageInfo,
        val dataFrom: DataFrom,
        val transformedList: List<String>?,
    ) : DisplayResult, ImageResult.Success

    data class Error constructor(
        override val request: DisplayRequest,
        override val drawable: Drawable?,
        override val exception: SketchException,
    ) : DisplayResult, ImageResult.Error
}