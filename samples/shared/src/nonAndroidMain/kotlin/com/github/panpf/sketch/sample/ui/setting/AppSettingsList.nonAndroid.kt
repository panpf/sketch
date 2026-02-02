package com.github.panpf.sketch.sample.ui.setting

import com.github.panpf.sketch.decode.name
import com.github.panpf.sketch.decode.values
import com.github.panpf.sketch.sample.AppSettings
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType

actual fun platformColorTypes(): List<String> {
    return ColorType.values().filter { it != ColorType.UNKNOWN }.map { it.name }
}

actual fun platformColorSpaces(): List<String> {
    return ColorSpace.values().map { it.name() }
}

actual fun platformDecodeMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    add(
        DropdownSettingItem(
            title = "Bitmap Color Space",
            desc = null,
            values = listOf("Default").plus(platformColorSpaces()),
            state = appSettings.colorSpaceName,
        )
    )
}