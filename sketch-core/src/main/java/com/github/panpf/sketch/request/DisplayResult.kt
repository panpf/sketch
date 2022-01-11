package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.internal.ImageResult
import com.github.panpf.sketch.util.SketchException

sealed interface DisplayResult : ImageResult {
    val request: DisplayRequest

    class Success constructor(
        override val request: DisplayRequest,
        val data: DisplayData
    ) : DisplayResult

    class Error constructor(
        override val request: DisplayRequest,
        val exception: SketchException,
        val drawable: Drawable?,
    ) : DisplayResult
}