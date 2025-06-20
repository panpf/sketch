/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright 2023 Coil Contributors
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.PainterState.Error
import com.github.panpf.sketch.PainterState.Loading
import com.github.panpf.sketch.PainterState.Success
import com.github.panpf.sketch.internal.AsyncImageContent
import com.github.panpf.sketch.internal.requestOf
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.toRequestSize

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
 * @param loading An optional callback to overwrite what's drawn while the image request is loading.
 * @param success An optional callback to overwrite what's drawn when the image request succeeds.
 * @param error An optional callback to overwrite what's drawn when the image request fails.
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
 *
 * @see com.github.panpf.sketch.compose.core.common.test.SubcomposeAsyncImageTest.testSubcomposeAsyncImage1
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    uri: String?,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    loading: @Composable (SubcomposeAsyncImageScope.(Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) = SubcomposeAsyncImage(
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
    content = contentOf(loading, success, error, clipToBounds),
)

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
 * @param content A callback to draw the content inside an [SubcomposeAsyncImageScope].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.SubcomposeAsyncImageTest.testSubcomposeAsyncImage2
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
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
    content: @Composable SubcomposeAsyncImageScope.() -> Unit,
) = SubcomposeAsyncImage(
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
    content = content
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
 * @param loading An optional callback to overwrite what's drawn while the image request is loading.
 * @param success An optional callback to overwrite what's drawn when the image request succeeds.
 * @param error An optional callback to overwrite what's drawn when the image request fails.
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
 *
 * @see com.github.panpf.sketch.compose.core.common.test.SubcomposeAsyncImageTest.testSubcomposeAsyncImage3
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    request: ImageRequest,
    sketch: Sketch,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    loading: @Composable (SubcomposeAsyncImageScope.(Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) = SubcomposeAsyncImage(
    request = request,
    contentDescription = contentDescription,
    sketch = sketch,
    modifier = modifier,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    content = contentOf(loading, success, error, clipToBounds),
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
 * @param content A callback to draw the content inside an [SubcomposeAsyncImageScope].
 *
 * @see com.github.panpf.sketch.compose.core.common.test.SubcomposeAsyncImageTest.testSubcomposeAsyncImage4
 */
@Composable
fun SubcomposeAsyncImage(
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
    content: @Composable SubcomposeAsyncImageScope.() -> Unit,
) {
    val painter = rememberAsyncImagePainter(
        request = request,
        sketch = sketch,
        state = state,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
    if (request.definedOptions.sizeResolver == null) {
        // Slow path: draw the content with subcomposition as we need to resolve the constraints
        // before calling `content`.
        BoxWithConstraints(
            modifier = modifier,
            contentAlignment = alignment,
            propagateMinConstraints = true
        ) {
            // Ensure images are prepared before content is drawn when in-memory cache exists
            state.setSizeWithLeast(constraints.toRequestSize())

            RealSubcomposeAsyncImageScope(
                parentScope = this,
                state = state,
                painter = painter,
                contentDescription = contentDescription,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            ).content()
        }
    } else {
        // Fast path: draw the content without subcomposition as we don't need to resolve the constraints.
        Box(
            modifier = modifier,
            contentAlignment = alignment,
            propagateMinConstraints = true
        ) {
            RealSubcomposeAsyncImageScope(
                parentScope = this,
                state = state,
                painter = painter,
                contentDescription = contentDescription,
                alignment = alignment,
                contentScale = contentScale,
                alpha = alpha,
                colorFilter = colorFilter
            ).content()
        }
    }
}

/**
 * A scope for the children of [SubcomposeAsyncImage].
 */
@LayoutScopeMarker
@Immutable
interface SubcomposeAsyncImageScope : BoxScope {

    /**
     * SubcomposeAsyncImageContent can read its painter or painterState parameters to draw
     */
    val state: AsyncImageState

    /** The painter that is drawn by [SubcomposeAsyncImageContent]. */
    val painter: AsyncImagePainter

    /** The content description for [SubcomposeAsyncImageContent]. */
    val contentDescription: String?

    /** The default alignment for any composables drawn in this scope. */
    val alignment: Alignment

    /** The content scale for [SubcomposeAsyncImageContent]. */
    val contentScale: ContentScale

    /** The alpha for [SubcomposeAsyncImageContent]. */
    val alpha: Float

    /** The color filter for [SubcomposeAsyncImageContent]. */
    val colorFilter: ColorFilter?
}

/**
 * A composable that draws [SubcomposeAsyncImage]'s content with [SubcomposeAsyncImageScope]'s
 * properties.
 *
 * @see SubcomposeAsyncImageScope
 */
@Composable
fun SubcomposeAsyncImageScope.SubcomposeAsyncImageContent(
    modifier: Modifier = Modifier,
    painter: Painter = this.painter,
    contentDescription: String? = this.contentDescription,
    alignment: Alignment = this.alignment,
    contentScale: ContentScale = this.contentScale,
    alpha: Float = this.alpha,
    colorFilter: ColorFilter? = this.colorFilter,
    clipToBounds: Boolean = true,
) = AsyncImageContent(
    modifier = modifier,
    painter = painter,
    contentDescription = contentDescription,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    clipToBounds = clipToBounds,
)

@Stable
private fun contentOf(
    loading: @Composable (SubcomposeAsyncImageScope.(Loading) -> Unit)?,
    success: @Composable (SubcomposeAsyncImageScope.(Success) -> Unit)?,
    error: @Composable (SubcomposeAsyncImageScope.(Error) -> Unit)?,
    clipToBounds: Boolean = true,
): @Composable SubcomposeAsyncImageScope.() -> Unit {
    return if (loading != null || success != null || error != null) {
        {
            var draw = true
            when (val painterState = state.painterState) {
                is Loading -> if (loading != null) loading(painterState).also {
                    draw = false
                }

                is Success -> if (success != null) success(painterState).also {
                    draw = false
                }

                is Error -> if (error != null) error(painterState).also {
                    draw = false
                }

                else -> {} // Skipped if rendering on the main thread.
            }
            if (draw) SubcomposeAsyncImageContent(clipToBounds = clipToBounds)
        }
    } else {
        { SubcomposeAsyncImageContent(clipToBounds = clipToBounds) }
    }
}

private data class RealSubcomposeAsyncImageScope(
    private val parentScope: BoxScope,
    override val state: AsyncImageState,
    override val painter: AsyncImagePainter,
    override val contentDescription: String?,
    override val alignment: Alignment,
    override val contentScale: ContentScale,
    override val alpha: Float,
    override val colorFilter: ColorFilter?,
) : SubcomposeAsyncImageScope, BoxScope by parentScope
