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

package com.github.panpf.zoomimage

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.AsyncImagePainter
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.internal.AsyncImageContent
import com.github.panpf.sketch.name
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.zoomimage.compose.subsampling.subsampling
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.compose.zoom.mouseZoom
import com.github.panpf.zoomimage.compose.zoom.zoom
import com.github.panpf.zoomimage.compose.zoom.zoomScrollBar
import com.github.panpf.zoomimage.sketch.SketchImageSource
import com.github.panpf.zoomimage.sketch.SketchTileBitmapCache
import com.github.panpf.zoomimage.subsampling.ImageSource
import kotlin.math.roundToInt


/**
 * An image component that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * SketchZoomAsyncImage(
 *     uri = "https://sample.com/sample.jpeg",
 *     contentDescription = "view image",
 *     sketch = context.sketch,
 *     modifier = Modifier.fillMaxSize(),
 * )
 * ```
 *
 * @param uri [ImageRequest.uri] value.
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
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
 * @param zoomState The state to control zoom
 * @param scrollBar Controls whether scroll bars are displayed and their style
 * @param onLongPress Called when the user long presses the image
 * @param onTap Called when the user taps the image
 * @see com.github.panpf.zoomimage.compose.sketch.core.test.SketchZoomAsyncImageTest.testSketchZoomAsyncImage1
 */
@Composable
@NonRestartableComposable
fun SketchZoomAsyncImage(
    uri: String?,
    contentDescription: String?,
    sketch: Sketch,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    zoomState: SketchZoomState = rememberSketchZoomState(),
    scrollBar: ScrollBarSpec? = ScrollBarSpec.Default,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
) = SketchZoomAsyncImage(
    request = ImageRequest(LocalPlatformContext.current, uri),
    contentDescription = contentDescription,
    sketch = sketch,
    modifier = modifier,
    state = state,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    zoomState = zoomState,
    scrollBar = scrollBar,
    onLongPress = onLongPress,
    onTap = onTap,
)

/**
 * An image component that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * SketchZoomAsyncImage(
 *     request = ComposableImageRequest("https://sample.com/sample.jpeg") {
 *         placeholder(Res.drawable.placeholder)
 *         crossfade()
 *     },
 *     contentDescription = "view image",
 *     sketch = context.sketch,
 *     modifier = Modifier.fillMaxSize(),
 * )
 * ```
 *
 * @param request [ImageRequest].
 * @param contentDescription Text used by accessibility services to describe what this image
 *  represents. This should always be provided unless this image is used for decorative purposes,
 *  and does not represent a meaningful action that a user can take.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
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
 * @param zoomState The state to control zoom
 * @param scrollBar Controls whether scroll bars are displayed and their style
 * @param onLongPress Called when the user long presses the image
 * @param onTap Called when the user taps the image
 * @see com.github.panpf.zoomimage.compose.sketch.core.test.SketchZoomAsyncImageTest.testSketchZoomAsyncImage2
 */
@Composable
fun SketchZoomAsyncImage(
    request: ImageRequest,
    contentDescription: String?,
    sketch: Sketch,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    zoomState: SketchZoomState = rememberSketchZoomState(),
    scrollBar: ScrollBarSpec? = ScrollBarSpec.Default,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
) {
    zoomState.zoomable.contentScale = contentScale
    zoomState.zoomable.alignment = alignment

    LaunchedEffect(zoomState.subsampling) {
        zoomState.subsampling.tileBitmapCache = SketchTileBitmapCache(sketch)
    }
    LaunchedEffect(Unit) {
        snapshotFlow { state.painterState }.collect {
            onPainterState(sketch, zoomState, request, it)
        }
    }

    // moseZoom directly acts on ZoomAsyncImage, causing the zoom center to be abnormal.
    Box(modifier = modifier.mouseZoom(zoomState.zoomable)) {
        BaseZoomAsyncImage(
            request = request,
            contentDescription = contentDescription,
            sketch = sketch,
            state = state,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
            modifier = Modifier
                .matchParentSize()
                .zoom(
                    zoomable = zoomState.zoomable,
                    userSetupContentSize = true,
                    onLongPress = onLongPress,
                    onTap = onTap
                )
                .subsampling(zoomState.zoomable, zoomState.subsampling),
        )

        if (scrollBar != null) {
            Box(
                Modifier
                    .matchParentSize()
                    .zoomScrollBar(zoomState.zoomable, scrollBar)
            )
        }
    }
}

private fun onPainterState(
    sketch: Sketch,
    zoomState: SketchZoomState,
    request: ImageRequest,
    loadState: PainterState?,
) {
    zoomState.zoomable.logger.d {
        "SketchZoomAsyncImage. onPainterState. state=${loadState?.name}. uri='${request.uri}'"
    }
    val painterSize = loadState?.painter
        ?.intrinsicSize
        ?.takeIf { it.isSpecified }
        ?.roundToIntSize()
        ?.takeIf { it.isNotEmpty() }
    zoomState.zoomable.contentSize = painterSize ?: IntSize.Zero

    when (loadState) {
        is PainterState.Success -> {
            val imageSource = SketchImageSource.Factory(sketch, request.uri.toString())
            zoomState.setImageSource(imageSource)
        }

        else -> {
            zoomState.setImageSource(null as ImageSource?)
        }
    }
}

private fun Size.roundToIntSize(): IntSize {
    return IntSize(width.roundToInt(), height.roundToInt())
}

private fun IntSize.isNotEmpty(): Boolean = width > 0 && height > 0

/**
 * 1. Disabled clipToBounds
 * 2. alignment = Alignment.TopStart
 * 3. contentScale = ContentScale.None
 */
@Composable
private fun BaseZoomAsyncImage(
    request: ImageRequest,
    contentDescription: String?,
    sketch: Sketch,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
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
            state.setSize(size)
        },
        painter = painter,
        contentDescription = contentDescription,
        alignment = Alignment.TopStart,
        contentScale = ContentScale.None,
        alpha = alpha,
        colorFilter = colorFilter,
        clipToBounds = false,
    )
}