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

package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.cache.MemoryCache
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.LruCache
import com.github.panpf.sketch.util.requiredMainThread
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * An empty implementation of [MemoryCache].
 *
 * @see com.github.panpf.sketch.core.test.cache.internal.EmptyMemoryCacheTest
 */
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