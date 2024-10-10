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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.util.difference
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.windowContainerSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

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
 * The state that [AsyncImage] and [AsyncImagePainter] depend on is used to load images and manage requests and states.
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

    private var lastRequest: ImageRequest? = null
    private var loadImageJob: Job? = null
    private var coroutineScope: CoroutineScope? = null
    private var rememberedCount: Int = 0

    internal val target = AsyncImageTarget(lifecycle, options, containerSize)

    var sketch: Sketch? by mutableStateOf(null)
        internal set
    var request: ImageRequest? by mutableStateOf(null)
        internal set
    var contentScale: ContentScale? by target.contentScaleMutableState
        internal set
    var filterQuality: FilterQuality? by target.filterQualityMutableState
        internal set

    val size: IntSize? by target.sizeState
    val painter: Painter? by target.painterState
    val painterState: PainterState by target.painterStateState
    val result: ImageResult? by target.resultState
    val loadState: LoadState? by target.loadStateState
    val progress: Progress? by target.progressState

    fun setSize(size: IntSize) {
        target.setSize(size)
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

        target.onRemembered()

        if (inspectionMode) {
            loadPreviewImage(coroutineScope)
        } else {
            launchImageLoadTask(coroutineScope)
        }
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        // Since AsyncImageState is annotated with @Stable, onForgotten will be executed multiple times,
        // but we only need execute it once
        if (rememberedCount <= 0) return
        rememberedCount = (rememberedCount - 1).coerceAtLeast(0)
        if (rememberedCount != 0) return

        val coroutineScope = this.coroutineScope ?: return
        cancelLoadImageJob()
        coroutineScope.cancel()
        this.coroutineScope = null

        target.onForgotten()
    }

    private fun loadPreviewImage(coroutineScope: CoroutineScope) {
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
                val previewPainter = placeholderImage?.asPainter()
                target.setPreviewPainter(previewPainter)
            }
        }
    }

    private fun launchImageLoadTask(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            combine(
                flows = listOf(
                    snapshotFlow { request }.filterNotNull(),
                    snapshotFlow { sketch }.filterNotNull(),
                    snapshotFlow { contentScale }.filterNotNull(),
                    snapshotFlow { filterQuality }.filterNotNull(),
                ),
                transform = { it }
            ).collect {
                val request = (it[0] as ImageRequest).apply { validateRequest(this) }
                val sketch = it[1] as Sketch
                val contentScale = it[2] as ContentScale
                val filterQuality = it[3] as FilterQuality
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
                loadImage(sketch, request, contentScale, filterQuality)
            }
        }
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
        @Suppress("UNUSED_PARAMETER") contentScale: ContentScale,
        @Suppress("UNUSED_PARAMETER") filterQuality: FilterQuality,
    ) {
        // No need to care about contentScale and filterQuality since they are managed by AsyncImageTarget
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
        val filterQuality = filterQuality ?: return
        coroutineScope ?: return
        cancelLoadImageJob()
        loadImage(sketch, request, contentScale, filterQuality)
    }

    private fun cancelLoadImageJob() {
        val loadImageJob = loadImageJob
        if (loadImageJob != null && loadImageJob.isActive) {
            loadImageJob.cancel()
        }
    }

    override fun toString(): String = "AsyncImageState@${toHexString()}"
}