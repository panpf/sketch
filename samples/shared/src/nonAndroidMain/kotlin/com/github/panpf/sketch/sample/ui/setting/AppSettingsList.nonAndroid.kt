package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.decode.name
import com.github.panpf.sketch.decode.values
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.DropdownSettingItem
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType

actual fun platformColorTypes(): List<String> {
    return ColorType.values().filter { it != ColorType.UNKNOWN }.map { it.name }
}

actual fun platformColorSpaces(): List<String> {
    return ColorSpace.values().map { it.name() }
}

@Composable
actual fun PlatformDecodeSettingsList(appSettings: AppSettings) {
    val colorSpaceValues = remember {
        listOf("Default").plus(platformColorSpaces())
    }
    DropdownSettingItem(
        title = "Bitmap Color Space",
        desc = null,
        values = colorSpaceValues,
        state = appSettings.colorSpaceName,
    )
}