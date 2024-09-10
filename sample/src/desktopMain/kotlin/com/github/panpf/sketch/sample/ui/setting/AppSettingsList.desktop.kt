package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.EventBus
import org.jetbrains.skia.ColorType

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Bitmap Quality",
            desc = null,
            values = listOf("Default", "LOW", "HIGH").plus(ColorType.values().map { it.name }),
            state = appSettings.bitmapQualityName,
        )
    )
}

actual fun platformAnimatedMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        SwitchSettingItem(
            title = "Cache Decode Timeout Frame",
            desc = null,
            state = appSettings.cacheDecodeTimeoutFrame,
        )
    )
}



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