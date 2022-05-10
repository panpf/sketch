package com.github.panpf.sketch.compose.internal

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.internal.AsyncImagePainter.State
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.sketch
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.findLastSketchDrawable
import kotlinx.coroutines.Dispatchers
import com.github.panpf.sketch.target.DisplayTarget as SketchTarget

@Composable
fun rememberAsyncImagePainter(
    imageUri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)?,
    filterQuality: FilterQuality,
    contentScale: ContentScale,
    resizeScale: Scale,
): AsyncImagePainter {
    val context = LocalContext.current
    val sketch = context.sketch
    val isPreview = LocalInspectionMode.current
    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    val imagePainter = remember(scope) {
        AsyncImagePainter(
            context = context,
            sketch = sketch,
            imageUri = imageUri,
            configBlock = configBlock,
            contentScale = contentScale,
            resizeScale = resizeScale,
            isPreview = isPreview,
            filterQuality = filterQuality
        )
    }
    // Invoke this manually so `painter.state` is up to date immediately.
    // It must be updated immediately or it will crash in the LazyColumn or LazyVerticalGrid
    imagePainter.onRemembered()
    // transition
    updateTransition(imagePainter)
    return imagePainter
}

class AsyncImagePainter(
    context: Context,
    private val sketch: Sketch,
    imageUri: String?,
    configBlock: (DisplayRequest.Builder.() -> Unit)?,
    val contentScale: ContentScale,
    @Suppress("CanBeParameter") val resizeScale: Scale,
    private val isPreview: Boolean,
    private val filterQuality: FilterQuality,
) : Painter(), RememberObserver {

    private val target = createSketchTarget()
    private var disposable: Disposable<DisplayResult>? = null

    val request = buildRequest(context, imageUri, configBlock, resizeScale, target)

    /** The current [AsyncImagePainter.State]. */
    internal var state: State by mutableStateOf(State.Empty)
        private set

    override fun onRemembered() {
        if (isPreview) {
            state = State.Loading(
                request.placeholderImage
                    ?.getDrawable(request, null)
                    ?.toPainter(filterQuality)
            )
        } else if (disposable == null) {


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
//            if (request.defined.sizeResolver == null) {
//                resizeSize(DrawSizeResolver())
//            }
            disposable = sketch.enqueue(request)
        }
    }

    override fun onForgotten() {
        disposable?.dispose()
        disposable = null
    }

    override fun onAbandoned() = onForgotten()

    internal var painter: Painter? by mutableStateOf(null)

    //    private var drawSize = MutableStateFlow(Size.Zero)
    private var alpha: Float by mutableStateOf(1f)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
//        drawSize.value = size

        // Draw the current painter.
        painter?.apply { draw(size, alpha, colorFilter) }
    }

    private fun createSketchTarget(): SketchTarget =
        object : SketchTarget {
            override fun onStart(placeholder: Drawable?) {
                super.onStart(placeholder)
                state = State.Loading(placeholder?.toPainter(filterQuality))
            }

            override fun onSuccess(result: Drawable) {
                super.onSuccess(result)
                state = State.Success(result.toPainter(filterQuality), result)
            }

            override fun onError(error: Drawable?) {
                super.onError(error)
                state = State.Error(error?.toPainter(filterQuality), error)
            }
        }

    private fun buildRequest(
        context: Context,
        imageUri: String?,
        configBlock: (DisplayRequest.Builder.() -> Unit)?,
        resizeScale: Scale,
        target: SketchTarget
    ): DisplayRequest =
        DisplayRequest(context, imageUri) {
            target(target)
            configBlock?.invoke(this)
        }.run {
            val resetSizeResolver = resize == null && definedOptions.resizeSizeResolver == null
            val resetScale = definedOptions.resizeScaleDecider == null
            if (resetSizeResolver || resetScale) {
                newDisplayRequest {
                    if (resetSizeResolver) {
                        resizeSize(ConstraintsSizeResolver())
                    }
                    if (resetScale) {
                        resizeScale(resizeScale)
                    }
                }
            } else {
                this
            }
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
        data class Success(override val painter: Painter, val drawable: Drawable) : State()

        /** The request failed. */
        data class Error(override val painter: Painter?, val drawable: Drawable?) : State()
    }
}

/**
 * Allows us to observe the current [AsyncImagePainter.painter]. This function allows us to
 * minimize the amount of recomposition needed such that this function only needs to be restarted
 * when the [AsyncImagePainter.state] changes.
 */
@Composable
private fun updateTransition(imagePainter: AsyncImagePainter) {
    val request = imagePainter.request
    // This may look like a useless remember, but this allows any painter instances
    // to receive remember events (if it implements RememberObserver). Do not remove.
    val state = imagePainter.state
    val painter = remember(state) { state.painter }

    // Short circuit if the crossfade transition isn't set.
    // Check `imageLoader.defaults.transitionFactory` specifically as the default isn't set
    // until the request is executed.
    val transition = request.transition
    if (transition !is CrossfadeTransition.Factory) {
        imagePainter.painter = painter
        return
    }

    // Keep track of the most recent loading painter to crossfade from it.
    val loading = remember(request) { ValueHolder<Painter?>(null) }
    if (state is State.Loading) loading.value = state.painter

    // Short circuit if the request isn't successful or if it's returned by the memory cache.
    if (state is State.Success) {
        val sketchDrawable = state.drawable.findLastSketchDrawable()
        if (sketchDrawable != null && sketchDrawable.dataFrom != MEMORY_CACHE) {
            // Set the crossfade painter.
            imagePainter.painter = rememberCrossfadePainter(
                key = state,
                start = loading.value,
                end = painter,
                contentScale = imagePainter.contentScale,
                durationMillis = transition.durationMillis,
                fadeStart = state.drawable !is SketchCountBitmapDrawable,
                preferExactIntrinsicSize = transition.preferExactIntrinsicSize
            )
        } else {
            imagePainter.painter = painter
        }
    } else {
        imagePainter.painter = painter
    }
}

/** A simple mutable value holder that avoids recomposition. */
private class ValueHolder<T>(@JvmField var value: T)