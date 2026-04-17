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
import com.russhwolf.settings.Settings
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private val lock = SynchronizedObject()
private var settingsHolder: Settings? = null
private val PlatformContext.settings: Settings
    get() = synchronized(lock) {
        settingsHolder ?: createSettings(this).apply {
            settingsHolder = this
        }
    }

expect fun createSettings(context: PlatformContext): Settings

interface SettingsStateFlow<T> : MutableStateFlow<T>

fun booleanSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Boolean,
): SettingsStateFlow<Boolean> = SettingsStateFlowImpl(
    adapter = BooleanSettingsAdapter(context.settings, key, initialize)
)

fun stringSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: String,
): SettingsStateFlow<String> = SettingsStateFlowImpl(
    adapter = StringSettingsAdapter(context.settings, key, initialize)
)

fun intSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Int,
): SettingsStateFlow<Int> = SettingsStateFlowImpl(
    adapter = IntSettingsAdapter(context.settings, key, initialize)
)

fun floatSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Float,
): SettingsStateFlow<Float> = SettingsStateFlowImpl(
    adapter = FloatSettingsAdapter(context.settings, key, initialize)
)

fun <E : Enum<E>> enumSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: E,
    convert: (name: String) -> E,
): SettingsStateFlow<E> = SettingsStateFlowImpl(
    adapter = EnumSettingsAdapter(context.settings, key, initialize, convert)
)

private class SettingsStateFlowImpl<T>(
    private val adapter: ValueSettingsAdapter<T>
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

private interface ValueSettingsAdapter<T> {
    val state: MutableStateFlow<T>

    fun setValue(value: T?)
}

private class BooleanSettingsAdapter(
    private val settings: Settings,
    private val key: String,
    private val initialize: Boolean
) : ValueSettingsAdapter<Boolean> {

    override val state = MutableStateFlow(initialize)

    init {
        state.value = settings.getBooleanOrNull(key) ?: initialize
    }

    override fun setValue(value: Boolean?) {
        if (value != null) {
            state.value = value
            settings.putBoolean(key, value)
        } else {
            state.value = initialize
            settings.remove(key)
        }
    }
}

private class IntSettingsAdapter(
    private val settings: Settings,
    private val key: String,
    private val initialize: Int
) : ValueSettingsAdapter<Int> {

    override val state = MutableStateFlow(initialize)

    init {
        state.value = settings.getIntOrNull(key) ?: initialize
    }

    override fun setValue(value: Int?) {
        if (value != null) {
            state.value = value
            settings.putInt(key, value)
        } else {
            state.value = initialize
            settings.remove(key)
        }
    }
}

private class FloatSettingsAdapter(
    private val settings: Settings,
    private val key: String,
    private val initialize: Float
) : ValueSettingsAdapter<Float> {

    override val state = MutableStateFlow(initialize)

    init {
        state.value = settings.getFloatOrNull(key) ?: initialize
    }

    override fun setValue(value: Float?) {
        if (value != null) {
            state.value = value
            settings.putFloat(key, value)
        } else {
            state.value = initialize
            settings.remove(key)
        }
    }
}

private class StringSettingsAdapter(
    private val settings: Settings,
    private val key: String,
    private val initialize: String
) : ValueSettingsAdapter<String> {

    override val state = MutableStateFlow(initialize)

    init {
        state.value = settings.getStringOrNull(key) ?: initialize
    }

    override fun setValue(value: String?) {
        if (value != null) {
            state.value = value
            settings.putString(key, value)
        } else {
            state.value = initialize
            settings.remove(key)
        }
    }
}

private class EnumSettingsAdapter<E : Enum<E>>(
    private val settings: Settings,
    private val key: String,
    private val initialize: E,
    private val convert: (name: String) -> E
) : ValueSettingsAdapter<E> {

    override val state = MutableStateFlow(initialize)

    init {
        state.value = settings.getStringOrNull(key)?.let { convert(it) } ?: initialize
    }

    override fun setValue(value: E?) {
        if (value != null) {
            state.value = value
            settings.putString(key, value.name)
        } else {
            state.value = initialize
            settings.remove(key)
        }
    }
}