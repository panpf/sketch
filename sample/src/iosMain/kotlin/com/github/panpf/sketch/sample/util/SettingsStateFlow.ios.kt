/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.github.panpf.sketch.PlatformContext
import okio.Path.Companion.toPath
import platform.Foundation.NSPreferencePanesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

actual fun createDataStore(context: PlatformContext): DataStore<Preferences> {
    val configDir = getPreferenceDirectory().toPath().resolve("sketch4")
    val preferencesPath = configDir.resolve("dice.preferences_pb")
    return PreferenceDataStoreFactory.createWithPath { preferencesPath }
}

private fun getPreferenceDirectory(): String {
    val paths =
        NSSearchPathForDirectoriesInDomains(NSPreferencePanesDirectory, NSUserDomainMask, true)
    return paths.first() as String
}