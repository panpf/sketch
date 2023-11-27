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

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StringMmkvData(
    mmkv: MMKV, key: String, defaultState: String
) : BaseMmkvData<String>(defaultState, StringMmkvAdapter(mmkv, key, defaultState))

class BooleanMmkvData(
    mmkv: MMKV, key: String, defaultState: Boolean
) : BaseMmkvData<Boolean>(defaultState, BooleanMmkvAdapter(mmkv, key, defaultState))

abstract class BaseMmkvData<T>(defaultState: T, private val mmkvAdapter: MmkvAdapter<T>) {

    private val _stateFlow = MutableStateFlow(defaultState)
    private val _sharedFlow = MutableSharedFlow<T>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val stateFlow: StateFlow<T>
        get() = _stateFlow
    val sharedFlow: SharedFlow<T>
        get() = _sharedFlow

    init {
        _stateFlow.value = mmkvAdapter.state
    }

    var value: T
        get() = stateFlow.value
        set(value) {
            mmkvAdapter.state = value
            _stateFlow.value = value
            scope.launch {
                _sharedFlow.emit(value)
            }
        }
}

interface MmkvAdapter<T> {
    var state: T
}

class StringMmkvAdapter(
    private val mmkv: MMKV,
    private val key: String,
    private val defaultValue: String
) : MmkvAdapter<String> {

    override var state: String
        get() = mmkv.decodeString(key, defaultValue) ?: defaultValue
        set(value) {
            mmkv.encode(key, value)
        }
}

class BooleanMmkvAdapter(
    private val mmkv: MMKV,
    private val key: String,
    private val defaultValue: Boolean
) : MmkvAdapter<Boolean> {

    override var state: Boolean
        get() = mmkv.decodeBool(key, defaultValue)
        set(value) {
            mmkv.encode(key, value)
        }
}