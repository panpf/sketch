package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.dialog.AppSettingsDialog
import com.github.panpf.sketch.sample.ui.dialog.Page.LIST
import com.github.panpf.sketch.sample.ui.model.PhotoGridMode
import com.github.panpf.sketch.sample.ui.rememberIconLayoutGridPainter
import com.github.panpf.sketch.sample.ui.rememberIconLayoutGridStaggeredPainter
import com.github.panpf.sketch.sample.ui.rememberIconPausePainter
import com.github.panpf.sketch.sample.ui.rememberIconPlayPainter
import com.github.panpf.sketch.sample.ui.rememberIconSettingsPainter

@Composable
fun MainMenu(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier.background(
            color = colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(50)
        )
    ) {
        val context = LocalPlatformContext.current
        val appSettings = context.appSettings
        val modifier1 = Modifier.size(40.dp).padding(10.dp)
        val disallowAnimatedImageInList by appSettings.disallowAnimatedImageInList.collectAsState()
        val photoListLayoutMode by appSettings.photoGridMode.collectAsState()
        val playIcon = if (disallowAnimatedImageInList) {
            rememberIconPlayPainter()
        } else {
            rememberIconPausePainter()
        }
        val photoGridModeIcon = if (photoListLayoutMode == PhotoGridMode.SQUARE) {
            rememberIconLayoutGridStaggeredPainter()
        } else {
            rememberIconLayoutGridPainter()
        }
        Icon(
            painter = playIcon,
            contentDescription = null,
            modifier = modifier1.clickable {
                appSettings.disallowAnimatedImageInList.value = !disallowAnimatedImageInList
            },
            tint = colorScheme.onTertiaryContainer
        )
        Icon(
            painter = photoGridModeIcon,
            contentDescription = null,
            modifier = modifier1.clickable {
                appSettings.photoGridMode.value =
                    if (photoListLayoutMode == PhotoGridMode.SQUARE) {
                        PhotoGridMode.STAGGERED
                    } else {
                        PhotoGridMode.SQUARE
                    }
            },
            tint = colorScheme.onTertiaryContainer
        )
        var showSettingsDialog by remember { mutableStateOf(false) }
        Icon(
            painter = rememberIconSettingsPainter(),
            contentDescription = null,
            modifier = modifier1.clickable {
                showSettingsDialog = !showSettingsDialog
            },
            tint = colorScheme.onTertiaryContainer
        )
        if (showSettingsDialog) {
            AppSettingsDialog(LIST) {
                showSettingsDialog = !showSettingsDialog
            }
        }
    }
}