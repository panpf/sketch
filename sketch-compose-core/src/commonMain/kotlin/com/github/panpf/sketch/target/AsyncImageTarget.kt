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

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult.Error
import com.github.panpf.sketch.request.ImageResult.Success
import com.github.panpf.sketch.request.LifecycleResolver
import com.github.panpf.sketch.request.internal.ComposeRequestManager
import com.github.panpf.sketch.resize.AsyncImageSizeResolver
import com.github.panpf.sketch.target.internal.AsyncImageListener

/**
 * A [Target] that handles setting [Image] on an [AsyncImage]
 *
 * @see com.github.panpf.sketch.compose.core.common.test.target.AsyncImageTargetTest
 */
class AsyncImageTarget constructor(imageState: AsyncImageState) : GenericComposeTarget() {

    private val lifecycle: Lifecycle = imageState.lifecycle
    private val requestManager: ComposeRequestManager = imageState.requestManager
    private val listener = AsyncImageListener(this)

    private val lazyString by lazy { imageState.toString() }

    var imageState: AsyncImageState? = imageState

    override val painter: Painter?
        get() = imageState?.painterMutableState?.value

    override val contentScale: ContentScale
        get() = imageState?.contentScaleMutableState?.value ?: ContentScale.Fit

    override val alignment: Alignment
        get() = imageState?.alignmentMutableState?.value ?: Alignment.Center

    override val filterQuality: FilterQuality
        get() = imageState?.filterQualityMutableState?.value ?: super.filterQuality

    override fun setPainter(painter: Painter?) {
        val oldPainter = imageState?.painterMutableState?.value
        if (painter !== oldPainter) {
            (oldPainter as? RememberObserver)?.onForgotten()
            imageState?.painterMutableState?.value = painter
            (painter as? RememberObserver)?.onRemembered()
        }
    }

    fun setPreviewImage(sketch: Sketch, request: ImageRequest, image: Image?) {
        onStart(sketch, request, placeholder = image)
        listener.onStart(request)
    }

    fun onRemembered() {
        (painter as? RememberObserver)?.onRemembered()
    }

    fun onForgotten() {
        (painter as? RememberObserver)?.onForgotten()
    }

    override fun getRequestManager(): ComposeRequestManager = requestManager


    override fun getListener(): AsyncImageListener = listener

    override fun getProgressListener(): AsyncImageListener = listener

    override fun getLifecycleResolver(): LifecycleResolver = LifecycleResolver(lifecycle)


    override fun getSizeResolver(): AsyncImageSizeResolver? = imageState?.sizeResolver

    override fun getImageOptions(): ImageOptions? = imageState?.imageOptions


    override fun onStart(sketch: Sketch, request: ImageRequest, placeholder: Image?) {
        super.onStart(sketch, request, placeholder)
        val imageState = imageState ?: return
        val loading = Loading(painter)
        imageState.painterStateMutableState.value = loading
        imageState.onPainterState?.invoke(loading)
    }

    override fun onSuccess(sketch: Sketch, request: ImageRequest, result: Success, image: Image) {
        super.onSuccess(sketch, request, result, image)
        val imageState = imageState ?: return
        val success = PainterState.Success(result, painter!!)
        imageState.painterStateMutableState.value = success
        imageState.onPainterState?.invoke(success)
    }

    override fun onError(sketch: Sketch, request: ImageRequest, error: Error, image: Image?) {
        super.onError(sketch, request, error, image)
        val imageState = imageState ?: return
        val error1 = PainterState.Error(error, painter)
        imageState.painterStateMutableState.value = error1
        imageState.onPainterState?.invoke(error1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AsyncImageTarget
        if (lifecycle != other.lifecycle) return false
        if (requestManager != other.requestManager) return false
        return true
    }

    override fun hashCode(): Int {
        var result = lifecycle.hashCode()
        result = 31 * result + requestManager.hashCode()
        return result
    }

    override fun toString(): String = "AsyncImageTarget($lazyString)"
}