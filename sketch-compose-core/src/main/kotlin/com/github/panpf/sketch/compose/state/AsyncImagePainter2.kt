package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.compose.AsyncImagePainter.State
import com.github.panpf.sketch.compose.CrossfadePainter
import com.github.panpf.sketch.compose.state.AsyncImagePainter2.State.Loading
import com.github.panpf.sketch.compose.toIntSizeOrNull
import com.github.panpf.sketch.compose.toPainter
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.iterateSketchCountBitmapDrawable
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

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
 * @param onState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter2(
    state: AsyncImageState,
    transform: (AsyncImagePainter2.State) -> AsyncImagePainter2.State = AsyncImagePainter2.DefaultTransform,
    onState: ((AsyncImagePainter2.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter2 {
    val inspectionMode = LocalInspectionMode.current
    val painter = remember(state, inspectionMode) {
        state.sketch.logger.d("NewAsyncImageTest") {
            "rememberAsyncImagePainter2. new. ${state.request.uriString}"
        }
        AsyncImagePainter2(state, inspectionMode)
    }
    state.contentScale = contentScale
    painter.transform = transform
    painter.onState = onState
    painter.contentScale = contentScale
    painter.filterQuality = filterQuality
    painter.onRemembered() // Invoke this manually so `painter.state` is set to `Loading` immediately.
    return painter
}

/**
 * A [Painter] that that executes an [DisplayRequest] asynchronously and renders the result.
 */
@Stable
class AsyncImagePainter2 internal constructor(
    private val imageState: AsyncImageState,
    private val inspectionMode: Boolean,
) : Painter(), RememberObserver {

    val logModule = "AsyncImagePainter2@${Integer.toHexString(hashCode())}"

    private var coroutineScope: CoroutineScope? = null

    private var painter: Painter? by mutableStateOf(null)
    private var alpha: Float by mutableFloatStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    // These fields allow access to the current value
    // instead of the value in the current composition.
    private var _state: State = State.Empty
        set(value) {
            field = value
            state = value
        }
    private var _painter: Painter? = null
        set(value) {
            field = value
            painter = value
        }

    internal var transform = DefaultTransform
    internal var onState: ((State) -> Unit)? = null
    internal var contentScale = ContentScale.Fit
    internal var filterQuality = DefaultFilterQuality

    /** The current [AsyncImagePainter2.State]. */
    var state: State by mutableStateOf(State.Empty)
        private set

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        val drawSize = size.toIntSizeOrNull()
        if (drawSize != null && drawSize != imageState.size) {
            imageState.sketch.logger.d("NewAsyncImageTest") {
                "$logModule. onSizeChanged: ${imageState.size} -> $drawSize. ${imageState.request.uriString}"
            }
            imageState.size = drawSize
        }

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }

    override fun onRemembered() {
        // onRemembered will be executed multiple times
        if (coroutineScope != null) return

        imageState.sketch.logger.d("NewAsyncImageTest") {
            "$logModule. onRemembered. ${imageState.request.uriString}"
        }
        // Create a new scope to observe state and execute requests while we're remembered.
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        // Manually notify the child painter that we're remembered.
        (_painter as? RememberObserver)?.onRemembered()

        // If we're in inspection mode skip the image request and set the state to loading.
        if (inspectionMode && imageState.painterState == null) {
            val request = imageState.request
            val globalImageOptions = imageState.sketch.globalImageOptions
            val mergedOptions = request.defaultOptions?.merged(globalImageOptions)
            val updatedRequest = request.newBuilder().default(mergedOptions).build()
            val placeholderDrawable = updatedRequest.placeholder
                ?.getDrawable(imageState.sketch, updatedRequest, null)
            updateState(Loading(placeholderDrawable?.toPainter()))
            return
        }

        coroutineScope?.launch {
            snapshotFlow { imageState.painterState }.filterNotNull().collect {
                imageState.sketch.logger.d("NewAsyncImageTest") {
                    "$logModule. updateState. $it. ${imageState.request.uriString}"
                }
                updateState(it)
            }
        }
    }

    override fun onForgotten() {
        updateState(State.Empty)
        clear()
        (_painter as? RememberObserver)?.onForgotten()
    }

    override fun onAbandoned() {
        clear()
        (_painter as? RememberObserver)?.onAbandoned()
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    private fun clear() {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun updateState(input: State) {
        val previous = _state
        val current = transform(input)
        _state = current
        _painter = maybeNewCrossfadePainter(previous, current) ?: current.painter

        // Manually forget and remember the old/new painters if we're already remembered.
        if (coroutineScope != null && previous.painter !== current.painter) {
            (previous.painter as? RememberObserver)?.onForgotten()
            (current.painter as? RememberObserver)?.onRemembered()
            updateDisplayed(previous.painter, current.painter)
        }

        // Notify the state listener.
        onState?.invoke(current)
    }

    private fun updateDisplayed(oldPainter: Painter?, newPainter: Painter?) {
        newPainter?.takeIf { it is DrawablePainter }
            ?.let { it as DrawablePainter }
            ?.drawable?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(true, "AsyncImagePainter")
            }
        oldPainter?.takeIf { it is DrawablePainter }
            ?.let { it as DrawablePainter }
            ?.drawable?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(false, "AsyncImagePainter")
            }
    }

    /** Create and return a [CrossfadePainter] if requested. */
    private fun maybeNewCrossfadePainter(previous: State, current: State): CrossfadePainter? {
        // We can only invoke the transition factory if the state is success or error.
        val result = when (current) {
            is State.Success -> current.result
            is State.Error -> current.result
            else -> return null
        }

        // Invoke the transition factory and wrap the painter in a `CrossfadePainter` if it returns a `CrossfadeTransformation`.
        val transition =
            result.request.transitionFactory?.create(fakeTransitionTarget, result, true)
        return if (transition is CrossfadeTransition) {
            CrossfadePainter(
                start = previous.painter.takeIf { previous is Loading },
                end = current.painter,
                contentScale = contentScale,
                durationMillis = transition.durationMillis,
                fadeStart = transition.fadeStart,
                preferExactIntrinsicSize = transition.preferExactIntrinsicSize
            )
        } else {
            null
        }
    }

    /**
     * The current state of the [AsyncImagePainter2].
     */
    sealed class State {

        /** The current painter being drawn by [AsyncImagePainter2]. */
        abstract val painter: Painter?

        /** The request has not been started. */
        data object Empty : State() {
            override val painter: Painter? get() = null
        }

        /** The request is in-progress. */
        data class Loading(
            override val painter: Painter?,
        ) : State()

        /** The request was successful. */
        data class Success(
            override val painter: Painter,
            val result: DisplayResult.Success,
        ) : State()

        /** The request failed due to [DisplayResult.Error.throwable]. */
        data class Error(
            override val painter: Painter?,
            val result: DisplayResult.Error,
        ) : State()
    }

    companion object {
        /**
         * A state transform that does not modify the state.
         */
        val DefaultTransform: (State) -> State = { it }
    }
}

private val fakeTransitionTarget = object : TransitionDisplayTarget {
    override val drawable: Drawable? get() = null
    override val supportDisplayCount: Boolean = true
}
