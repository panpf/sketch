package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageOptions
import okio.Path.Companion.toPath
import platform.Foundation.NSPreferencePanesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
    val configDir = getPreferenceDirectory().toPath().resolve("sketch4")
    val preferencesPath = configDir.resolve("dice.preferences_pb")
    return PreferenceDataStoreFactory.createWithPath { preferencesPath }
}

actual fun isDebugMode(): Boolean = true

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {
}

private fun getPreferenceDirectory(): String {
    val paths =
        NSSearchPathForDirectoriesInDomains(NSPreferencePanesDirectory, NSUserDomainMask, true)
    return paths.first() as String
}