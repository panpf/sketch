package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.zoomimage.SketchZoomState
import kotlinx.coroutines.launch

@Composable
actual fun PhotoViewerBottomBarWrapper(
    imageUri: String,
    modifier: Modifier,
    zoomState: SketchZoomState,
    buttonBackgroundColor: Color?,
    buttonContentColor: Color?,
    onInfoClick: (() -> Unit)?,
) {
    val context = LocalPlatformContext.current
    val coroutineScope = rememberCoroutineScope()
    PhotoViewerBottomBar(
        modifier = modifier,
        zoomState = zoomState,
        buttonBackgroundColor = buttonBackgroundColor,
        buttonContentColor = buttonContentColor,
        onInfoClick = onInfoClick,
        onShareClick = {
            coroutineScope.launch {
                sharePhoto(context, imageUri)
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                savePhoto(context, imageUri)
            }
        },
    )
}

@Suppress("UNUSED_PARAMETER")
private suspend fun savePhoto(context: PlatformContext, imageUri: String) {
    EventBus.toastFlow.emit("Saving is under development")
}

@Suppress("UNUSED_PARAMETER")
private suspend fun sharePhoto(context: PlatformContext, imageUri: String) {
    EventBus.toastFlow.emit("Js platform does not support sharing photo")
}