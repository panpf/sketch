package com.github.panpf.sketch.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

fun <T> resetLazy(initializer: () -> T): ResetLazy<T> = ResetLazy(initializer)

class ResetLazy<T>(private val initializer: () -> T) : Lazy<T> {

    private val lock = SynchronizedObject()
    private var _value: T? = null

    override val value: T
        get() {
            val currentValue = _value
            if (currentValue != null) return currentValue

            synchronized(lock) {
                val currentValue1 = _value
                if (currentValue1 != null) return currentValue1

                val newValue = initializer()
                _value = newValue
                return newValue
            }
        }

    override fun isInitialized(): Boolean = _value != null

    fun reset() {
        synchronized(lock) {
            _value = null
        }
    }
}