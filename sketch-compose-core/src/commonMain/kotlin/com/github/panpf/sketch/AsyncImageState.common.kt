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
package com.github.panpf.sketch

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
import com.github.panpf.sketch.PainterState.Empty
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.internal.AsyncImageSizeResolver
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.target.GenericComposeTarget
import com.github.panpf.sketch.util.difference
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.isEmpty
import com.github.panpf.sketch.util.toScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
expect fun getWindowContainerSize(): IntSize

@Composable
fun rememberAsyncImageState(options: ImageOptions? = null): AsyncImageState {
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) {
        GlobalLifecycle
    } else {
        LocalLifecycleOwner.current.lifecycle
    }
    val containerSize = getWindowContainerSize()
    return remember(lifecycle, inspectionMode, containerSize, options) {
        AsyncImageState(lifecycle, inspectionMode, containerSize, options)
    }
}

@Composable
fun rememberAsyncImageState(optionsLazy: () -> ImageOptions): AsyncImageState {
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) {
        GlobalLifecycle
    } else {
        LocalLifecycleOwner.current.lifecycle
    }
    val containerSize = getWindowContainerSize()
    return remember(lifecycle, inspectionMode, containerSize) {
        val options = optionsLazy?.invoke()
        AsyncImageState(lifecycle, inspectionMode, containerSize, options)
    }
}

@Stable
class AsyncImageState internal constructor(
    private val lifecycle: Lifecycle,
    private val inspectionMode: Boolean,
    private val containerSize: IntSize,
    private val options: ImageOptions?,
) : RememberObserver {

    private val target = AsyncImageTarget()
    private val listener = AsyncImageListener()
    private var lastRequest: ImageRequest? = null
    private val requestManager = ComposeRequestManager(this)
    private var coroutineScope: CoroutineScope? = null
    private var loadImageJob: Job? = null
    private var rememberedCount = 0

    var sketch: Sketch? by mutableStateOf(null)
        internal set
    var request: ImageRequest? by mutableStateOf(null)
        internal set
    private var sourceSize: IntSize? = null
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
        val oldSize = this.size
        if (oldSize != null && oldSize.isEmpty()) {
            // The width or height of oldSize is 0, which means that the width or height of AsyncImage is wrap content.
            // Then only the size set for the first time can be used as the size of the image request,
            // because the first size can accurately represent the width and height of AsyncImage.
            return
        }
        val limitSize = IntSize(
            if (size.width > 0) size.width else containerSize.width,
            if (size.height > 0) size.height else containerSize.height
        )
        this.sourceSize = size
        this.size = limitSize
        this.sizeResolver.sizeState.value = limitSize
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

        requestManager.onRemembered()

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
                    val updatedRequest = request.newBuilder().defaultOptions(mergedOptions).build()
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
                    val lastRequest = this@AsyncImageState.lastRequest
                    if (lastRequest != null) {
                        if (lastRequest.key == request.key) {
                            if (lastRequest != request) {
                                // TODO Test whether onAnimationStart, onAnimationEnd, animatedTransformation will cause this problem
                                val diffImageRequest = lastRequest.difference(request)
                                throw IllegalArgumentException("ImageRequest key is the same but the content is different: $diffImageRequest.")
                            }
                        }
                    }
                    this@AsyncImageState.lastRequest = request
                    cancelLoadImageJob()
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
        (painter as? RememberObserver)?.onForgotten()
        painter = null
        painterState = Empty
        requestManager.onForgotten()
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

    private fun loadImage(
        sketch: Sketch,
        request: ImageRequest,
        @Suppress("UNUSED_PARAMETER") contentScale: ContentScale
    ) {
        // TODO show placeholder in preview mode
        val coroutineScope = coroutineScope ?: return
        val fullRequest = request.newRequest {
            target(target)
        }
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

    internal fun isRemembered(): Boolean = rememberedCount > 0

    override fun toString(): String = "AsyncImageState@${hashCode().toString(16)}"

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

        override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
            this@AsyncImageState.progress = progress
        }

        override fun toString(): String {
            return "AsyncImageListener@${hashCode().toString(16)}"
        }
    }

    private inner class AsyncImageTarget : GenericComposeTarget() {

        override var painter: Painter?
            get() = this@AsyncImageState.painter
            set(newPainter) {
                val oldPainter = this@AsyncImageState.painter
                if (newPainter !== oldPainter) {
                    (oldPainter as? RememberObserver)?.onForgotten()
                    this@AsyncImageState.painter = newPainter
                    (newPainter as? RememberObserver)?.onRemembered()
                }
            }

        override val fitScale: Boolean
            get() = contentScale?.fitScale ?: true


        override fun getRequestManager(): RequestManager = requestManager


        override fun getListener(): Listener = this@AsyncImageState.listener

        override fun getProgressListener(): ProgressListener = this@AsyncImageState.listener

        override fun getLifecycleResolver(): LifecycleResolver =
            LifecycleResolver(this@AsyncImageState.lifecycle)


        override fun getSizeResolver(): SizeResolver = this@AsyncImageState.sizeResolver

        override fun getScaleDecider(): ScaleDecider? =
            this@AsyncImageState.contentScale?.toScale()?.let { ScaleDecider(it) }

        override fun getImageOptions(): ImageOptions? = this@AsyncImageState.options


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

        override fun toString(): String = "AsyncImageTarget@${hashCode().toString(16)}"
    }
}

/**
 * The current painter state of the [AsyncImageState].
 */
@Stable
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