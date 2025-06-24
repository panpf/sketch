package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings

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
}