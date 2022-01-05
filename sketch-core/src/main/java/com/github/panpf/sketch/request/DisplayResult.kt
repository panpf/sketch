package com.github.panpf.sketch.request

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.request.internal.ImageResult

sealed interface DisplayResult : ImageResult {
    val request: DisplayRequest

    class Success(
        override val request: DisplayRequest,
        val drawable: Drawable,
        val info: ImageInfo,
        val from: DataFrom
    ) : DisplayResult

    class Error(
        override val request: DisplayRequest,
        val throwable: Throwable,
        val drawable: Drawable?,
    ) : DisplayResult
}