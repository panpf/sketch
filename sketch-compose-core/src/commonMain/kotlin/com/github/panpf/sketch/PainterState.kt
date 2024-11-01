package com.github.panpf.sketch

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.PainterState.Error
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.PainterState.Success
import com.github.panpf.sketch.request.ImageResult

/**
 * The current painter state of the [AsyncImageState].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterStateTest
 */
@Stable
sealed interface PainterState {

    /** The current painter being drawn by [AsyncImagePainter]. */
    val painter: Painter?

    /** The request is in-progress. */
    data class Loading(
        override val painter: Painter?,
    ) : PainterState

    /** The request was successful. */
    data class Success constructor(
        override val painter: Painter,  // TODO result: ImageResult.Success
    ) : PainterState

    /** The request failed due to [ImageResult.Error.throwable]. */
    data class Error(
        override val painter: Painter?,  // TODO result: ImageResult.Error
    ) : PainterState
}

/**
 * Returns the name of the [PainterState].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.PainterStateTest.testPainterStateName
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
val PainterState.name: String
    get() = when (this) {
        is Loading -> "Loading"
        is Success -> "Success"
        is Error -> "Error"
        else -> this.toString()
    }