/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.PlatformContext
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings

//actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
//    val configDir = AppDirsFactory.getInstance().getUserConfigDir(
//        /* appName = */ "com.github.panpf.sketch4.sample",
//        /* appVersion = */ null,
//        /* appAuthor = */ null,
//    )!!.let { File(it) }
//    val preferencesPath = configDir.resolve("dice.preferences_pb").toOkioPath()
//    return PreferenceDataStoreFactory.createWithPath { preferencesPath }
//}

actual fun createSettings(context: PlatformContext): Settings {
    val delegate = java.util.prefs.Preferences.userRoot().node("com.github.panpf.sketch4.sample")
    return PreferencesSettings(delegate)
}