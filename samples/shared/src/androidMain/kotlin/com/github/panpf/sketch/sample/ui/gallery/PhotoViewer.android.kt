package com.github.panpf.sketch.sample.ui.gallery

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.ui.base.handleActionResult
import com.github.panpf.zoomimage.SketchZoomState
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.storage.WRITE_STORAGE
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
    val coroutineScope = rememberCoroutineScope()
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val photoActionViewModel: PhotoActionViewModel = koinViewModel()
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
                val actionResult = photoActionViewModel.share(imageUri)
                handleActionResult(appEvents, actionResult)
            }
        },
        onSaveClick = {
            coroutineScope.launch {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    controller.providePermission(Permission.WRITE_STORAGE)
                }
                val actionResult = photoActionViewModel.save(imageUri)
                handleActionResult(appEvents, actionResult)
            }
        },
    )
}