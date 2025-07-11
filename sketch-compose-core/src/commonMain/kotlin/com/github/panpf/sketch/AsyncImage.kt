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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.github.panpf.sketch.internal.AsyncImageContent
import com.github.panpf.sketch.internal.requestOf
import com.github.panpf.sketch.request.ImageRequest

/**
 * A composable that executes an [ImageRequest] asynchronously and renders the result.
 *
 * @param uri [ImageRequest.uri] value.
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param alpha Optional opacity to be applied to the [AsyncImagePainter] when it is rendered
 *  onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the [AsyncImagePainter] when it is
 *  rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *  @param clipToBounds Whether to clip the content to the bounds of this layout. Defaults to true.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageTest.testAsyncImage1
 */
@Composable
@NonRestartableComposable
fun AsyncImage(
    uri: String?,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) = AsyncImage(
    request = requestOf(LocalPlatformContext.current, uri),
    contentDescription = contentDescription,
    sketch = sketch,
    modifier = modifier,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    clipToBounds = clipToBounds,
)

/**
 * A composable that executes an [ImageRequest] asynchronously and renders the result.
 *
 * @param request [ImageRequest].
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param alpha Optional opacity to be applied to the [AsyncImagePainter] when it is rendered
 *  onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the [AsyncImagePainter] when it is
 *  rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *  @param clipToBounds Whether to clip the content to the bounds of this layout. Defaults to true.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageTest.testAsyncImage2
 */
@Composable
fun AsyncImage(
    request: ImageRequest,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) {
    val painter = rememberAsyncImagePainter(
        request = request,
        sketch = sketch,
        state = state,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
    AsyncImageContent(
        modifier = modifier.onSizeChanged { size ->
            // Ensure images are prepared before content is drawn when in-memory cache exists
            state.setSizeWithLeast(size)
        },
        painter = painter,
        contentDescription = contentDescription,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        clipToBounds = clipToBounds,
    )
}

/**
 * A composable that executes an [ImageRequest] asynchronously and renders the result.
 *
 * @param uri [ImageRequest.uri] value.
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param alpha Optional opacity to be applied to the [AsyncImagePainter] when it is rendered
 *  onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the [AsyncImagePainter] when it is
 *  rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *  @param clipToBounds Whether to clip the content to the bounds of this layout. Defaults to true.
 *  @param keepContentNoneStartOnDraw Whether to always draw the content as none on the left on drawing, even if LayoutDirection is Rtl.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageTest.testAsyncImage1
 */
@Composable
@NonRestartableComposable
@Deprecated("Please use an overload function without the keepContentNoneStartOnDraw parameter instead. Will be removed in the future")
fun AsyncImage(
    uri: String?,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    keepContentNoneStartOnDraw: Boolean = false,
) = AsyncImage(
    request = requestOf(LocalPlatformContext.current, uri),
    contentDescription = contentDescription,
    sketch = sketch,
    modifier = modifier,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    clipToBounds = clipToBounds,
    keepContentNoneStartOnDraw = keepContentNoneStartOnDraw,
)

/**
 * A composable that executes an [ImageRequest] asynchronously and renders the result.
 *
 * @param request [ImageRequest].
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param sketch The [Sketch] that will be used to execute the request.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param state [AsyncImageState] that will be used to store the state of the request.
 * @param alignment Optional alignment parameter used to place the [AsyncImagePainter] in the given
 *  bounds defined by the width and height.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param alpha Optional opacity to be applied to the [AsyncImagePainter] when it is rendered
 *  onscreen.
 * @param colorFilter Optional [ColorFilter] to apply for the [AsyncImagePainter] when it is
 *  rendered onscreen.
 * @param filterQuality Sampling algorithm applied to a bitmap when it is scaled and drawn into the
 *  destination.
 *  @param clipToBounds Whether to clip the content to the bounds of this layout. Defaults to true.
 *  @param keepContentNoneStartOnDraw Whether to always draw the content as none on the left on drawing, even if LayoutDirection is Rtl.
 *
 * @see com.github.panpf.sketch.compose.core.common.test.AsyncImageTest.testAsyncImage2
 */
@Composable
@Deprecated("Please use an overload function without the keepContentNoneStartOnDraw parameter instead. Will be removed in the future")
fun AsyncImage(
    request: ImageRequest,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    keepContentNoneStartOnDraw: Boolean = false,
) {
    val painter = rememberAsyncImagePainter(
        request = request,
        sketch = sketch,
        state = state,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
    val drawAlignment = if (keepContentNoneStartOnDraw) {
        Alignment.TopStart.rtlFlipped(LocalLayoutDirection.current)
    } else {
        alignment
    }
    val drawContentScale =
        if (keepContentNoneStartOnDraw) ContentScale.None else contentScale
    AsyncImageContent(
        modifier = modifier.onSizeChanged { size ->
            // Ensure images are prepared before content is drawn when in-memory cache exists
            state.setSizeWithLeast(size)
        },
        painter = painter,
        contentDescription = contentDescription,
        alignment = drawAlignment,
        contentScale = drawContentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        clipToBounds = clipToBounds,
    )
}

/**
 * If [layoutDirection] is [LayoutDirection.Rtl], returns the horizontally flipped [Alignment], otherwise returns itself
 */
@Stable
private fun Alignment.rtlFlipped(layoutDirection: LayoutDirection): Alignment {
    if (layoutDirection != LayoutDirection.Rtl) return this
    return when (this) {
        Alignment.TopStart -> Alignment.TopEnd
        Alignment.TopCenter -> Alignment.TopCenter
        Alignment.TopEnd -> Alignment.TopStart
        Alignment.CenterStart -> Alignment.CenterEnd
        Alignment.Center -> Alignment.Center
        Alignment.CenterEnd -> Alignment.CenterStart
        Alignment.BottomStart -> Alignment.BottomEnd
        Alignment.BottomCenter -> Alignment.BottomCenter
        Alignment.BottomEnd -> Alignment.BottomStart
        else -> this
    }
}