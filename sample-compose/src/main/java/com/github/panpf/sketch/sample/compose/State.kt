package com.github.panpf.sketch.sample.compose

import androidx.compose.ui.graphics.painter.Painter
import coil.request.ErrorResult
import com.github.panpf.sketch.request.DisplayResult

/**
 * The current state of the [AsyncImagePainter].
 */
sealed class State {

    /** The current painter being drawn by [AsyncImagePainter]. */
    abstract val painter: Painter?

    /** The request has not been started. */
    object Empty : State() {
        override val painter: Painter? get() = null
    }

    /** The request is in-progress. */
    data class Loading(
        override val painter: Painter?,
    ) : State()

    /** The request was successful. */
    data class Success(
        override val painter: Painter,
//        val result: DisplayResult.Success,
    ) : State()

    /** The request failed due to [ErrorResult.throwable]. */
    data class Error(
        override val painter: Painter?,
//        val result: DisplayResult.Error,
    ) : State()
}
