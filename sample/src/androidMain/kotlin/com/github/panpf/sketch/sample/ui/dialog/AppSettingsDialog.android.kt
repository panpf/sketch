package com.github.panpf.sketch.sample.ui.dialog

import android.graphics.ColorSpace.Named
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.github.panpf.sketch.sample.AppSettings

@Composable
actual fun getSettingsDialogHeight(): Dp {
    return with(LocalDensity.current) {
        (LocalContext.current.resources.displayMetrics.heightPixels * 0.8f).toInt().toDp()
    }
}

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = buildList {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
        // Cannot use Named.entries, crashes on versions lower than O
        val items = listOf("Default").plus(Named.values().map { it.name })
        add(
            DropdownSettingItem(
                title = "Color Space",
                desc = null,
                values = items,
                state = appSettings.colorSpace,
            )
        )
    }
    if (VERSION.SDK_INT <= VERSION_CODES.M) {
        add(
            SwitchSettingItem(
                title = "inPreferQualityOverSpeed",
                desc = null,
                state = appSettings.inPreferQualityOverSpeed
            )
        )
    }
}