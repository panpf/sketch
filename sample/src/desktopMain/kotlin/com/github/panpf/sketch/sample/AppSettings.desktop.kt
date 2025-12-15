package com.github.panpf.sketch.sample

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppSettings actual constructor(context: PlatformContext) : BaseAppSettings(context) {

    val cacheDecodeTimeoutFrame: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "cacheDecodeTimeoutFrame", true)
    }

    val localPhotosDirPath: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, "localPhotosDirPath", "")
    }
}

actual fun platformSupportedDarkModes(): List<DarkMode> = DarkMode.values().toList()