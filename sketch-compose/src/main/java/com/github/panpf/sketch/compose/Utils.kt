package com.github.panpf.sketch.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.compose.AsyncImagePainter.Companion.DefaultTransform
import com.github.panpf.sketch.compose.AsyncImagePainter.State
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.UriInvalidException
import com.github.panpf.sketch.request.get
import com.github.panpf.sketch.resize.Scale
import kotlin.math.roundToInt

///** Create an [ImageRequest] from the [model]. */
//@Composable
//@ReadOnlyComposable
//internal fun requestOf(model: Any?): ImageRequest {
//    if (model is ImageRequest) {
//        return model
//    } else {
//        return ImageRequest.Builder(LocalContext.current).data(model).build()
//    }
//}

private const val FROM_COMPOSE_KEY = "sketch:fromCompose"

fun DisplayRequest.Builder.setFromCompose(enabled: Boolean): DisplayRequest.Builder = apply {
    if (enabled) {
        setParameter(key = FROM_COMPOSE_KEY, value = true, cacheKey = null)
    } else {
        removeParameter(key = FROM_COMPOSE_KEY)
    }
}

fun ImageOptions.Builder.setFromCompose(enabled: Boolean): ImageOptions.Builder = apply {
    if (enabled) {
        setParameter(key = FROM_COMPOSE_KEY, value = true, cacheKey = null)
    } else {
        removeParameter(key = FROM_COMPOSE_KEY)
    }
}

fun DisplayRequest.isFromCompose(): Boolean =
    (parameters?.get(FROM_COMPOSE_KEY) as Boolean?) == true

fun ImageOptions.isFromCompose(): Boolean =
    (parameters?.get(FROM_COMPOSE_KEY) as Boolean?) == true

@Stable
internal fun transformOf(
    placeholder: Painter?,
    error: Painter?,
    uriEmpty: Painter?,
): (State) -> State {
    return if (placeholder != null || error != null || uriEmpty != null) {
        { state ->
            when (state) {
                is State.Loading -> {
                    if (placeholder != null) state.copy(painter = placeholder) else state
                }
//                is State.Error -> if (state.result.throwable is NullRequestDataException) {
                is State.Error -> if (state.result.exception is UriInvalidException) {
                    if (uriEmpty != null) state.copy(painter = uriEmpty) else state
                } else {
                    if (error != null) state.copy(painter = error) else state
                }
                else -> state
            }
        }
    } else {
        DefaultTransform
    }
}

@Stable
internal fun onStateOf(
    onLoading: ((State.Loading) -> Unit)?,
    onSuccess: ((State.Success) -> Unit)?,
    onError: ((State.Error) -> Unit)?,
): ((State) -> Unit)? {
    return if (onLoading != null || onSuccess != null || onError != null) {
        { state ->
            when (state) {
                is State.Loading -> onLoading?.invoke(state)
                is State.Success -> onSuccess?.invoke(state)
                is State.Error -> onError?.invoke(state)
                is State.Empty -> {}
            }
        }
    } else {
        null
    }
}

//@Stable
//internal fun ContentScale.toScale(): Scale = when (this) {
//    ContentScale.Fit, ContentScale.Inside -> Scale.FIT
//    else -> Scale.FILL
//}

@Stable
internal fun ContentScale.toScale(): Scale {
    return when (this) {
        ContentScale.FillBounds,
        ContentScale.FillWidth,
        ContentScale.FillHeight -> Scale.FILL
        ContentScale.Fit -> Scale.CENTER_CROP
        ContentScale.Crop -> Scale.CENTER_CROP
        ContentScale.Inside -> Scale.CENTER_CROP
        ContentScale.None -> Scale.CENTER_CROP
        else -> Scale.CENTER_CROP
    }
}

internal fun Constraints.constrainWidth(width: Float) =
    width.coerceIn(minWidth.toFloat(), maxWidth.toFloat())

internal fun Constraints.constrainHeight(height: Float) =
    height.coerceIn(minHeight.toFloat(), maxHeight.toFloat())

internal inline fun Float.takeOrElse(block: () -> Float) = if (isFinite()) this else block()

internal fun Size.toIntSize() = IntSize(width.roundToInt(), height.roundToInt())

internal val ZeroConstraints = Constraints.fixed(0, 0)
