package com.github.panpf.sketch.sample.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.sample.AppSettings

@Composable
actual fun getSettingsDialogHeight(): Dp {
    return 600.dp
}

actual fun platformMakeDecodeMenuList(appSettings: AppSettings): List<SettingItem> = emptyList()