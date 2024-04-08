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

import com.github.panpf.sketch.PlatformContext
import kotlinx.coroutines.flow.MutableStateFlow

expect fun stringSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: String,
): SettingsStateFlow<String>

expect fun booleanSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Boolean,
): SettingsStateFlow<Boolean>

expect fun intSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Int,
): SettingsStateFlow<Int>

expect fun <E : Enum<E>> enumSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: E,
    convert: (name: String) -> E,
): SettingsStateFlow<E>

interface SettingsStateFlow<T> : MutableStateFlow<T>