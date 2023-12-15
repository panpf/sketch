package com.github.panpf.sketch.compose.state

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.AsyncImagePainter.State
import com.github.panpf.sketch.compose.toIntSizeOrNull
import com.github.panpf.sketch.request.DisplayRequest

/**
 * Return an [AsyncImagePainter2] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter2] will not finish loading if [AsyncImagePainter2.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter2] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter2.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param request [DisplayRequest].
 * @param transform A callback to transform a new [State] before it's applied to the
 *  [AsyncImagePainter2]. Typically this is used to overwrite the state's [Painter].
 * @param onPainterState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter2(
    request: DisplayRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    transform: (PainterState) -> PainterState = AsyncImageState.DefaultTransform,
    onPainterState: ((PainterState) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter2 {
    state.request = request
    state.sketch = sketch
    state.contentScale = contentScale
    state.transform = transform
    state.onPainterState = onPainterState
    state.filterQuality = filterQuality
    return remember(state) {
        Log.d(
            "AsyncImageTest",
            "AsyncImagePainter2. new. ${state.request?.uriString}"
        )
        AsyncImagePainter2(state)
    }
}

/**
 * A [Painter] that reads 'painter' from [AsyncImageState] and renders
 */
@Stable
class AsyncImagePainter2 internal constructor(
    private val state: AsyncImageState,
) : Painter() {

    private var alpha: Float by mutableFloatStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = state.painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        if (state.size == null) {
            Log.d(
                "AsyncImageTest",
                "AsyncImagePainter2. onDraw. size null. ${state.request?.uriString}"
            )
        } else if (state.painter == null) {
            Log.d(
                "AsyncImageTest",
                "AsyncImagePainter2. onDraw. painter null. ${state.request?.uriString}"
            )
        } else {
            Log.d("AsyncImageTest", "AsyncImagePainter2. onDraw. ok. ${state.request?.uriString}")
        }
        // Update the draw scope's current size. It plays a decisive role when using AsyncImagePainter without AsyncImage
        state.size = this@onDraw.size.toIntSizeOrNull()

        // Draw the current painter.
        state.painter?.apply { draw(size, alpha, colorFilter) }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }
}
