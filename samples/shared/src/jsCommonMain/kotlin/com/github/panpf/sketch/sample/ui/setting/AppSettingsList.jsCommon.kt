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

actual fun platformVideoMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()

actual fun platformOtherMenuList(
    appSettings: AppSettings,
    page: Page,
    appEvents: AppEvents
): List<SettingItem> = buildList {
    add(
        SwitchSettingItem(
            title = "Use JsDecoder",
            desc = null,
            state = appSettings.useJsDecoder,
            onClick = {
                appEvents.toastFlow.emit("Restart the app to take effect")
            }
        )
    )
}