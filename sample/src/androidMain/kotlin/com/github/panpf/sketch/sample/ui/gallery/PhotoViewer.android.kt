package com.github.panpf.sketch.sample.ui.gallery

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.EventBus
import com.github.panpf.sketch.sample.ui.base.ActionResult
import com.github.panpf.zoomimage.SketchZoomState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.storage.WRITE_STORAGE
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
                sharePhoto(context, imageUri)
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                try {
                    controller.providePermission(Permission.WRITE_STORAGE)
                    savePhoto(context, imageUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    EventBus.toastFlow.emit("You have denied storage permission and cannot save pictures for you.")
                }
            }
        },
    )
}

private suspend fun savePhoto(context: PlatformContext, imageUri: String) {
    val result = PhotoActionViewModel(context.applicationContext as Application).save(imageUri)
    handleActionResult(result)
}

private suspend fun sharePhoto(context: PlatformContext, imageUri: String) {
    val result = PhotoActionViewModel(context.applicationContext as Application).share(imageUri)
    handleActionResult(result)
}

suspend fun handleActionResult(result: ActionResult): Boolean =
    when (result) {
        is ActionResult.Success -> {
            result.message?.let {
                EventBus.toastFlow.emit(it)
            }
            true
        }

        is ActionResult.Error -> {
            EventBus.toastFlow.emit(result.message)
            false
        }
    }