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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.panpf.sketch.PlatformContext
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private var dataStoreHolder: DataStore<Preferences>? = null
private val lock = SynchronizedObject()

private val PlatformContext.dataStore: DataStore<Preferences>
    get() = synchronized(lock) {
        dataStoreHolder ?: createDataStore(this).apply {
            dataStoreHolder = this
        }
    }

expect fun createDataStore(context: PlatformContext): DataStore<Preferences>

actual fun stringSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: String,
): SettingsStateFlow<String> = DataStoreSettingsStateFlowImpl(
    adapter = StringDataStoreAdapter(context.dataStore, key, initialize)
)

actual fun booleanSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Boolean,
): SettingsStateFlow<Boolean> = DataStoreSettingsStateFlowImpl(
    adapter = BooleanDataStoreAdapter(context.dataStore, key, initialize)
)

actual fun intSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Int,
): SettingsStateFlow<Int> = DataStoreSettingsStateFlowImpl(
    adapter = IntDataStoreAdapter(context.dataStore, key, initialize)
)

actual fun <E : Enum<E>> enumSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: E,
    convert: (name: String) -> E,
): SettingsStateFlow<E> = DataStoreSettingsStateFlowImpl(
    adapter = EnumDataStoreAdapter(context.dataStore, key, initialize, convert)
)

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
        // Make sure you get the value immediately
        state.value = runBlocking {
            dataStore.data.map { it[preferencesKey] }.first() ?: initialize
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

private class EnumDataStoreAdapter<E : Enum<E>>(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val initialize: E,
    private val convert: (name: String) -> E
) : DataStoreAdapter<E> {

    private val preferencesKey = stringPreferencesKey(key)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override val state = MutableStateFlow(initialize)

    init {
        // Make sure you get the value immediately
        state.value = runBlocking {
            dataStore.data.map { it[preferencesKey] }.first()?.let { convert(it) } ?: initialize
        }
    }

    override fun setValue(value: E?) {
        if (value != null) {
            state.value = value
            coroutineScope.launch {
                dataStore.edit {
                    it[preferencesKey] = value.name
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
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override val state = MutableStateFlow(initialize)

    init {
        // Make sure you get the value immediately
        state.value = runBlocking {
            dataStore.data.map { it[preferencesKey] }.first() ?: initialize
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
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override val state = MutableStateFlow(initialize)

    init {
        // Make sure you get the value immediately
        state.value = runBlocking {
            dataStore.data.map { it[preferencesKey] }.first() ?: initialize
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