package com.github.panpf.sketch.sample

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatDelegate
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.sample.util.SettingsStateFlow
import com.github.panpf.sketch.sample.util.booleanSettingsStateFlow
import com.github.panpf.sketch.sample.util.stringSettingsStateFlow

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AppSettings actual constructor(context: PlatformContext) : BaseAppSettings(context) {

    // -------------------------------------- image --------------------------------------

    val preferQualityOverSpeed: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "preferQualityOverSpeed", false)
    }


    // -------------------------------------- other --------------------------------------

    val videoFrameDecoder: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "videoFrameDecoder", initialize = "FFmpeg")
    }

    val gifDecoder: SettingsStateFlow<String> by lazy {
        stringSettingsStateFlow(context, key = "gifDecoder1", initialize = "Movie+ImageDecoder")
    }

    val composePage: SettingsStateFlow<Boolean> by lazy {
        booleanSettingsStateFlow(context, "composePage", true)
    }
}

actual fun platformSupportedDarkModes(): List<DarkMode> {
    return if (VERSION.SDK_INT >= VERSION_CODES.O) {
        DarkMode.values().toList()
    } else {
        listOf(DarkMode.LIGHT, DarkMode.DARK)
    }
}

fun applyDarkMode(appSettings: AppSettings) {
    val mode = when (appSettings.darkMode.value) {
        DarkMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
    }
    AppCompatDelegate.setDefaultNightMode(mode)
}