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
package com.github.panpf.sketch.sample.util.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun stringSettingsStateFlow(
    key: String,
    initialize: String,
    dataStore: DataStore<Preferences>,
): SettingsStateFlow<String> = DataStoreSettingsStateFlowImpl(
    initialize = initialize,
    adapter = StringDataStoreAdapter(dataStore, key, initialize)
)

fun booleanSettingsStateFlow(
    key: String,
    initialize: Boolean,
    dataStore: DataStore<Preferences>,
): SettingsStateFlow<Boolean> = DataStoreSettingsStateFlowImpl(
    initialize = initialize,
    adapter = BooleanDataStoreAdapter(dataStore, key, initialize)
)

interface SettingsStateFlow<T> : MutableStateFlow<T>

private class DataStoreSettingsStateFlowImpl<T>(
    initialize: T,
    private val adapter: DataStoreAdapter<T>?
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

private interface DataStoreAdapter<T> {
    var state: T
}

private class StringDataStoreAdapter(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val initialize: String
) : DataStoreAdapter<String> {

    private val preferencesKey = stringPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override var state: String = initialize
        set(value) {
            field = value
            coroutineScope.launch {
                // TODO invalid
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        }

    init {
        coroutineScope.launch {
            dataStore.data.map { it[preferencesKey] }.collect {
                state = it ?: initialize
            }
        }
    }
}

private class BooleanDataStoreAdapter(
    private val preferences: DataStore<Preferences>,
    key: String,
    private val initialize: Boolean
) : DataStoreAdapter<Boolean> {

    private val preferencesKey = booleanPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override var state: Boolean = initialize
        set(value) {
            field = value
            coroutineScope.launch {
                preferences.edit {
                    it[preferencesKey] = value
                }
            }
        }

    init {
        coroutineScope.launch {
            preferences.data.map { it[preferencesKey] }.collect {
                state = it ?: initialize
            }
        }
    }
}

private class IntDataStoreAdapter(
    private val preferences: DataStore<Preferences>,
    key: String,
    private val initialize: Int
) : DataStoreAdapter<Int> {

    private val preferencesKey = intPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override var state: Int = initialize
        set(value) {
            field = value
            coroutineScope.launch {
                preferences.edit {
                    it[preferencesKey] = value
                }
            }
        }

    init {
        coroutineScope.launch {
            preferences.data.map { it[preferencesKey] }.collect {
                state = it ?: initialize
            }
        }
    }
}