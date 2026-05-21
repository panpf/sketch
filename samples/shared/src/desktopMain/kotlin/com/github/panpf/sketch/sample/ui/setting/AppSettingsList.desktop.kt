package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.ClickableSettingItem
import com.github.panpf.sketch.sample.ui.components.DropdownSettingItem
import com.github.panpf.sketch.sample.ui.components.SwitchSettingItem
import org.koin.compose.koinInject
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@Composable
actual fun PlatformAnimatedSettingsList(appSettings: AppSettings) {
    SwitchSettingItem(
        title = "Cache Decode Timeout Frame",
        desc = null,
        state = appSettings.cacheDecodeTimeoutFrame,
    )
}

@Composable
actual fun VideoSettingsList(appSettings: AppSettings) {

}

@Composable
actual fun PlatformOtherSettingsList(appSettings: AppSettings, page: AppSettingsPage) {
    val appEvents: AppEvents = koinInject()

    val httpClientValues = remember {
        listOf("Ktor", "OkHttp", "HttpURLConnection")
    }
    DropdownSettingItem(
        title = "Http Client",
        desc = null,
        values = httpClientValues,
        state = appSettings.httpClient,
        onItemClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )

    if (page == AppSettingsPage.LIST) {
        ClickableSettingItem(
            title = "Local Album Path",
            desc = "Add a local album path. Long press to clear.",
            value = appSettings.localPhotosDirPath,
            onClick = {
                SwingUtilities.invokeLater {
                    val dir = pickDir()
                    if (dir != null) {
                        appSettings.localPhotosDirPath.value = dir.absolutePath
                    }
                }
            },
            onLongClick = {
                appSettings.localPhotosDirPath.value = ""
            }
        )
    }
}

private fun pickDir(): File? {
    val chooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        dialogTitle = "Select directory"
        isAcceptAllFileFilterUsed = false
    }
    val result = chooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        return chooser.selectedFile
    }

    return null
}