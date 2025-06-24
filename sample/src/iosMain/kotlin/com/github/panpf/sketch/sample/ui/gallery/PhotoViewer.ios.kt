package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.zoomimage.SketchZoomState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
actual fun PhotoViewerBottomBarWrapper(
    imageUri: String,
    modifier: Modifier,
    zoomState: SketchZoomState,
    buttonBackgroundColor: Color?,
    buttonContentColor: Color?,
    onInfoClick: (() -> Unit)?,
) {
    val appEvents: AppEvents = koinInject()
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
                sharePhoto(context, appEvents, imageUri)
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                savePhoto(context, appEvents, imageUri)
            }
        },
    )
}

@Suppress("UNUSED_PARAMETER")
private suspend fun savePhoto(context: PlatformContext, appEvents: AppEvents, imageUri: String) {
    appEvents.toastFlow.emit("Saving is under development")
}

@Suppress("UNUSED_PARAMETER")
private suspend fun sharePhoto(context: PlatformContext, appEvents: AppEvents, imageUri: String) {
    appEvents.toastFlow.emit("Sharing is under development")
}