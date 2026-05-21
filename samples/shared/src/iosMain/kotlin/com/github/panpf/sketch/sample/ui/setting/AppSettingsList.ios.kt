package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.DividerSettingItem
import com.github.panpf.sketch.sample.ui.components.DropdownSettingItem
import com.github.panpf.sketch.sample.ui.components.SwitchSettingItem

@Composable
actual fun PlatformAnimatedSettingsList(appSettings: AppSettings) {
    SwitchSettingItem(
        title = "Cache Decode Timeout Frame",
        desc = null,
        state = appSettings.cacheDecodeTimeoutFrame,
    )
}

@Composable
actual fun VideoSettingsList(appSettings: AppSettings) {
    DividerSettingItem("Video")

    val videoFramePercentValue = remember {
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
    }
    DropdownSettingItem(
        title = "Video Frame Percent",
        desc = null,
        values = videoFramePercentValue,
        state = appSettings.videoFramePercent,
    )

    SwitchSettingItem(
        title = "Prefer Video Cover",
        desc = null,
        state = appSettings.preferVideoCover,
    )
}

@Composable
actual fun PlatformOtherSettingsList(appSettings: AppSettings, page: AppSettingsPage) {

}