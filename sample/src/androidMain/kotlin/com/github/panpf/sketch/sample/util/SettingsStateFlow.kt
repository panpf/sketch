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

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun SettingsStateFlow(
    key: String, initialize: String, preferences: SharedPreferences,
): SettingsStateFlow<String> =
    SharedPreferencesSettingsStateFlowImpl(
        initialize,
        StringSharedPreferencesAdapter(preferences, key, initialize)
    )

fun SettingsStateFlow(
    key: String, initialize: Boolean, preferences: SharedPreferences,
): SettingsStateFlow<Boolean> =
    SharedPreferencesSettingsStateFlowImpl(
        initialize,
        BooleanSharedPreferencesAdapter(preferences, key, initialize)
    )

interface SettingsStateFlow<T> : MutableStateFlow<T>

private class SharedPreferencesSettingsStateFlowImpl<T>(
    initialize: T,
    private val adapter: SharedPreferencesAdapter<T>?
) : SettingsStateFlow<T> {

    private val delegateStateFlow = MutableStateFlow(initialize)

    init {
        if (adapter != null) {
            delegateStateFlow.value = adapter.state
        }
    }

    override var value: T
        get() = delegateStateFlow.value
        set(value) {
            adapter?.state = value
            delegateStateFlow.value = value
        }

    override val replayCache: List<T>
        get() = delegateStateFlow.replayCache

    override val subscriptionCount: StateFlow<Int>
        get() = delegateStateFlow.subscriptionCount

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        delegateStateFlow.collect(collector)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return delegateStateFlow.compareAndSet(expect, update)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        delegateStateFlow.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return delegateStateFlow.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        delegateStateFlow.emit(value)
    }
}

private interface SharedPreferencesAdapter<T> {
    var state: T
}

private class StringSharedPreferencesAdapter(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: String
) : SharedPreferencesAdapter<String> {

    override var state: String
        get() = preferences.getString(key, defaultValue) ?: defaultValue
        set(value) {
            preferences.edit {
                putString(key, value)
            }
        }
}

private class BooleanSharedPreferencesAdapter(
    private val preferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean
) : SharedPreferencesAdapter<Boolean> {

    override var state: Boolean
        get() = preferences.getBoolean(key, defaultValue)
        set(value) {
            preferences.edit {
                putBoolean(key, value)
            }
        }
}