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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.panpf.sketch.request.GlobalLifecycle
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.target.AsyncImageTarget
import com.github.panpf.sketch.util.RememberedCounter
import com.github.panpf.sketch.util.difference
import com.github.panpf.sketch.util.screenSize
import com.github.panpf.sketch.util.toHexString
import com.github.panpf.sketch.util.toIntSize
import com.github.panpf.sketch.util.windowContainerSize
import kotlinx.coroutines.CoroutineExceptionHandler
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
    val context = LocalPlatformContext.current
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) GlobalLifecycle else LocalLifecycleOwner.current.lifecycle
    val windowContainerSize = windowContainerSize()
    return remember(context, inspectionMode, lifecycle, options) {
        AsyncImageState(context, inspectionMode, lifecycle, options)
    }.apply {
        this@apply.windowContainerSize = windowContainerSize
    }
}

/**
 * Create and remember [AsyncImageState]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageStateTest.testRememberAsyncImageState
 */
@Composable
fun rememberAsyncImageState(optionsLazy: () -> ImageOptions): AsyncImageState {
    val context = LocalPlatformContext.current
    val inspectionMode = LocalInspectionMode.current
    val lifecycle = if (inspectionMode) GlobalLifecycle else LocalLifecycleOwner.current.lifecycle
    val windowContainerSize = windowContainerSize()
    return remember(context, inspectionMode, lifecycle) {
        val options = optionsLazy.invoke()
        AsyncImageState(context, inspectionMode, lifecycle, options)
    }.apply {
        this@apply.windowContainerSize = windowContainerSize
    }
}

/**
 * The state that [AsyncImage] and [AsyncImagePainter] depend on is used to load images and manage requests and states.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageStateTest
 */
@Stable
class AsyncImageState internal constructor(
    val context: PlatformContext,
    val inspectionMode: Boolean,
    val lifecycle: Lifecycle,
    val imageOptions: ImageOptions?,
) : RememberObserver {

    internal var currentRequest: ImageRequest? = null
    internal var currentTarget: AsyncImageTarget? = null
    internal var loadImageJob: Job? = null
    internal var coroutineScope: CoroutineScope? = null
    internal val rememberedCounter: RememberedCounter = RememberedCounter()
    internal var coroutineExceptionHandler: CoroutineExceptionHandler? = null

    internal val sizeResolver = AsyncImageSizeResolver()
    internal val requestManager = ComposeRequestManager()
    internal val contentScaleMutableState: MutableState<ContentScale?> = mutableStateOf(null)
    internal val alignmentMutableState: MutableState<Alignment?> = mutableStateOf(null)
    internal val filterQualityMutableState: MutableState<FilterQuality?> = mutableStateOf(null)
    internal val sizeMutableState: MutableState<IntSize?> = mutableStateOf(null)
    internal val painterMutableState: MutableState<Painter?> = mutableStateOf(null)
    internal val painterStateMutableState: MutableState<PainterState?> = mutableStateOf(null)
    internal val loadStateMutableState: MutableState<LoadState?> = mutableStateOf(null)
    internal val resultMutableState: MutableState<ImageResult?> = mutableStateOf(null)
    internal val progressMutableState: MutableState<Progress?> = mutableStateOf(null)
    internal val sizeState: State<IntSize?> = sizeMutableState
    internal val painterStateState: State<PainterState?> = painterStateMutableState
    internal val loadStateState: State<LoadState?> = loadStateMutableState
    internal val resultState: State<ImageResult?> = resultMutableState
    internal val progressState: State<Progress?> = progressMutableState

    var windowContainerSize: IntSize = context.screenSize().toIntSize()
        set(value) {
            val finalValue = if (value.width < 100 || value.height < 100) {
                IntSize(value.width.coerceAtLeast(100), value.height.coerceAtLeast(100))
            } else {
                value
            }
            field = finalValue
        }

    /*
     * The default values for sketch, request, contentScale, and filterQuality are all null.
     * This ensures that a load image request will only be triggered after they are all initialized.
     */
    var sketch: Sketch? by mutableStateOf(null)
        internal set
    var request: ImageRequest? by mutableStateOf(null)
        internal set
    var contentScale: ContentScale? by contentScaleMutableState
        internal set
    var alignment: Alignment? by alignmentMutableState
        internal set
    var filterQuality: FilterQuality? by filterQualityMutableState
        internal set

    val size: IntSize? by sizeState
    val painter: Painter? by painterMutableState
    val painterState: PainterState? by painterStateState
    val result: ImageResult? by resultState
    val loadState: LoadState? by loadStateState
    val progress: Progress? by progressState

    var onPainterState: ((PainterState) -> Unit)? = null
    var onLoadState: ((LoadState) -> Unit)? = null

    fun setSize(size: IntSize) {
        // If the width or height is 0, it means that the constraint of the component is to wrap content.
        // In this case, the size of the window container can be used instead.
        val limitedSize = IntSize(
            width = if (size.width > 0) size.width else windowContainerSize.width,
            height = if (size.height > 0) size.height else windowContainerSize.height
        )
        this.sizeMutableState.value = limitedSize
        this.sizeResolver.sizeState.value = limitedSize
    }

    /**
     * Note: When using AsyncImageState externally,
     * do not actively call its onRemembered method because this will destroy the remembered count.
     */
    override fun onRemembered() {
        if (!rememberedCounter.remember()) return

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            val coroutineExceptionHandler = coroutineExceptionHandler
            if (coroutineExceptionHandler != null) {
                coroutineExceptionHandler.handleException(coroutineContext, throwable)
            } else {
                throw throwable
            }
        }
        val coroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + exceptionHandler)
        this.coroutineScope = coroutineScope

        currentTarget?.onRemembered()
        requestManager.onRemembered()
        launchLoadImageTask(coroutineScope)
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        if (!rememberedCounter.forget()) return

        cancelLoadImageJob()
        requestManager.onForgotten()
        currentTarget?.onForgotten()

        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun launchLoadImageTask(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            combine(
                flows = listOf(
                    snapshotFlow { sketch }.filterNotNull(),
                    snapshotFlow { request }.filterNotNull(),
                    snapshotFlow { contentScale }.filterNotNull(),
                    snapshotFlow { alignment }.filterNotNull(),
                    snapshotFlow { filterQuality }.filterNotNull(),
                ),
                transform = { it }
            ).collect {
                val sketch = it[0] as Sketch
                val request = it[1] as ImageRequest
                val contentScale = it[2] as ContentScale
                val alignment = it[3] as Alignment
                val filterQuality = it[4] as FilterQuality
                checkRequest(request, this@AsyncImageState.currentRequest)
                this@AsyncImageState.currentRequest = request

                if (inspectionMode) {
                    loadPreviewImage(sketch, request, contentScale, alignment, filterQuality)
                } else {
                    cancelLoadImageJob()
                    loadImage(sketch, request, contentScale, alignment, filterQuality)
                }
            }
        }
    }

    private fun checkRequest(request: ImageRequest, lastRequest: ImageRequest?) {
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

        /*
         * The keys of the two ImageRequests are equal but the equals are not equal.
         * This is because the custom attributes set to the ImageRequest do not implement the equals method,
         * resulting in the equals of each created ImageRequest being unequal,
         * which eventually triggers reorganization and reduces performance,
         * so an exception must be thrown to remind the developer to solve problems
         */
        if (lastRequest != null && lastRequest.key == request.key && lastRequest != request) {
            val differenceInfo = lastRequest.difference(request)
            throw IllegalArgumentException("Compared with the previous ImageRequest key is the same but the content is different. $differenceInfo.")
        }
    }

    private fun setCurrentTarget(newTarget: AsyncImageTarget?) {
        val oldTarget = currentTarget
        if (oldTarget === newTarget) return
        oldTarget?.imageState = null
        oldTarget?.onForgotten()
        currentTarget = newTarget
        if (newTarget != null && rememberedCounter.isRemembered) {
            newTarget.onRemembered()
        }
    }

    /**
     * Note: The target uses contentScale and filterQuality,
     *  so you must wait for them to be initialized before loading the image.
     */
    private fun loadPreviewImage(
        sketch: Sketch,
        request: ImageRequest,
        @Suppress("UNUSED_PARAMETER") contentScale: ContentScale,
        @Suppress("UNUSED_PARAMETER") alignment: Alignment,
        @Suppress("UNUSED_PARAMETER") filterQuality: FilterQuality,
    ) {
        val globalImageOptions = sketch.globalImageOptions
        val newDefaultOptions = request.defaultOptions?.merged(globalImageOptions)
        val updatedRequest = request.newRequest {
            merge(imageOptions)
            defaultOptions(newDefaultOptions)
        }
        val placeholderImage = updatedRequest.placeholder
            ?.getImage(sketch, updatedRequest, null)
        val target = AsyncImageTarget(this@AsyncImageState)
        setCurrentTarget(target)
        target.setPreviewImage(sketch, request, placeholderImage)
    }

    /**
     * Note: The target uses contentScale and filterQuality,
     *  so you must wait for them to be initialized before loading the image.
     */
    private fun loadImage(
        sketch: Sketch,
        request: ImageRequest,
        @Suppress("UNUSED_PARAMETER") contentScale: ContentScale,
        @Suppress("UNUSED_PARAMETER") alignment: Alignment,
        @Suppress("UNUSED_PARAMETER") filterQuality: FilterQuality,
    ) {
        // No need to care about contentScale and filterQuality since they are managed by AsyncImageTarget
        val coroutineScope = coroutineScope ?: return
        currentTarget?.imageState = null
        val target = AsyncImageTarget(this@AsyncImageState)
        setCurrentTarget(target)
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
        val alignment = alignment ?: return
        val filterQuality = filterQuality ?: return
        coroutineScope ?: return
        cancelLoadImageJob()
        loadImage(sketch, request, contentScale, alignment, filterQuality)
    }

    private fun cancelLoadImageJob() {
        val loadImageJob = loadImageJob
        if (loadImageJob != null && loadImageJob.isActive) {
            loadImageJob.cancel()
        }
    }

    override fun toString(): String = "AsyncImageState@${toHexString()}"
}