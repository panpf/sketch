package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.sample.AppSettings
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

actual fun platformMakeOtherMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()