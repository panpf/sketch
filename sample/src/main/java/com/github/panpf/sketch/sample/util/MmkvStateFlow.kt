package com.github.panpf.sketch.sample.util

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
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
    private val scope = CoroutineScope(SupervisorJob())

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