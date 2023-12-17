/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.compose

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.LoadState.Canceled
import com.github.panpf.sketch.compose.LoadState.Started
import com.github.panpf.sketch.compose.PainterState.Empty
import com.github.panpf.sketch.compose.PainterState.Loading
import com.github.panpf.sketch.compose.internal.AsyncImageDisplayTarget
import com.github.panpf.sketch.compose.internal.AsyncImageScaleDecider
import com.github.panpf.sketch.compose.internal.AsyncImageSizeResolver
import com.github.panpf.sketch.compose.internal.CrossfadePainter
import com.github.panpf.sketch.compose.internal.toScale
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.isDefault
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.target.DisplayTarget
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.transition.TransitionDisplayTarget
import com.github.panpf.sketch.util.iterateSketchCountBitmapDrawable
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun rememberAsyncImageState(): AsyncImageState {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val inspectionMode = LocalInspectionMode.current
    return remember { AsyncImageState(lifecycle, inspectionMode) }
}

@Stable
class AsyncImageState internal constructor(
    private val lifecycle: Lifecycle,
    private val inspectionMode: Boolean,
) : RememberObserver {

    private val listener = MyListener()
    private val target = AsyncImageDisplayTarget(MyAsyncImageDisplayTarget())
    private val progressListener = MyProgressListener()
    private var coroutineScope: CoroutineScope? = null
    private var loadImageJob: Job? = null
    private var rememberedCount = 0
    private var _painterState: PainterState = Empty
        set(value) {
            field = value
            painterState = value
        }
    private var _painter: Painter? = null
        set(value) {
            field = value
            painter = value
        }

    var sketch: Sketch? by mutableStateOf(null)
        internal set
    var request: DisplayRequest? by mutableStateOf(null)
        internal set
    var size: IntSize? by mutableStateOf(null)
        private set
    var contentScale: ContentScale? by mutableStateOf(null)
        internal set
    internal var transform = DefaultTransform
    internal var onPainterState: ((PainterState) -> Unit)? = null
    internal var filterQuality = DrawScope.DefaultFilterQuality
    private val sizeResolver = AsyncImageSizeResolver(size)

    var loadState: LoadState? by mutableStateOf(null)
        private set
    var result: DisplayResult? by mutableStateOf(null)
        private set
    var progress: Progress? by mutableStateOf(null)
        private set
    var painterState: PainterState by mutableStateOf(Empty)
        private set
    var painter: Painter? by mutableStateOf(null)
        private set

    fun setSize(size: IntSize) {
        this.size = size
        this.sizeResolver.sizeState.value = size
    }

    /**
     * Note: When using AsyncImageState externally,
     * do not actively call its onRemembered method because this will destroy the rememberedCount count.
     */
    override fun onRemembered() {
        // Since AsyncImageState is annotated with @Stable, onRemembered will be executed multiple times,
        // but we only need execute it once
        rememberedCount++
        if (rememberedCount > 1) return

        if (this.coroutineScope != null) return
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        this.coroutineScope = coroutineScope

        if (inspectionMode) {
            coroutineScope.launch {
                combine(
                    flows = listOf(
                        snapshotFlow { request }.filterNotNull(),
                        snapshotFlow { sketch }.filterNotNull(),
                    ),
                    transform = { it }
                ).collect {
                    val request = (it[0] as DisplayRequest).apply { validateRequest(this) }
                    val sketch = it[1] as Sketch
                    val globalImageOptions = sketch.globalImageOptions
                    val mergedOptions = request.defaultOptions?.merged(globalImageOptions)
                    val updatedRequest = request.newBuilder().default(mergedOptions).build()
                    val placeholderDrawable = updatedRequest.placeholder
                        ?.getDrawable(sketch, updatedRequest, null)
                    painterState = Loading(placeholderDrawable?.toPainter())
                }
            }
        } else {
            coroutineScope.launch {
                combine(
                    flows = listOf(
                        snapshotFlow { request }.filterNotNull(),
                        snapshotFlow { sketch }.filterNotNull(),
                        snapshotFlow { contentScale }
                    ),
                    transform = { it }
                ).collect {
                    val request = (it[0] as DisplayRequest).apply { validateRequest(this) }
                    val sketch = it[1] as Sketch
                    val contentScale = it[2] as ContentScale
                    loadImage(sketch, request, contentScale)
                }
            }
        }
    }

    private fun validateRequest(request: DisplayRequest) {
        /*
         * Why are listener, progressListener, and target not allowed?
         * Because they are usually created directly when used, this will cause the equals result to be false when DisplayRequest is repeatedly created in compose.
         * Then DisplayRequest will eventually cause AsyncImage to be reorganized when used as a parameter of AsyncImage
         */
        require(request.listener == null) {
            "listener is not supported in compose, please use AsyncImageState.loadState instead"
        }
        require(request.progressListener == null) {
            "progressListener is not supported in compose, please use AsyncImageState.progress instead"
        }
        require(request.target == null) {
            "target is not supported in compose"
        }
    }

    private fun loadImage(sketch: Sketch, request: DisplayRequest, contentScale: ContentScale?) {
        val coroutineScope = coroutineScope ?: return
        val noSetSize = request.definedOptions.resizeSizeResolver == null
        val noSetScale = request.definedOptions.resizeScaleDecider == null
        val defaultLifecycleResolver = request.lifecycleResolver.isDefault()
        if (noSetScale && contentScale == null) {
            return
        }
        val fullRequest = request.newDisplayRequest {
            if (noSetSize) {
                resizeSize(sizeResolver)
            }
            if (noSetScale && contentScale != null) {
                resizeScale(AsyncImageScaleDecider(ScaleDecider(contentScale.toScale())))
            }
            if (defaultLifecycleResolver) {
                lifecycle(lifecycle)
            }
            target(target)
            listener(listener)
            progressListener(progressListener)
        }
        cancelLoadImageJob()
        loadImageJob = coroutineScope.launch {
            sketch.execute(fullRequest)
        }
    }

    private fun updateState(input: PainterState) {
        val previous = _painterState
        val current = transform(input)
        _painterState = current
        _painter = maybeNewCrossfadePainter(previous, current) ?: current.painter

        // Manually forget and remember the old/new painters if we're already remembered.
        if (coroutineScope != null && previous.painter !== current.painter) {
            (previous.painter as? RememberObserver)?.onForgotten()
            (current.painter as? RememberObserver)?.onRemembered()
            updateDisplayed(previous.painter, current.painter)
        }

        // Notify the state listener.
        onPainterState?.invoke(current)
    }

    /** Create and return a [CrossfadePainter] if requested. */
    private fun maybeNewCrossfadePainter(
        previous: PainterState,
        current: PainterState
    ): CrossfadePainter? {
        // We can only invoke the transition factory if the state is success or error.
        val result = when (current) {
            is PainterState.Success -> current.result
            is PainterState.Error -> current.result
            else -> return null
        }

        // Invoke the transition factory and wrap the painter in a `CrossfadePainter` if it returns a `CrossfadeTransformation`.
        val transition =
            result.request.transitionFactory?.create(fakeTransitionTarget, result, true)
        return if (transition is CrossfadeTransition) {
            CrossfadePainter(
                start = previous.painter.takeIf { previous is Loading },
                end = current.painter,
                contentScale = contentScale!!,
                durationMillis = transition.durationMillis,
                fadeStart = transition.fadeStart,
                preferExactIntrinsicSize = transition.preferExactIntrinsicSize
            )
        } else {
            null
        }
    }

    private fun updateDisplayed(oldPainter: Painter?, newPainter: Painter?) {
        newPainter?.takeIf { it is DrawablePainter }
            ?.let { it as DrawablePainter }
            ?.drawable?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(true, "AsyncImageState")
            }
        oldPainter?.takeIf { it is DrawablePainter }
            ?.let { it as DrawablePainter }
            ?.drawable?.iterateSketchCountBitmapDrawable {
                it.countBitmap.setIsDisplayed(false, "AsyncImageState")
            }
    }

    /**
     * Convert this [Drawable] into a [Painter] using Compose primitives if possible.
     *
     * Very important, updateDisplayed() needs to set setIsDisplayed to keep SketchDrawable, SketchStateDrawable
     */
    private fun Drawable.toPainter() = DrawablePainter(mutate())
    // Drawables from Sketch contain reference counting and therefore cannot be converted to the lower level Painter
//        when (this) {
//        is SketchDrawable -> DrawablePainter(mutate())
//        is SketchStateDrawable -> DrawablePainter(mutate())
//        is BitmapDrawable -> BitmapPainter(bitmap.asImageBitmap(), filterQuality = filterQuality)
//        is ColorDrawable -> ColorPainter(Color(color))
//        else -> DrawablePainter(mutate())
//    }

    fun restart() {
        val request = request ?: return
        val sketch = sketch ?: return
        val contentScale = contentScale ?: return
        coroutineScope ?: return
        cancelLoadImageJob()
        loadImage(sketch, request, contentScale)
    }

    private fun cancelLoadImageJob() {
        val loadImageJob = loadImageJob
        if (loadImageJob != null && loadImageJob.isActive) {
            loadImageJob.cancel()
        }
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        // Since AsyncImageState is annotated with @Stable, onForgotten will be executed multiple times,
        // but we only need execute it once
        rememberedCount = (rememberedCount - 1).coerceAtLeast(0)
        if (rememberedCount > 0) return

        val coroutineScope = this.coroutineScope ?: return
        cancelLoadImageJob()
        coroutineScope.cancel()
        this.coroutineScope = null
        (_painterState.painter as? RememberObserver)?.onForgotten()
        updateState(Empty)
    }

    private inner class MyListener : Listener<DisplayRequest, Success, Error> {
        override fun onStart(request: DisplayRequest) {
            this@AsyncImageState.result = null
            this@AsyncImageState.progress = null
            this@AsyncImageState.loadState = Started
        }

        override fun onSuccess(request: DisplayRequest, result: Success) {
            this@AsyncImageState.result = result
            this@AsyncImageState.loadState = LoadState.Success
            updateState(PainterState.Success(result.drawable.toPainter(), result))
        }

        override fun onError(request: DisplayRequest, result: Error) {
            this@AsyncImageState.result = result
            this@AsyncImageState.loadState = LoadState.Error
            updateState(PainterState.Error(result.drawable?.toPainter(), result))
        }

        override fun onCancel(request: DisplayRequest) {
            this@AsyncImageState.result = null
            this@AsyncImageState.loadState = Canceled
        }
    }

    private inner class MyProgressListener : ProgressListener<DisplayRequest> {
        override fun onUpdateProgress(
            request: DisplayRequest, totalLength: Long, completedLength: Long
        ) {
            progress = Progress(totalLength = totalLength, completedLength = completedLength)
        }
    }

    private inner class MyAsyncImageDisplayTarget : DisplayTarget {

        override val supportDisplayCount: Boolean = true

        override fun onStart(placeholder: Drawable?) {
            updateState(Loading(placeholder?.toPainter()))
        }
    }

    companion object {
        /**
         * A state transform that does not modify the state.
         */
        val DefaultTransform: (PainterState) -> PainterState = { it }
    }
}

/**
 * The current state of the [DisplayRequest].
 */
enum class LoadState {
    Started, Success, Error, Canceled
}

/**
 * The current download progress of the [DisplayRequest].
 */
data class Progress(val totalLength: Long, val completedLength: Long) {
    val decimalProgress: Float by lazy {
        if (totalLength > 0) completedLength.toFloat() / totalLength else 0f
    }
}

/**
 * The current painter state of the [AsyncImageState].
 */
sealed interface PainterState {

    /** The current painter being drawn by [AsyncImagePainter]. */
    val painter: Painter?

    /** The request has not been started. */
    data object Empty : PainterState {
        override val painter: Painter? get() = null
    }

    /** The request is in-progress. */
    data class Loading(
        override val painter: Painter?,
    ) : PainterState

    /** The request was successful. */
    data class Success(
        override val painter: Painter,
        val result: DisplayResult.Success,
    ) : PainterState

    /** The request failed due to [DisplayResult.Error.throwable]. */
    data class Error(
        override val painter: Painter?,
        val result: DisplayResult.Error,
    ) : PainterState
}

private val fakeTransitionTarget = object : TransitionDisplayTarget {
    override val drawable: Drawable? get() = null
    override val supportDisplayCount: Boolean = true
}