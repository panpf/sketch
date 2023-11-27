package com.github.panpf.sketch.util.pool

import java.util.ArrayDeque
import java.util.Queue

internal abstract class BaseKeyPool<T : Poolable?> {

    private val keyPool: Queue<T> = ArrayDeque(MAX_SIZE)

    fun get(): T {
        return keyPool.poll() ?: create()
    }

    fun offer(key: T) {
        if (keyPool.size < MAX_SIZE) {
            keyPool.offer(key)
        }
    }

    abstract fun create(): T

    companion object {
        private const val MAX_SIZE = 20
    }
}
