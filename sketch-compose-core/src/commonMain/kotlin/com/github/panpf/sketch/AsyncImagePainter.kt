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

@file:Suppress("UnusedImport")

package com.github.panpf.sketch

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.toIntSize
import com.github.panpf.sketch.internal.requestOf
import com.github.panpf.sketch.painter.SketchPainter
import com.github.panpf.sketch.request.ImageRequest

/**
 * Return an [AsyncImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size(100, 100))`) if you need this.
 *
 * @param uri [ImageRequest.uri] value.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [uri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImagePainterTest.testRememberAsyncImagePainter
 */
@Composable
@NonRestartableComposable
fun rememberAsyncImagePainter(
    uri: String?,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = requestOf(LocalPlatformContext.current, uri),
    sketch = sketch,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    filterQuality = filterQuality
)

/**
 * Return an [AsyncImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size(100, 100))`) if you need this.
 *
 * @param uri [ImageRequest.uri] value.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [uri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImagePainterTest.testRememberAsyncImagePainter
 */
@Composable
@NonRestartableComposable
fun rememberAsyncImagePainter(
    uri: String?,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = requestOf(LocalPlatformContext.current, uri),
    sketch = sketch,
    state = state,
    contentScale = contentScale,
    alignment = Alignment.Center,
    filterQuality = filterQuality
)

/**
 * Return an [AsyncImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size(100, 100))`) if you need this.
 *
 * @param request [ImageRequest].
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImagePainterTest.testRememberAsyncImagePainter2
 */
@Composable
fun rememberAsyncImagePainter(
    request: ImageRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter {
    // You must set sketch and request at the end,
    // because alignment, contentScale, and filterQuality have default values,
    // so the loading task will be started immediately after the sketch and request is set.
    // If alignment, contentScale, filterQuality and the default values are different,
    // the loading task will be started again, causing waste.
    state.alignment = alignment
    state.contentScale = contentScale
    state.filterQuality = filterQuality
    state.sketch = sketch
    state.request = request
    return remember(state) {
        AsyncImagePainter(state)
    }
}

/**
 * Return an [AsyncImagePainter] that executes an [ImageRequest] asynchronously and renders the result.
 *
 * **This is a lower-level API than [AsyncImage] and may not work as expected in all situations. **
 *
 * - [AsyncImagePainter] will not finish loading if [AsyncImagePainter.onDraw] is not called.
 *   This can occur if a composable has an unbounded (i.e. [Constraints.Infinity]) width/height
 *   constraint. For example, to use [AsyncImagePainter] with [LazyRow] or [LazyColumn], you must
 *   set a bounded width or height respectively using `Modifier.width` or `Modifier.height`.
 * - [AsyncImageState.painterState] will not transition to [PainterState.Success] synchronously during the
 *   composition phase. Use [SubcomposeAsyncImage] or set a custom [ImageRequest.Builder.size] value
 *   (e.g. `size(Size(100, 100))`) if you need this.
 *
 * @param request [ImageRequest].
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImagePainterTest.testRememberAsyncImagePainter2
 */
@Composable
@NonRestartableComposable
fun rememberAsyncImagePainter(
    request: ImageRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = request,
    sketch = sketch,
    state = state,
    contentScale = contentScale,
    alignment = Alignment.Center,
    filterQuality = filterQuality
)

/**
 * A [Painter] that reads 'painter' from [AsyncImageState] and renders
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImagePainterTest
 */
@Stable
class AsyncImagePainter internal constructor(
    val state: AsyncImageState,
) : Painter(), SketchPainter {

    private var alpha: Float by mutableFloatStateOf(DefaultAlpha)
    private var colorFilter: ColorFilter? by mutableStateOf(null)

    override val intrinsicSize: Size
        get() = state.painter?.intrinsicSize ?: Size.Unspecified

    override fun DrawScope.onDraw() {
        setupRequestSize(this@onDraw.size)

        // Draw the current painter.
        state.painter?.apply { draw(size, alpha, colorFilter) }
    }

    private fun setupRequestSize(drawSize: Size) {
        // When using AsyncImage or SubcomposeAsyncImage, it will not be executed here because they will actively call setSize
        // So this will only be executed when AsyncImagePainter is used as a Painter in the Image component
        if (state.size == null) {
            state.setSizeWithLeast(drawSize.toIntSize())
        }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AsyncImagePainter
        return state == other.state
    }

    override fun hashCode(): Int {
        return state.hashCode()
    }

    override fun toString(): String {
        return "AsyncImagePainter(state=$state)"
    }
}
