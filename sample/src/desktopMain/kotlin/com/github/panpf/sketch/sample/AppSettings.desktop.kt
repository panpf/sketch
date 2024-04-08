package com.github.panpf.sketch.sample

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.request.ImageOptions
import net.harawata.appdirs.AppDirsFactory
import okio.Path.Companion.toOkioPath
import java.io.File

actual fun isDebugMode(): Boolean = true

actual fun ImageOptions.Builder.platformBuildImageOptions(appSettings: AppSettings) {

}