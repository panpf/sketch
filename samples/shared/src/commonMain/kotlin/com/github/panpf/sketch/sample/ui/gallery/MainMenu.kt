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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.Res.drawable
import com.github.panpf.sketch.sample.ic_layout_grid
import com.github.panpf.sketch.sample.ic_layout_grid_staggered
import com.github.panpf.sketch.sample.ic_pause
import com.github.panpf.sketch.sample.ic_play
import com.github.panpf.sketch.sample.ic_settings
import com.github.panpf.sketch.sample.ui.components.MyDialog
import com.github.panpf.sketch.sample.ui.components.rememberMyDialogState
import com.github.panpf.sketch.sample.ui.setting.AppSettingsList
import com.github.panpf.sketch.sample.ui.setting.Page.LIST
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun MainMenu(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color = colorScheme.tertiaryContainer)
    ) {
        val appSettings: AppSettings = koinInject()
        val disallowAnimatedImageInList by appSettings.disallowAnimatedImageInList.collectAsState()
        val staggeredGridMode by appSettings.staggeredGridMode.collectAsState()
        val playIcon = if (disallowAnimatedImageInList) {
            painterResource(drawable.ic_play)
        } else {
            painterResource(drawable.ic_pause)
        }
        val staggeredGridModeIcon = if (staggeredGridMode) {
            painterResource(drawable.ic_layout_grid)
        } else {
            painterResource(drawable.ic_layout_grid_staggered)
        }
        Icon(
            painter = playIcon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    appSettings.disallowAnimatedImageInList.value = !disallowAnimatedImageInList
                }
                .padding(10.dp),
            tint = colorScheme.onTertiaryContainer
        )
        Icon(
            painter = staggeredGridModeIcon,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
                .clickable {
                    appSettings.staggeredGridMode.value = !staggeredGridMode
                }
                .padding(10.dp),
            tint = colorScheme.onTertiaryContainer
        )
        val settingsDialogState = rememberMyDialogState()
        Icon(
            painter = painterResource(drawable.ic_settings),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
                .clickable {
                    settingsDialogState.show()
                }
                .padding(10.dp),
            tint = colorScheme.onTertiaryContainer
        )
        MyDialog(settingsDialogState) {
            AppSettingsList(LIST)
        }
    }
}