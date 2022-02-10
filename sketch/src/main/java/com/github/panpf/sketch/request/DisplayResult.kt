package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.util.SketchException

sealed interface DisplayResult : ImageResult {
    val request: DisplayRequest
    val drawable: Drawable?

    class Success constructor(
        override val request: DisplayRequest,
        override val drawable: Drawable,
        val imageInfo: ImageInfo,
        val dataFrom: DataFrom
    ) : DisplayResult

    class Error constructor(
        override val request: DisplayRequest,
        override val drawable: Drawable?,
        val exception: SketchException,
    ) : DisplayResult
}