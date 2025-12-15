package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

actual fun platformAnimatedMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        SwitchSettingItem(
            title = "Cache Decode Timeout Frame",
            desc = null,
            state = appSettings.cacheDecodeTimeoutFrame,
        )
    )
}

actual fun platformOtherMenuList(
    appSettings: AppSettings,
    page: Page,
    appEvents: AppEvents
): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Http Client",
            desc = null,
            values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
            state = appSettings.httpClient,
            onItemClick = {
                appEvents.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
    if (page == Page.LIST) {
        add(
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