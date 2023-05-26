package com.github.panpf.sketch.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.AsyncImagePainter.Companion.DefaultTransform
import com.github.panpf.sketch.compose.AsyncImagePainter.State
import com.github.panpf.sketch.compose.internal.AsyncImageDisplayTarget
import com.github.panpf.sketch.compose.internal.AsyncImageScaleDecider
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.resize.FixedScaleDecider
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.iterateSketchCountBitmapDrawable
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.github.panpf.sketch.util.Size as CoilSize

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param uriEmpty A [Painter] that is displayed when the request's [DisplayRequest.uriString] is empty.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    imageUri: String?,
    placeholder: Painter? = null,
    error: Painter? = null,
    uriEmpty: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri),
    transform = transformOf(placeholder, error, uriEmpty),
    onState = onStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param uriEmpty A [Painter] that is displayed when the request's [DisplayRequest.uriString] is empty.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
@Deprecated(
    "Please use the request version",
    replaceWith = ReplaceWith("rememberAsyncImagePainter(request = DisplayRequest(LocalContext.current, imageUri), ...)")
)
fun rememberAsyncImagePainter(
    imageUri: String?,
    placeholder: Painter? = null,
    error: Painter? = null,
    uriEmpty: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri, configBlock),
    transform = transformOf(placeholder, error, uriEmpty),
    onState = onStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param transform A callback to transform a new [State] before it's applied to the
 *  [AsyncImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
internal fun rememberAsyncImagePainter(
    imageUri: String?,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri),
    transform = transform,
    onState = onState,
    contentScale = contentScale,
    filterQuality = filterQuality
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param transform A callback to transform a new [State] before it's applied to the
 *  [AsyncImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
@Deprecated(
    "Please use the request version",
    replaceWith = ReplaceWith("rememberAsyncImagePainter(request = DisplayRequest(LocalContext.current, imageUri), ...)")
)
internal fun rememberAsyncImagePainter(
    imageUri: String?,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri, configBlock),
    transform = transform,
    onState = onState,
    contentScale = contentScale,
    filterQuality = filterQuality
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param request [DisplayRequest].
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param uriEmpty A [Painter] that is displayed when the request's [DisplayRequest.uriString] is empty.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    request: DisplayRequest,
    placeholder: Painter? = null,
    error: Painter? = null,
    uriEmpty: Painter? = error,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = request,
    transform = transformOf(placeholder, error, uriEmpty),
    onState = onStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImagePainter.state] will not transition to [State.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `size(Size.ORIGINAL)`) if you need this.
 *
 * @param request [DisplayRequest].
 * @param transform A callback to transform a new [State] before it's applied to the
 *  [AsyncImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onState Called when the state of this painter changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
internal fun rememberAsyncImagePainter(
    request: DisplayRequest,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter {
    validateRequest(request)
    val sketch = LocalContext.current.sketch
    val painter = remember { AsyncImagePainter(request, sketch) }
    painter.transform = transform
    painter.onState = onState
    painter.contentScale = contentScale
    painter.filterQuality = filterQuality
    painter.isPreview = LocalInspectionMode.current
    painter.sketch = sketch
    painter.request = request // Update request last so all other properties are up to date.
    painter.onRemembered() // Invoke this manually so `painter.state` is set to `Loading` immediately.
    return painter
}

/**
 * A [Painter] that that executes an [DisplayRequest] asynchronously and renders the result.
 */
@Stable
class AsyncImagePainter internal constructor(
    request: DisplayRequest,
    sketch: Sketch
) : Painter(), RememberObserver {

    private var rememberScope: CoroutineScope? = null
    private val drawSize = MutableStateFlow(Size.Zero)

    private var painter: Painter? by mutableStateOf(null)
    private var alpha: Float by mutableStateOf(DefaultAlpha)
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
    internal var isPreview = false

    /** The current [AsyncImagePainter.State]. */
    var state: State by mutableStateOf(State.Empty)
        private set

    /** The current [DisplayRequest]. */
    var request: DisplayRequest by mutableStateOf(request)
        internal set

    /** The current [Sketch]. */
    var sketch: Sketch by mutableStateOf(sketch)
        internal set

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        drawSize.value = size

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onRemembered() {
        // Short circuit if we're already remembered.
        if (rememberScope != null) return

        // Create a new scope to observe state and execute requests while we're remembered.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        rememberScope = scope

        // Manually notify the child painter that we're remembered.
        (_painter as? RememberObserver)?.onRemembered()

        // If we're in inspection mode skip the image request and set the state to loading.
//        if (isPreview) {
//            val request = request.newBuilder().defaults(imageLoader.defaults).build()
//            updateState(State.Loading(request.placeholder?.toPainter()))
//            return
//        }
        if (isPreview) {
            val request = sketch.globalImageOptions?.let {
                val defaultOptions = request.defaultOptions
                if (defaultOptions !== it) {
                    val newDefaultOptions = defaultOptions?.merged(it) ?: it
                    request.newBuilder().default(newDefaultOptions).build()
                } else {
                    null
                }
            } ?: request

            val placeholderDrawable = request.placeholder?.getDrawable(sketch, request, null)
            updateState(State.Loading(placeholderDrawable?.toPainter()))
            return
        }

        // Observe the current request and execute any emissions.
        scope.launch {
            snapshotFlow { request }
                .mapLatest { sketch.execute(updateRequest(request)).toState() }
                .collect(::updateState)
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

    private fun clear() {
        rememberScope?.cancel()
        rememberScope = null
    }

    /** Update the [request] to work with [AsyncImagePainter]. */
//    private fun updateRequest(request: DisplayRequest): DisplayRequest {
//        return request.newBuilder()
//            .target(onStart = { placeholder ->
//                updateState(State.Loading(placeholder?.toPainter()))
//            })
//            .apply {
//                if (request.defined.sizeResolver == null) {
//                    // If no other size resolver is set, suspend until the canvas size is positive.
//                    size { drawSize.mapNotNull { it.toSizeOrNull() }.first() }
//                }
//                if (request.defined.scale == null) {
//                    // If no other scale resolver is set, use the content scale.
//                    scale(contentScale.toScale())
//                }
//                if (request.defined.precision != Precision.EXACT) {
//                    // AsyncImagePainter scales the image to fit the canvas size at draw time.
//                    precision(Precision.INEXACT)
//                }
//            }
//            .build()
//    }
    private fun updateRequest(request: DisplayRequest): DisplayRequest {
        return request.newDisplayRequest {
            target(AsyncImageDisplayTarget(object : DisplayTarget {
                override fun onStart(placeholder: Drawable?) {
                    updateState(State.Loading(placeholder?.toPainter()))
                }
            }))
            if (request.definedOptions.resizeSizeResolver == null) {
                // If no other size resolver is set, suspend until the canvas size is positive.
                resizeSize { drawSize.mapNotNull { it.toSizeOrNull() }.first() }
            }
            if (request.definedOptions.resizeScaleDecider == null) {
                // If no other scale resolver is set, use the content scale.
                resizeScale(AsyncImageScaleDecider(FixedScaleDecider(contentScale.toScale())))
            }
        }
    }

    private fun updateState(input: State) {
        val previous = _state
        val current = transform(input)
        _state = current
        _painter = maybeNewCrossfadePainter(previous, current) ?: current.painter

        // Manually forget and remember the old/new painters if we're already remembered.
        if (rememberScope != null && previous.painter !== current.painter) {
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

        // Invoke the transition factory and wrap the painter in a `CrossfadePainter` if it returns
        // a `CrossfadeTransformation`.
//        val transition = result.request.transition?.create(FakeTransitionTarget, result)
        val transition =
            result.request.transitionFactory?.create(FakeTransitionTarget, result, true)
        @Suppress("LiftReturnOrAssignment")
        if (transition is CrossfadeTransition) {
            return CrossfadePainter(
                start = previous.painter.takeIf { previous is State.Loading },
                end = current.painter,
                contentScale = contentScale,
                durationMillis = transition.durationMillis,
                fadeStart = transition.fadeStart,
                preferExactIntrinsicSize = transition.preferExactIntrinsicSize
            )
        } else {
            return null
        }
    }

    private fun DisplayResult.toState() = when (this) {
        is DisplayResult.Success -> State.Success(drawable.toPainter(), this)
        is DisplayResult.Error -> State.Error(drawable?.toPainter(), this)
    }

    /**
     * Convert this [Drawable] into a [Painter] using Compose primitives if possible.
     *
     * Very important, updateDisplayed() needs to set setDisplayed to keep SketchDrawable, SketchStateDrawable
     */
    private fun Drawable.toPainter() = DrawablePainter(mutate())
//        when (this) {
//        is SketchDrawable -> DrawablePainter(mutate())
//        is SketchStateDrawable -> DrawablePainter(mutate())
//        is BitmapDrawable -> BitmapPainter(bitmap.asImageBitmap(), filterQuality = filterQuality)
//        is ColorDrawable -> ColorPainter(Color(color))
//        else -> DrawablePainter(mutate())
//    }

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

//private fun validateRequest(request: DisplayRequest) {
//    when (request.data) {
//        is DisplayRequest.Builder -> unsupportedData(
//            name = "DisplayRequest.Builder",
//            description = "Did you forget to call DisplayRequest.Builder.build()?"
//        )
//        is ImageBitmap -> unsupportedData("ImageBitmap")
//        is ImageVector -> unsupportedData("ImageVector")
//        is Painter -> unsupportedData("Painter")
//    }
//    require(request.target == null) { "request.target must be null." }
//}

private fun validateRequest(request: DisplayRequest) {
    require(request.target == null) { "request.target must be null." }
}

//private fun unsupportedData(
//    name: String,
//    description: String = "If you wish to display this $name, use androidx.compose.foundation.Image."
//): Nothing = throw IllegalArgumentException("Unsupported type: $name. $description")

private val Size.isPositive get() = width >= 0.5 && height >= 0.5

//private fun Size.toSizeOrNull() = when {
//    isUnspecified -> CoilSize.ORIGINAL
//    isPositive -> CoilSize(
//        width = if (width.isFinite()) Dimension(width.roundToInt()) else Dimension.Undefined,
//        height = if (height.isFinite()) Dimension(height.roundToInt()) else Dimension.Undefined
//    )
//    else -> null
//}

private fun Size.toSizeOrNull(): CoilSize? = when {
    isUnspecified -> null
    isPositive && width.isFinite() && height.isFinite() -> CoilSize(
        width.roundToInt(),
        height.roundToInt()
    )
    else -> null
}

//private val FakeTransitionTarget = object : TransitionTarget {
//    override val view get() = throw UnsupportedOperationException()
//    override val drawable: Drawable? get() = null
//}

private val FakeTransitionTarget = object : TransitionDisplayTarget {
    override val drawable: Drawable? get() = null
}
