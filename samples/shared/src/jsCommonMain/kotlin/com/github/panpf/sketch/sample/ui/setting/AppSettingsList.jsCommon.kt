package com.github.panpf.sketch.sample.ui.setting

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.sample.AppEvents
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.components.SwitchSettingItem
import org.koin.compose.koinInject

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

}

@Composable
actual fun PlatformOtherSettingsList(appSettings: AppSettings, page: AppSettingsPage) {
    val appEvents: AppEvents = koinInject()

    SwitchSettingItem(
        title = "Use JsDecoder",
        desc = null,
        state = appSettings.useJsDecoder,
        onClick = {
            appEvents.toastFlow.emit("Restart the app to take effect")
        }
    )
}