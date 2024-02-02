package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import okio.Path.Companion.toOkioPath

actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath { context.filesDir.resolve("dice.preferences_pb").toOkioPath() }
}

actual fun isDebugMode(): Boolean = BuildConfig.DEBUG