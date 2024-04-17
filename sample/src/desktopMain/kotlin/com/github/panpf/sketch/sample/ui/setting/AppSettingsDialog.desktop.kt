package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.MyEvents

@Composable
actual fun getSettingsDialogHeight(): Dp {
    return 600.dp
}

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()

actual fun platformMakeOtherMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Http Client",
            desc = null,
            values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
            state = appSettings.httpClient,
            onItemClick = {
                MyEvents.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
}