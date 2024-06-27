@file:OptIn(ExperimentalResourceApi::class)

package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.LocalPlatformContext
import com.github.panpf.sketch.sample.appSettings
import com.github.panpf.sketch.sample.ui.model.PhotoGridMode
import com.github.panpf.sketch.sample.ui.setting.AppSettingsDialog
import com.github.panpf.sketch.sample.ui.setting.Page.LIST
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import sketch.sample.generated.resources.Res.drawable
import sketch.sample.generated.resources.ic_layout_grid
import sketch.sample.generated.resources.ic_layout_grid_staggered
import sketch.sample.generated.resources.ic_pause
import sketch.sample.generated.resources.ic_play
import sketch.sample.generated.resources.ic_settings

@OptIn(ExperimentalResourceApi::class)
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
            painterResource(drawable.ic_play)
        } else {
            painterResource(drawable.ic_pause)
        }
        val photoGridModeIcon = if (photoListLayoutMode == PhotoGridMode.SQUARE) {
            painterResource(drawable.ic_layout_grid_staggered)
        } else {
            painterResource(drawable.ic_layout_grid)
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
            painter = painterResource(drawable.ic_settings),
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