package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppSettings

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()

actual fun platformAnimatedMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        SwitchSettingItem(
            title = "Cache Decode Timeout Frame",
            desc = null,
            state = appSettings.cacheDecodeTimeoutFrame,
        )
    )
}

actual fun platformMakeOtherMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()