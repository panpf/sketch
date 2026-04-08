package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.zoomimage.SketchZoomState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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
    val photoViewerViewModel: PhotoViewerViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    PhotoViewerBottomBar(
        modifier = modifier,
        zoomState = zoomState,
        buttonBackgroundColor = buttonBackgroundColor,
        buttonContentColor = buttonContentColor,
        onInfoClick = onInfoClick,
        onShareClick = {
            coroutineScope.launch {
                handleShareActionResult(appEvents, photoViewerViewModel.share(imageUri))
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                handleSaveActionResult(appEvents, photoViewerViewModel.save(imageUri))
            }
        },
    )
}