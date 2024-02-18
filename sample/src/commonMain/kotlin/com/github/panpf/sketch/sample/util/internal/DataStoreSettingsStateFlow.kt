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
    adapter = StringDataStoreAdapter(dataStore, key, initialize)
)

fun booleanSettingsStateFlow(
    key: String,
    initialize: Boolean,
    dataStore: DataStore<Preferences>,
): SettingsStateFlow<Boolean> = DataStoreSettingsStateFlowImpl(
    adapter = BooleanDataStoreAdapter(dataStore, key, initialize)
)

interface SettingsStateFlow<T> : MutableStateFlow<T>

private class DataStoreSettingsStateFlowImpl<T>(
    private val adapter: DataStoreAdapter<T>
) : SettingsStateFlow<T> {

    override var value: T
        get() = adapter.state.value
        set(value) {
            adapter.setValue(value)
        }

    override val replayCache: List<T>
        get() = adapter.state.replayCache

    override val subscriptionCount: StateFlow<Int>
        get() = adapter.state.subscriptionCount

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        adapter.state.collect(collector)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return adapter.state.compareAndSet(expect, update)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        adapter.state.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return adapter.state.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        adapter.state.emit(value)
    }
}

private interface DataStoreAdapter<T> {
    val state: MutableStateFlow<T>

    fun setValue(value: T?)
}

private class StringDataStoreAdapter(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val initialize: String
) : DataStoreAdapter<String> {

    private val preferencesKey = stringPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override val state = MutableStateFlow(initialize)

    init {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            dataStore.data.map { it[preferencesKey] }.collect {
                state.value = it ?: initialize
            }
        }
    }

    override fun setValue(value: String?) {
        if (value != null) {
            state.value = value
            coroutineScope.launch {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        } else {
            state.value = initialize
            coroutineScope.launch {
                dataStore.edit {
                    it.remove(preferencesKey)
                }
            }
        }
    }
}

private class BooleanDataStoreAdapter(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val initialize: Boolean
) : DataStoreAdapter<Boolean> {

    private val preferencesKey = booleanPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override val state = MutableStateFlow(initialize)

    init {
        coroutineScope.launch {
            dataStore.data.map { it[preferencesKey] }.collect {
                state.value = it ?: initialize
            }
        }
    }

    override fun setValue(value: Boolean?) {
        if (value != null) {
            state.value = value
            coroutineScope.launch {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        } else {
            state.value = initialize
            coroutineScope.launch {
                dataStore.edit {
                    it.remove(preferencesKey)
                }
            }
        }
    }
}

private class IntDataStoreAdapter(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val initialize: Int
) : DataStoreAdapter<Int> {

    private val preferencesKey = intPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override val state = MutableStateFlow(initialize)

    init {
        coroutineScope.launch {
            dataStore.data.map { it[preferencesKey] }.collect {
                state.value = it ?: initialize
            }
        }
    }

    override fun setValue(value: Int?) {
        if (value != null) {
            state.value = value
            coroutineScope.launch {
                dataStore.edit {
                    it[preferencesKey] = value
                }
            }
        } else {
            state.value = initialize
            coroutineScope.launch {
                dataStore.edit {
                    it.remove(preferencesKey)
                }
            }
        }
    }
}