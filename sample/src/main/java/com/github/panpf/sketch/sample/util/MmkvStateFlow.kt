package com.github.panpf.sketch.sample.util

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StringMmkvStateFlow(
    mmkv: MMKV, key: String, defaultState: String
) : BaseMmkvStateFlow<String>(defaultState, StringMmkvAdapter(mmkv, key, defaultState))

class BooleanMmkvStateFlow(
    mmkv: MMKV, key: String, defaultState: Boolean
) : BaseMmkvStateFlow<Boolean>(defaultState, BooleanMmkvAdapter(mmkv, key, defaultState))

abstract class BaseMmkvStateFlow<T>(
    defaultState: T,
    private val mmkvAdapter: MmkvAdapter<T>,
) : MutableStateFlow<T>, MutableSharedFlow<T> {

    private val stateFlow = MutableStateFlow(defaultState)

    init {
        stateFlow.value = mmkvAdapter.state
    }

    override val replayCache: List<T>
        get() = stateFlow.replayCache

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        stateFlow.collect(collector)
    }

    override var value: T
        get() = stateFlow.value
        set(value) {
            mmkvAdapter.state = value
            stateFlow.value = value
        }

    override val subscriptionCount: StateFlow<Int>
        get() = stateFlow.subscriptionCount

    override suspend fun emit(value: T) {
        stateFlow.emit(value)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        stateFlow.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return stateFlow.tryEmit(value)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return if (value == expect) {
            value = update
            true
        } else {
            false
        }
    }
}

//fun <T> matchMmkvAdapter(
//    mmkv: MMKV,
//    clazz: Class<T>,
//    key: String,
//    defaultValue: T?
//): MmkvAdapter<T> =
//    when (clazz) {
//        String::class.java -> StringMmkvAdapter(mmkv, key, defaultValue as String?)
//        Boolean::class.java -> BooleanMmkvAdapter(
//            mmkv, key, (defaultValue as Boolean?) ?: false
//        )
//        else -> throw IllegalArgumentException("Not currently supported $clazz")
//    } as MmkvAdapter<T>

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

//class EnumMmkvAdapter<T: Enum<T>>(private val mmkv: MMKV, private val key: String) : MmkvAdapter<T?> {
//    override var state: T?
//        get() = mmkv.decodeString(key)
//        set(value) {
//            if (value != null) {
//                mmkv.encode(key, value)
//            } else {
//                mmkv.remove(key)
//            }
//        }
//
//}