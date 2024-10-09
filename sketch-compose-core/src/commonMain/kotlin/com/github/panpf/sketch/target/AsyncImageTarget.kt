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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.PainterState.Empty
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.request.internal.AsyncImageListener
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.resize.ScaleDecider
import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.isEmpty
import com.github.panpf.sketch.util.toScale

/**
 * A [Target] that handles setting [Image] on an [AsyncImage]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.AsyncImageTargetTest
 */
class AsyncImageTarget(
    private val lifecycle: Lifecycle,
    private val options: ImageOptions?,
    private val containerSize: IntSize,
    private val listener: AsyncImageListener?
) : GenericComposeTarget() {

    private var sourceSize: IntSize? = null
    private val sizeResolver = AsyncImageSizeResolver()
    private val requestManager = ComposeRequestManager()

    var painterMutableState: MutableState<Painter?> = mutableStateOf(null)
    var painterStateMutableState: MutableState<PainterState> = mutableStateOf(Empty)
    var contentScaleMutableState: MutableState<ContentScale?> = mutableStateOf(null)
    var sizeMutableState: MutableState<IntSize?> = mutableStateOf(null)

    override var painter: Painter?
        get() = painterMutableState.value
        set(newPainter) {
            val oldPainter = painterMutableState.value
            if (newPainter !== oldPainter) {
                (oldPainter as? RememberObserver)?.onForgotten()
                painterMutableState.value = newPainter
                (newPainter as? RememberObserver)?.onRemembered()
            }
        }

    override val fitScale: Boolean
        get() = contentScaleMutableState.value?.fitScale ?: true

    fun updateSize(size: IntSize): Boolean {
        val oldSize = this.sizeMutableState.value
        if (oldSize != null && oldSize.isEmpty()) {
            // The width or height of oldSize is 0, which means that the width or height of AsyncImage is wrap content.
            // Then only the size set for the first time can be used as the size of the image request,
            // because the first size can accurately represent the width and height of AsyncImage.
            return false
        }
        val limitSize = IntSize(
            if (size.width > 0) size.width else containerSize.width,
            if (size.height > 0) size.height else containerSize.height
        )
        this.sourceSize = size
        this.sizeMutableState.value = limitSize
        this.sizeResolver.sizeState.value = limitSize
        return true
    }

    fun onRemembered() {
        requestManager.onRemembered()
    }

    fun onForgotten() {
        (painter as? RememberObserver)?.onForgotten()
        painterMutableState.value = null
        painterStateMutableState.value = Empty
        requestManager.onForgotten()
    }

    override fun getRequestManager(): RequestManager = requestManager


    override fun getListener(): Listener? = listener

    override fun getProgressListener(): ProgressListener? = listener

    override fun getLifecycleResolver(): LifecycleResolver =
        LifecycleResolver(lifecycle)


    override fun getSizeResolver(): SizeResolver = sizeResolver

    override fun getScaleDecider(): ScaleDecider? =
        contentScaleMutableState.value?.toScale()?.let { ScaleDecider(it) }

    override fun getImageOptions(): ImageOptions? = options


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
        if (options != other.options) return false
        if (containerSize != other.containerSize) return false
        if (listener != other.listener) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lifecycle.hashCode()
        result = 31 * result + (options?.hashCode() ?: 0)
        result = 31 * result + containerSize.hashCode()
        result = 31 * result + (listener?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AsyncImageTarget(lifecycle=$lifecycle, options=$options, containerSize=$containerSize, listener=$listener)"
    }
}