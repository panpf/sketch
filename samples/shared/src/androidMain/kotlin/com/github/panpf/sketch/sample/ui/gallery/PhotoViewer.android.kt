package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.ui.base.ActionResult
import com.github.panpf.zoomimage.SketchZoomState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.storage.WRITE_STORAGE
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
    val sketch: Sketch = koinInject()
    val coroutineScope = rememberCoroutineScope()
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController =
        remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)
    PhotoViewerBottomBar(
        modifier = modifier,
        zoomState = zoomState,
        buttonBackgroundColor = buttonBackgroundColor,
        buttonContentColor = buttonContentColor,
        onInfoClick = onInfoClick,
        onShareClick = {
            coroutineScope.launch {
                sharePhoto(sketch, appEvents, imageUri)
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                try {
                    controller.providePermission(Permission.WRITE_STORAGE)
                    savePhoto(sketch, appEvents, imageUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    appEvents.toastFlow.emit("You have denied storage permission and cannot save pictures for you.")
                }
            }
        },
    )
}

private suspend fun savePhoto(sketch: Sketch, appEvents: AppEvents, imageUri: String) {
    val result = PhotoActionViewModel(sketch).save(imageUri)
    handleActionResult(appEvents, result)
}

private suspend fun sharePhoto(sketch: Sketch, appEvents: AppEvents, imageUri: String) {
    val result = PhotoActionViewModel(sketch).share(imageUri)
    handleActionResult(appEvents, result)
}

private suspend fun handleActionResult(appEvents: AppEvents, result: ActionResult): Boolean =
    when (result) {
        is ActionResult.Success -> {
            result.message?.let {
                appEvents.toastFlow.emit(it)
            }
            true
        }

        is ActionResult.Error -> {
            appEvents.toastFlow.emit(result.message)
            false
        }
    }