/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.target.GenericComposeTarget
import com.github.panpf.sketch.util.difference
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.isEmpty
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * Get window container size
 *
 * @see com.github.panpf.sketch.compose.core.android.test.AsyncImageStateAndroidTest.testWindowContainerSize
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.AsyncImageStateNonAndroidTest.testWindowContainerSize
 */
@Composable
expect fun windowContainerSize(): IntSize

/**
 * Create and remember [AsyncImageState]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageStateTest.testRememberAsyncImageState
 */
@Composable
fun rememberAsyncImageState(options: ImageOptions? = null): AsyncImageState {
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) GlobalLifecycle else LocalLifecycleOwner.current.lifecycle
    val containerSize = windowContainerSize()
    return remember(inspectionMode, lifecycle, containerSize, options) {
        AsyncImageState(inspectionMode, lifecycle, containerSize, options)
    }
}

/**
 * Create and remember [AsyncImageState]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageStateTest.testRememberAsyncImageState
 */
@Composable
fun rememberAsyncImageState(optionsLazy: () -> ImageOptions): AsyncImageState {
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) GlobalLifecycle else LocalLifecycleOwner.current.lifecycle
    val containerSize = windowContainerSize()
    return remember(inspectionMode, lifecycle, containerSize) {
        val options = optionsLazy.invoke()
        AsyncImageState(inspectionMode, lifecycle, containerSize, options)
    }
}

/**
 * Asynchronous image state
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageStateTest
 */
@Stable
class AsyncImageState internal constructor(
    val inspectionMode: Boolean,
    val lifecycle: Lifecycle,
    val containerSize: IntSize,
    val options: ImageOptions?,
) : RememberObserver {

    internal val target = AsyncImageTarget(this)
    private val listener = AsyncImageListener(this)
    private val requestManager = ComposeRequestManager(this)
    private var sourceSize: IntSize? = null
    private var lastRequest: ImageRequest? = null
    private var loadImageJob: Job? = null
    private var coroutineScope: CoroutineScope? = null
    private var rememberedCount: Int = 0

    var sketch: Sketch? by mutableStateOf(null)
        internal set
    var request: ImageRequest? by mutableStateOf(null)
        internal set
    var size: IntSize? by mutableStateOf(null)
        private set
    var contentScale: ContentScale? by mutableStateOf(null)
        internal set
    var filterQuality = DrawScope.DefaultFilterQuality
        internal set
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
                    val newDefaultOptions = request.defaultOptions?.merged(globalImageOptions)
                    val updatedRequest = request.newRequest {
                        merge(options)
                        defaultOptions(newDefaultOptions)
                    }
                    val placeholderImage = updatedRequest.placeholder
                        ?.getImage(sketch, updatedRequest, null)
                    val painter1 = placeholderImage?.asPainter()
                    painterState = Loading(painter1)
                    painter = painter1
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

    override fun toString(): String = "AsyncImageState@${toHexString()}"

    class AsyncImageTarget(val state: AsyncImageState) : GenericComposeTarget() {

        override var painter: Painter?
            get() = state.painter
            set(newPainter) {
                val oldPainter = state.painter
                if (newPainter !== oldPainter) {
                    (oldPainter as? RememberObserver)?.onForgotten()
                    state.painter = newPainter
                    (newPainter as? RememberObserver)?.onRemembered()
                }
            }

        override val fitScale: Boolean
            get() = state.contentScale?.fitScale ?: true


        override fun getRequestManager(): RequestManager = state.requestManager


        override fun getListener(): Listener = state.listener

        override fun getProgressListener(): ProgressListener = state.listener

        override fun getLifecycleResolver(): LifecycleResolver =
            LifecycleResolver(state.lifecycle)


        override fun getSizeResolver(): SizeResolver = state.sizeResolver

        override fun getScaleDecider(): ScaleDecider? =
            state.contentScale?.toScale()?.let { ScaleDecider(it) }

        override fun getImageOptions(): ImageOptions? = state.options


        override fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) {
            super.onStart(sketch, request, placeholder)
            state.painterState = Loading(painter)
        }

        override fun onSuccess(sketch: Sketch, request: ImageRequest, result: Image) {
            super.onSuccess(sketch, request, result)
            state.painterState = PainterState.Success(painter!!)
        }

        override fun onError(sketch: Sketch, request: ImageRequest, error: Image?) {
            super.onError(sketch, request, error)
            state.painterState = PainterState.Error(painter)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as AsyncImageTarget
            if (state != other.state) return false
            return true
        }

        override fun hashCode(): Int = state.hashCode()

        override fun toString(): String = "AsyncImageTarget($state)"
    }

    class AsyncImageListener(val state: AsyncImageState) : Listener, ProgressListener {

        override fun onStart(request: ImageRequest) {
            state.result = null
            state.progress = null
            state.loadState = LoadState.Started(request)
        }

        override fun onSuccess(request: ImageRequest, result: Success) {
            state.result = result
            state.loadState = LoadState.Success(request, result)
        }

        override fun onError(request: ImageRequest, error: Error) {
            state.result = error
            state.loadState = LoadState.Error(request, error)
        }

        override fun onCancel(request: ImageRequest) {
            state.loadState = LoadState.Canceled(request)
        }

        override fun onUpdateProgress(request: ImageRequest, progress: Progress) {
            state.progress = progress
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as AsyncImageListener
            if (state != other.state) return false
            return true
        }

        override fun hashCode(): Int = state.hashCode()

        override fun toString(): String = "AsyncImageListener($state)"
    }
}