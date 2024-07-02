package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.EventBus

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()

actual fun platformMakeOtherMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Http Client",
            desc = null,
            values = listOf("Ktor", "OkHttp", "HttpURLConnection"),
            state = appSettings.httpClient,
            onItemClick = {
                EventBus.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
}