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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.request.ImageRequest
import org.koin.compose.koinInject

/**
 * A composable that executes an [ImageRequest] asynchronously and renders the result.
 *
 * @param uri [ImageRequest.uri] value.
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
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
 * @see com.github.panpf.sketch.compose.koin.common.test.SingletonSubcomposeAsyncImageTest.testSubcomposeAsyncImage1
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    uri: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    loading: @Composable (SubcomposeAsyncImageScope.(PainterState.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(PainterState.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(PainterState.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) = SubcomposeAsyncImage(
    uri = uri,
    contentDescription = contentDescription,
    sketch = koinInject(),
    modifier = modifier,
    state = state,
    loading = loading,
    success = success,
    error = error,
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
 * @param uri [ImageRequest.uri] value.
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
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
 * @see com.github.panpf.sketch.compose.koin.common.test.SingletonSubcomposeAsyncImageTest.testSubcomposeAsyncImage2
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    uri: String?,
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
    uri = uri,
    contentDescription = contentDescription,
    sketch = koinInject(),
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
 * @see com.github.panpf.sketch.compose.koin.common.test.SingletonSubcomposeAsyncImageTest.testSubcomposeAsyncImage3
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    request: ImageRequest,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    loading: @Composable (SubcomposeAsyncImageScope.(PainterState.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(PainterState.Success) -> Unit)? = null,
    error: @Composable (SubcomposeAsyncImageScope.(PainterState.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
) = SubcomposeAsyncImage(
    request = request,
    contentDescription = contentDescription,
    sketch = koinInject(),
    modifier = modifier,
    state = state,
    loading = loading,
    success = success,
    error = error,
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
 * @see com.github.panpf.sketch.compose.koin.common.test.SingletonSubcomposeAsyncImageTest.testSubcomposeAsyncImage4
 */
@Composable
@NonRestartableComposable
fun SubcomposeAsyncImage(
    request: ImageRequest,
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
    request = request,
    contentDescription = contentDescription,
    sketch = koinInject(),
    modifier = modifier,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    content = content
)