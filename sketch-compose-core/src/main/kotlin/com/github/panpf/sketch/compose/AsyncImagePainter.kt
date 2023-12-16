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

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.AsyncImageState.Companion.DefaultTransform
import com.github.panpf.sketch.compose.internal.onPainterStateOf
import com.github.panpf.sketch.compose.internal.toIntSizeOrNull
import com.github.panpf.sketch.compose.internal.transformOf
import com.github.panpf.sketch.request.DisplayRequest

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `resizeSize(Size(100, 100))`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param uriEmpty A [Painter] that is displayed when the request's [DisplayRequest.uriString] is empty.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    imageUri: String?,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    placeholder: Painter? = null,
    error: Painter? = null,
    uriEmpty: Painter? = error,
    onLoading: ((PainterState.Loading) -> Unit)? = null,
    onSuccess: ((PainterState.Success) -> Unit)? = null,
    onError: ((PainterState.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri),
    sketch = sketch,
    state = state,
    transform = transformOf(placeholder, error, uriEmpty),
    onPainterState = onPainterStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `resizeSize(Size(100, 100))`) if you need this.
 *
 * @param imageUri [DisplayRequest.uriString] value.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param transform A callback to transform a new [PainterState] before it's applied to the
 *  [AsyncImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onPainterState Called when the painterState changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    imageUri: String?,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    transform: (PainterState) -> PainterState = DefaultTransform,
    onPainterState: ((PainterState) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = DisplayRequest(LocalContext.current, imageUri),
    sketch = sketch,
    state = state,
    transform = transform,
    onPainterState = onPainterState,
    contentScale = contentScale,
    filterQuality = filterQuality
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `resizeSize(Size(100, 100))`) if you need this.
 *
 * @param request [DisplayRequest].
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param uriEmpty A [Painter] that is displayed when the request's [DisplayRequest.uriString] is empty.
 * @param onLoading Called when the image request begins loading.
 * @param onSuccess Called when the image request completes successfully.
 * @param onError Called when the image request completes unsuccessfully.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    request: DisplayRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    placeholder: Painter? = null,
    error: Painter? = null,
    uriEmpty: Painter? = error,
    onLoading: ((PainterState.Loading) -> Unit)? = null,
    onSuccess: ((PainterState.Success) -> Unit)? = null,
    onError: ((PainterState.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = request,
    sketch = sketch,
    state = state,
    transform = transformOf(placeholder, error, uriEmpty),
    onPainterState = onPainterStateOf(onLoading, onSuccess, onError),
    contentScale = contentScale,
    filterQuality = filterQuality,
)

/**
 * Return an [AsyncImagePainter] that executes an [DisplayRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [DisplayRequest.Builder.resizeSize] value
 *   (e.g. `resizeSize(Size(100, 100))`) if you need this.
 *
 * @param request [DisplayRequest].
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param transform A callback to transform a new [PainterState] before it's applied to the
 *  [AsyncImagePainter]. Typically this is used to overwrite the state's [Painter].
 * @param onPainterState Called when the painterState changes.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    request: DisplayRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    transform: (PainterState) -> PainterState = DefaultTransform,
    onPainterState: ((PainterState) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter {
    state.request = request
    state.sketch = sketch
    state.contentScale = contentScale
    state.transform = transform
    state.onPainterState = onPainterState
    state.filterQuality = filterQuality
    state.onRemembered() // Invoke this manually so `state.painterState` is set to `Loading` immediately.
    return remember(state) {
        AsyncImagePainter(state)
    }
}

/**
 * A [Painter] that reads 'painter' from [AsyncImageState] and renders
 */
@Stable
class AsyncImagePainter internal constructor(
    private val state: AsyncImageState,
) : Painter() {

    private var alpha: Float by mutableFloatStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = state.painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        // Update the draw scope's current size.
        // It plays a decisive role when using AsyncImagePainter without AsyncImage
        this@onDraw.size.toIntSizeOrNull()?.let { state.setSize(it) }

        // Draw the current painter.
        state.painter?.apply { draw(size, alpha, colorFilter) }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }
}
