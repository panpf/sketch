package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageOptions
import okio.FileSystem

actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
    // TODO This is a temporary solution. It is not recommended to use the temporary directory
    return PreferenceDataStoreFactory.createWithPath {
        FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve(
            "dice.preferences_pb"
        )
    }
}

actual fun isDebugMode(): Boolean = false

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {

}