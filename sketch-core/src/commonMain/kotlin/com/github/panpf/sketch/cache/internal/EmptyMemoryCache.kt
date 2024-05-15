package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data object EmptyMemoryCache : MemoryCache {

    private val mutexMap = LruCache<String, Mutex>(200)

    override val maxSize: Long = 0L

    override val size: Long = 0L

    override fun put(key: String, value: Value): Int = -3

    override fun remove(key: String): Value? = null

    override fun get(key: String): Value? = null

    override fun exist(key: String): Boolean = false

    override fun trim(targetSize: Long) {

    }

    override fun keys(): Set<String> = emptySet()

    override fun clear() {

    }

    override suspend fun <R> withLock(key: String, action: suspend MemoryCache.() -> R): R {
        requiredMainThread()    // Can save synchronization overhead
        val lock = mutexMap[key] ?: Mutex().apply {
            this@EmptyMemoryCache.mutexMap.put(key, this)
        }
        return lock.withLock {
            action(this@EmptyMemoryCache)
        }
    }
}