package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual fun createSettings(context: PlatformContext): Settings {
    return StorageSettings()
}