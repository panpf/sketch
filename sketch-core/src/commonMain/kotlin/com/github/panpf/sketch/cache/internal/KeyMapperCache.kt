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
package com.github.panpf.sketch.cache.internal

import com.github.panpf.sketch.util.LruCache
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * KeyMapperCache is not thread-safe. If you need to use it in multiple threads, please handle the thread-safety issues yourself.
 */
class KeyMapperCache(val maxSize: Long = 100L, val mapper: (key: String) -> String) {

    private val cache = LruCache<String, String>(maxSize)

    fun mapKey(key: String): String =
        cache[key] ?: mapper(key).apply {
            cache.put(key, this)
        }
}

internal fun <R> KeyMapperCache.withLock(
    lock: SynchronizedObject,
    block: KeyMapperCache.() -> R
): R = synchronized(lock) {
    block()
}