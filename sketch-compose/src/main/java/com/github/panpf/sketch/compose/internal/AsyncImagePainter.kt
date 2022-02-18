package com.github.panpf.sketch.compose.internal

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.internal.AsyncImagePainter.State.Empty
import com.github.panpf.sketch.compose.internal.AsyncImagePainter.State.Error
import com.github.panpf.sketch.compose.internal.AsyncImagePainter.State.Loading
import com.github.panpf.sketch.compose.internal.AsyncImagePainter.State.Success
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import kotlinx.coroutines.flow.MutableStateFlow

class AsyncImagePainter(
    context: Context,
    private val sketch: Sketch,
    imageUri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)?,
    private val isPreview: Boolean,
    private val filterQuality: FilterQuality,
) : Painter(), RememberObserver {

    private val target = object : com.github.panpf.sketch.target.Target {
        override fun onStart(placeholder: Drawable?) {
            super.onStart(placeholder)
            state = Loading(placeholder?.toPainter(filterQuality))
        }

        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)
            state = Success(result.toPainter(filterQuality))
        }

        override fun onError(error: Drawable?) {
            super.onError(error)
            state = Error(error?.toPainter(filterQuality))
        }
    }

//    private inner class DrawSizeResolver : SizeResolver {
//
//        override suspend fun size() = drawSize
//            .mapNotNull { size ->
//                when {
//                    size.isUnspecified -> com.github.panpf.sketch.util.Size(-1, -1)
//                    size.isPositive -> com.github.panpf.sketch.util.Size(
//                        size.width.roundToInt(),
//                        size.height.roundToInt()
//                    )
//                    else -> null
//                }
//            }
//            .first()
//    }

    val request = DisplayRequest(context, imageUri, target, configBlock).run {
        // todo Limit the size of the
        if (resizeSize == null && definedOptions.resizeSizeResolver == null) {
            newDisplayRequest {
                resizeSizeResolver(ConstraintsSizeResolver())
            }
        } else {
            this
        }
    }
    var disposable: Disposable<DisplayResult>? = null

    /** The current [AsyncImagePainter.State]. */
    var state: State by mutableStateOf(Empty)
        private set

    override fun onRemembered() {
        if (isPreview) {
            state = Loading(
                request.placeholderImage?.getDrawable(sketch, request, null)
                    ?.toPainter(filterQuality)
            )
        } else if (disposable == null) {
            disposable = sketch.enqueueDisplay(request)
        }
    }

    override fun onForgotten() {
        disposable?.dispose()
        disposable = null
    }

    override fun onAbandoned() = onForgotten()

    internal var painter: Painter? by mutableStateOf(null)

    private var drawSize = MutableStateFlow(Size.Zero)
    private var alpha: Float by mutableStateOf(1f)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        drawSize.value = size

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }

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
        data class Loading(override val painter: Painter?) : State()

        /** The request was successful. */
        data class Success(override val painter: Painter) : State()

        /** The request failed. */
        data class Error(override val painter: Painter?) : State()
    }
}