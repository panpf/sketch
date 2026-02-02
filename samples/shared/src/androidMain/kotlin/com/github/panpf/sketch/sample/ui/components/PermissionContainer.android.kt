package com.github.panpf.sketch.sample.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

@Composable
actual fun PermissionContainer(
    permission: Any?,
    permissionRequired: Boolean,
    content: @Composable () -> Unit
) {
    if (permission != null) {
        val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
        val controller: PermissionsController =
            remember(factory) { factory.createPermissionsController() }
        BindEffect(controller)
        var showContent by remember { mutableStateOf(false) }
        var deniedDialogShowing by remember { mutableStateOf(false) }
        val startPermissionFlow = remember { MutableStateFlow<Int?>(1) }
        LaunchedEffect(Unit) {
            startPermissionFlow.filterNotNull().collect {
                startPermissionFlow.value = null
                try {
                    controller.providePermission(permission as dev.icerock.moko.permissions.Permission)
                    showContent = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (permissionRequired) {
                        deniedDialogShowing = true
                    } else {
                        showContent = true
                    }
                }
            }
        }

        if (showContent) {
            content()
        }

        if (deniedDialogShowing) {
            AlertDialog(
                onDismissRequest = { deniedDialogShowing = false },
                title = {
                    Text(text = "Error")
                },
                text = {
                    Text(text = "The current page must be granted '$permission' permission before it can be used normally. Please grant permission again.")
                },
                confirmButton = {
                    Button(onClick = { startPermissionFlow.value = 1 }) {
                        Text(text = "OK")
                    }
                },
            )
        }
    } else {
        content()
    }
}