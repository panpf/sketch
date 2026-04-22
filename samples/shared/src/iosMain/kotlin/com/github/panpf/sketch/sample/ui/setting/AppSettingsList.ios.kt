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

actual fun platformVideoMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Video Frame Percent",
            desc = null,
            values = listOf(0f, 0.25f, 0.5f, 0.75f, 1f),
            state = appSettings.videoFramePercent,
        )
    )
    add(
        SwitchSettingItem(
            title = "Prefer Video Cover",
            desc = null,
            state = appSettings.preferVideoCover,
        )
    )
}

actual fun platformOtherMenuList(
    appSettings: AppSettings,
    page: Page,
    appEvents: AppEvents
): List<SettingItem> = emptyList()