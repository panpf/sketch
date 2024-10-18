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

package com.github.panpf.sketch.target

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.LoadState
import com.github.panpf.sketch.request.Progress
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.target.internal.AsyncImageListener
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.isEmpty
import com.github.panpf.sketch.util.toLogString
import com.github.panpf.sketch.util.toScale

/**
 * A [Target] that handles setting [Image] on an [AsyncImage]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.AsyncImageTargetTest
 */
class AsyncImageTarget(
    private val lifecycle: Lifecycle,
    private val imageOptions: ImageOptions?,
    private val windowContainerSize: IntSize,
) : GenericComposeTarget() {

    private val listener = AsyncImageListener()
    private val sizeResolver = AsyncImageSizeResolver()
    private val requestManager = ComposeRequestManager()

    private val sizeMutableState: MutableState<IntSize?> = mutableStateOf(null)
    private val painterMutableState: MutableState<Painter?> = mutableStateOf(null)
    private val painterStateMutableState: MutableState<PainterState?> = mutableStateOf(null)

    val contentScaleMutableState: MutableState<ContentScale?> = mutableStateOf(null)
    val filterQualityMutableState: MutableState<FilterQuality?> = mutableStateOf(null)

    val sizeState: State<IntSize?> = sizeMutableState
    val painterState: State<Painter?> = painterMutableState
    val painterStateState: State<PainterState?> = painterStateMutableState
    val loadStateState: State<LoadState?> = listener.loadStateState
    val resultState: State<ImageResult?> = listener.resultState
    val progressState: State<Progress?> = listener.progressState

    override val painter: Painter?
        get() = painterMutableState.value

    override val fitScale: Boolean
        get() = contentScaleMutableState.value?.fitScale ?: true

    override val filterQuality: FilterQuality
        get() = filterQualityMutableState.value ?: super.filterQuality

    init {
        require(!windowContainerSize.isEmpty()) {
            "containerSize must not be empty"
        }
    }

    override fun setPainter(painter: Painter?) {
        val oldPainter = painterMutableState.value
        if (painter !== oldPainter) {
            (oldPainter as? RememberObserver)?.onForgotten()
            painterMutableState.value = painter
            (painter as? RememberObserver)?.onRemembered()
        }
    }

    fun setPreviewImage(sketch: Sketch, request: ImageRequest, image: Image?) {
        onStart(sketch, request, placeholder = image)
        listener.onStart(request)
    }

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

    fun onRemembered() {
        (painter as? RememberObserver)?.onRemembered()
        requestManager.onRemembered()
    }

    fun onForgotten() {
        (painter as? RememberObserver)?.onForgotten()
        requestManager.onForgotten()
    }

    override fun getRequestManager(): ComposeRequestManager = requestManager


    override fun getListener(): AsyncImageListener = listener

    override fun getProgressListener(): AsyncImageListener = listener

    override fun getLifecycleResolver(): LifecycleResolver = LifecycleResolver(lifecycle)


    override fun getSizeResolver(): AsyncImageSizeResolver = sizeResolver

    override fun getScaleDecider(): ScaleDecider? =
        contentScaleMutableState.value?.toScale()?.let { ScaleDecider(it) }

    override fun getImageOptions(): ImageOptions? = imageOptions


    override fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) {
        super.onStart(sketch, request, placeholder)
        painterStateMutableState.value = Loading(painter)
    }

    override fun onSuccess(sketch: Sketch, request: ImageRequest, result: Image) {
        super.onSuccess(sketch, request, result)
        painterStateMutableState.value = PainterState.Success(painter!!)
    }

    override fun onError(sketch: Sketch, request: ImageRequest, error: Image?) {
        super.onError(sketch, request, error)
        painterStateMutableState.value = PainterState.Error(painter)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AsyncImageTarget
        if (lifecycle != other.lifecycle) return false
        if (imageOptions != other.imageOptions) return false
        if (windowContainerSize != other.windowContainerSize) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lifecycle.hashCode()
        result = 31 * result + (imageOptions?.hashCode() ?: 0)
        // Because windowContainerSize are value classes, they will be replaced by long.
        // Long will lose precision when converting hashCode, causing the hashCode generated by different srcOffset and srcSize to be the same.
        result = 31 * result + windowContainerSize.toString().hashCode()
        return result
    }

    override fun toString(): String {
        return "AsyncImageTarget(lifecycle=$lifecycle, options=$imageOptions, containerSize=${windowContainerSize.toLogString()})"
    }
}