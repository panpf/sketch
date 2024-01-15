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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
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
import com.github.panpf.sketch.compose.internal.isEmpty
import com.github.panpf.sketch.compose.internal.toIntSizeOrNull
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
 * @param imageUri [ImageRequest.uriString] value.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [imageUri]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
@NonRestartableComposable
fun rememberAsyncImagePainter(
    imageUri: String?,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter = rememberAsyncImagePainter(
    request = ImageRequest(LocalContext.current, imageUri),
    sketch = sketch,
    state = state,
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
 * @param request [ImageRequest].
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param contentScale Used to determine the aspect ratio scaling to be used if the canvas bounds
 *  are a different size from the intrinsic size of the image loaded by [request]. This should be set
 *  to the same value that's passed to [Image].
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 */
@Composable
fun rememberAsyncImagePainter(
    request: ImageRequest,
    sketch: Sketch,
    state: AsyncImageState = rememberAsyncImageState(),
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DefaultFilterQuality,
): AsyncImagePainter {
    state.request = request
    state.sketch = sketch
    state.contentScale = contentScale
    state.filterQuality = filterQuality
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
        // Use draw size as component size
        // It plays a decisive role when using AsyncImagePainter without AsyncImage
        // When intrinsicSize is empty or Unspecified, drawSize represents the size of the component.
        val componentSizeEmpty = state.size?.takeIf { !it.isEmpty() } == null
        val painterSizeEmpty = intrinsicSize.let { it.isUnspecified || it.isEmpty() }
        if (componentSizeEmpty && painterSizeEmpty) {
            val drawSize = this@onDraw.size.toIntSizeOrNull()
            if (drawSize != null) {
                state.setSize(drawSize)
            }
        }

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
