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
import com.github.panpf.sketch.compose.PainterState.Empty
import com.github.panpf.sketch.compose.PainterState.Loading
import com.github.panpf.sketch.compose.internal.AsyncImageScaleDecider
import com.github.panpf.sketch.compose.internal.AsyncImageSizeResolver
import com.github.panpf.sketch.compose.internal.fitScale
import com.github.panpf.sketch.compose.internal.forEachRememberObserver
import com.github.panpf.sketch.compose.internal.toScale
import com.github.panpf.sketch.compose.request.asPainter
import com.github.panpf.sketch.compose.target.GenericComposeTarget
import com.github.panpf.sketch.compose.transition.CrossfadeComposeTransition
import com.github.panpf.sketch.request.Image
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.isDefault
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.transition.CrossfadeTransition
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

    private val target = AsyncImageTarget()
    private val listener = AsyncImageListener()
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
    var request: ImageRequest? by mutableStateOf(null)
        internal set
    var size: IntSize? by mutableStateOf(null)
        private set
    var contentScale: ContentScale? by mutableStateOf(null)
        internal set
    internal var filterQuality = DrawScope.DefaultFilterQuality
    private val sizeResolver = AsyncImageSizeResolver(size)

    var loadState: LoadState? by mutableStateOf(null)
        private set
    var result: ImageResult? by mutableStateOf(null)
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
        if (rememberedCount != 1) return

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
                    val request = (it[0] as ImageRequest).apply { validateRequest(this) }
                    val sketch = it[1] as Sketch
                    val globalImageOptions = sketch.globalImageOptions
                    val mergedOptions = request.defaultOptions?.merged(globalImageOptions)
                    val updatedRequest = request.newBuilder().default(mergedOptions).build()
                    val placeholderImage = updatedRequest.placeholder
                        ?.getImage(sketch, updatedRequest, null)
                    painterState = Loading(placeholderImage?.asPainter())
                }
            }
        } else {
            coroutineScope.launch {
                combine(
                    flows = listOf(
                        snapshotFlow { request }.filterNotNull(),
                        snapshotFlow { sketch }.filterNotNull(),
                        snapshotFlow { contentScale }.filterNotNull()
                    ),
                    transform = { it }
                ).collect {
                    val request = (it[0] as ImageRequest).apply { validateRequest(this) }
                    val sketch = it[1] as Sketch
                    val contentScale = it[2] as ContentScale
                    loadImage(sketch, request, contentScale)
                }
            }
        }
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        // Since AsyncImageState is annotated with @Stable, onForgotten will be executed multiple times,
        // but we only need execute it once
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return

        val coroutineScope = this.coroutineScope ?: return
        cancelLoadImageJob()
        coroutineScope.cancel()
        this.coroutineScope = null
        (_painterState.painter as? RememberObserver)?.onForgotten()
        painterState = Empty
        target.updatePainter(null)  // To trigger setIsDisplayed and onForgotten
    }

    private fun validateRequest(request: ImageRequest) {
        /*
         * Why are listener, progressListener, and target not allowed?
         * Because they are usually created directly when used, this will cause the equals result to be false when ImageRequest is repeatedly created in compose.
         * Then ImageRequest will eventually cause AsyncImage to be reorganized when used as a parameter of AsyncImage
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

    private fun loadImage(sketch: Sketch, request: ImageRequest, contentScale: ContentScale) {
        val coroutineScope = coroutineScope ?: return
        val noSetSize = request.definedOptions.resizeSizeResolver == null
        val noSetScale = request.definedOptions.resizeScaleDecider == null
        val defaultLifecycleResolver = request.lifecycleResolver.isDefault()
        val fullRequest = request.newRequest {
            if (noSetSize) {
                resizeSize(sizeResolver)
            }
            if (noSetScale) {
                resizeScale(AsyncImageScaleDecider(ScaleDecider(contentScale.toScale())))
            }
            if (defaultLifecycleResolver) {
                lifecycle(lifecycle)
            }
            target(target)
            listener(listener)
            progressListener(listener)
            val transitionFactory = request.transitionFactory
            if (transitionFactory is CrossfadeTransition.Factory) {
                transitionFactory(
                    CrossfadeComposeTransition.Factory(
                        durationMillis = transitionFactory.durationMillis,
                        fadeStart = transitionFactory.fadeStart,
                        preferExactIntrinsicSize = transitionFactory.preferExactIntrinsicSize,
                        alwaysUse = transitionFactory.alwaysUse,
                    )
                )
            }
        }
        cancelLoadImageJob()
        loadImageJob = coroutineScope.launch {
            sketch.execute(fullRequest)
        }
    }

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

    private inner class AsyncImageListener : Listener, ProgressListener {

        override fun onStart(request: ImageRequest) {
            this@AsyncImageState.result = null
            this@AsyncImageState.progress = null
            this@AsyncImageState.loadState = LoadState.Started(request)
        }

        override fun onSuccess(request: ImageRequest, result: Success) {
            this@AsyncImageState.result = result
            this@AsyncImageState.loadState = LoadState.Success(request, result)
        }

        override fun onError(request: ImageRequest, error: Error) {
            this@AsyncImageState.result = error
            this@AsyncImageState.loadState = LoadState.Error(request, error)
        }

        override fun onCancel(request: ImageRequest) {
            this@AsyncImageState.loadState = LoadState.Canceled(request)
        }

        override fun onUpdateProgress(
            request: ImageRequest, progress: Progress
        ) {
            this@AsyncImageState.progress = progress
        }

        override fun toString(): String {
            return "AsyncImageListener@${Integer.toHexString(hashCode())}"
        }
    }

    private inner class AsyncImageTarget : GenericComposeTarget() {

        // TODO Compose GIF cannot be played
        override var painter: Painter?
            get() = _painter
            set(newPainter) {
                val oldPainter = _painter
                if (newPainter !== oldPainter) {
                    oldPainter?.forEachRememberObserver { it.onForgotten() }
                    _painter = newPainter
                    newPainter?.forEachRememberObserver { it.onRemembered() }
                }
            }

        override val fitScale: Boolean
            get() = contentScale?.fitScale ?: true

        override fun onStart(requestContext: RequestContext, placeholder: Image?) {
            super.onStart(requestContext, placeholder)
            painterState = Loading(painter)
        }

        override fun onSuccess(requestContext: RequestContext, result: Image) {
            super.onSuccess(requestContext, result)
            painterState = PainterState.Success(painter!!)
        }

        override fun onError(requestContext: RequestContext, error: Image?) {
            super.onError(requestContext, error)
            painterState = PainterState.Error(painter)
        }

        override fun toString(): String {
            return "AsyncImageTarget@${Integer.toHexString(hashCode())}"
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
    ) : PainterState

    /** The request failed due to [ImageResult.Error.throwable]. */
    data class Error(
        override val painter: Painter?,
    ) : PainterState
}