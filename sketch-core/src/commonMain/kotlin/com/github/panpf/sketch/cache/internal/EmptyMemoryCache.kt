package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemoryCache.Value

data object EmptyMemoryCache : MemoryCache {

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
}