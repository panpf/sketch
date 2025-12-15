package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppSettings actual constructor(context: PlatformContext) : BaseAppSettings(context) {

    val cacheDecodeTimeoutFrame: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "cacheDecodeTimeoutFrame", true)
    }
}

actual fun platformSupportedDarkModes(): List<DarkMode> = DarkMode.values().toList()