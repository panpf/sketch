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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual fun stringSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: String,
): SettingsStateFlow<String> = SettingsStateFlowImpl(initialize)

actual fun booleanSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Boolean,
): SettingsStateFlow<Boolean> = SettingsStateFlowImpl(initialize)

actual fun intSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: Int,
): SettingsStateFlow<Int> = SettingsStateFlowImpl(initialize)

actual fun <E : Enum<E>> enumSettingsStateFlow(
    context: PlatformContext,
    key: String,
    initialize: E,
    convert: (name: String) -> E,
): SettingsStateFlow<E> = SettingsStateFlowImpl(initialize)

private class SettingsStateFlowImpl<T>(private val initialize: T) : SettingsStateFlow<T> {
    private val state = MutableStateFlow(initialize)

    override var value: T
        get() = state.value
        set(value) {
            state.value = value
        }

    override val replayCache: List<T>
        get() = state.replayCache

    override val subscriptionCount: StateFlow<Int>
        get() = state.subscriptionCount

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        state.collect(collector)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return state.compareAndSet(expect, update)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        state.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return state.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        state.emit(value)
    }
}