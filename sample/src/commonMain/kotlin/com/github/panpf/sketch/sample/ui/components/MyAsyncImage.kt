package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.AsyncImage
import com.github.panpf.sketch.AsyncImageState
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.rememberAsyncImageState
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sample.ui.gallery.PhotoInfo

@Composable
fun MyAsyncImage(
    uri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clipToBounds: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val infoDialogState = rememberMyDialogState()
    AsyncImage(
        uri = uri,
        contentDescription = contentDescription,
        sketch = SingletonSketch.get(LocalPlatformContext.current),
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick?.invoke() },
                onLongPress = { infoDialogState.show() }
            )
        },
        state = state,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
    )
    MyDialog(infoDialogState) {
        PhotoInfo(state.result)
    }
}

@Composable
fun MyAsyncImage(
    request: ImageRequest,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    state: AsyncImageState = rememberAsyncImageState(),
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    clipToBounds: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val infoDialogState = rememberMyDialogState()
    AsyncImage(
        request = request,
        contentDescription = contentDescription,
        sketch = SingletonSketch.get(LocalPlatformContext.current),
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick?.invoke() },
                onLongPress = { infoDialogState.show() }
            )
        },
        state = state,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
    )
    MyDialog(infoDialogState) {
        PhotoInfo(state.result)
    }
}