package com.github.panpf.zoomimage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
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
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.AsyncImagePainter
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PainterState
import com.github.panpf.sketch.internal.AsyncImageContent
import com.github.panpf.sketch.rememberAsyncImagePainter
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.components.zoomimage.core.SketchImageSource
import com.github.panpf.zoomimage.compose.ZoomState
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.compose.subsampling.subsampling
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import com.github.panpf.zoomimage.compose.zoom.zoom
import com.github.panpf.zoomimage.compose.zoom.zoomScrollBar
import com.github.panpf.zoomimage.subsampling.TileBitmapCache
import kotlin.math.roundToInt

expect fun createTileBitmapCache(
    sketch: Sketch,
    caller: String
): TileBitmapCache?

/**
 * An image component that integrates the Sketch image loading framework that zoom and subsampling huge images
 *
 * Example usages:
 *
 * ```kotlin
 * SketchZoomAsyncImage(
 *     imageUri = "http://sample.com/sample.jpg",
 *     contentDescription = "view image",
 *     sketch = context.sketch,
 *     modifier = Modifier.fillMaxSize(),
 * )
 * ```
 *
 * @param imageUri [ImageRequest.uri] value.
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
 * @param state The state to control zoom
 * @param scrollBar Controls whether scroll bars are displayed and their style
 * @param onLongPress Called when the user long presses the image
 * @param onTap Called when the user taps the image
 */
@Composable
@NonRestartableComposable
fun SketchZoomAsyncImage(
    imageUri: String?,
    contentDescription: String?,
    sketch: Sketch,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    state: ZoomState = rememberZoomState(),
    scrollBar: ScrollBarSpec? = ScrollBarSpec.Default,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
) = SketchZoomAsyncImage(
    request = ImageRequest(LocalPlatformContext.current, imageUri),
    contentDescription = contentDescription,
    sketch = sketch,
    modifier = modifier,
    alignment = alignment,
    contentScale = contentScale,
    alpha = alpha,
    colorFilter = colorFilter,
    filterQuality = filterQuality,
    state = state,
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
 *     request = ImageRequest(LocalContext.current, "http://sample.com/sample.jpg") {
 *         placeholder(R.drawable.placeholder)
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
 * @param state The state to control zoom
 * @param scrollBar Controls whether scroll bars are displayed and their style
 * @param onLongPress Called when the user long presses the image
 * @param onTap Called when the user taps the image
 */
@Composable
fun SketchZoomAsyncImage(
    request: ImageRequest,
    contentDescription: String?,
    sketch: Sketch,
    modifier: Modifier = Modifier,
    imageState: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    state: ZoomState = rememberZoomState(),
    scrollBar: ScrollBarSpec? = ScrollBarSpec.Default,
    onLongPress: ((Offset) -> Unit)? = null,
    onTap: ((Offset) -> Unit)? = null,
) {
    state.zoomable.contentScale = contentScale
    state.zoomable.alignment = alignment

    val context = LocalPlatformContext.current
    LaunchedEffect(Unit) {
        state.subsampling.tileBitmapCache = createTileBitmapCache(sketch, "SketchZoomAsyncImage")
    }

    LaunchedEffect(imageState.painterState) {
        onPainterState(context, sketch, state, request, imageState.painterState)
    }

    BaseZoomAsyncImage(
        request = request,
        contentDescription = contentDescription,
        sketch = sketch,
        state = imageState,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        modifier = modifier
            .let { if (scrollBar != null) it.zoomScrollBar(state.zoomable, scrollBar) else it }
            .zoom(state.zoomable, onLongPress = onLongPress, onTap = onTap)
            .subsampling(state.zoomable, state.subsampling),
    )
}

private fun onPainterState(
    context: PlatformContext,
    sketch: Sketch,
    state: ZoomState,
    request: ImageRequest,
    loadState: PainterState,
) {
    state.zoomable.logger.d {
        "SketchZoomAsyncImage. onPainterState. state=${loadState.name}. uri='${request.uri}'"
    }
    val zoomableState = state.zoomable
    val subsamplingState = state.subsampling
    val painterSize = loadState.painter
        ?.intrinsicSize
        ?.takeIf { it.isSpecified }
        ?.roundToIntSize()
        ?.takeIf { it.isNotEmpty() }
    zoomableState.contentSize = painterSize ?: IntSize.Zero

    when (loadState) {
        is PainterState.Success -> {
            subsamplingState.disabledTileBitmapCache =
                request.memoryCachePolicy != CachePolicy.ENABLED
            val imageSource = SketchImageSource(context, sketch, request.uri)
            subsamplingState.setImageSource(imageSource)
        }

        else -> {
            subsamplingState.setImageSource(null)
        }
    }
}

private val PainterState.name: String
    get() = when (this) {
        is PainterState.Loading -> "Loading"
        is PainterState.Success -> "Success"
        is PainterState.Error -> "Error"
        is PainterState.Empty -> "Empty"
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