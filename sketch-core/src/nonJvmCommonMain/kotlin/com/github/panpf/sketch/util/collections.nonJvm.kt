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

package com.github.panpf.sketch.util

internal actual fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int,
    loadFactor: Float,
): MutableMap<K, V> = LruMutableMap(LinkedHashMap(initialCapacity, loadFactor))

private class LruMutableMap<K : Any, V : Any>(
    private val delegate: MutableMap<K, V>,
) : MutableMap<K, V> by delegate {

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = delegate.entries.mapTo(mutableSetOf(), ::MutableEntry)

    override fun get(key: K): V? {
        // Remove then re-add the item to move it to the top of the insertion order.
        val item = delegate.remove(key)
        if (item != null) {
            delegate[key] = item
        }
        return item
    }

    override fun put(key: K, value: V): V? {
        // Remove then re-add the item to move it to the top of the insertion order.
        val item = delegate.remove(key)
        delegate[key] = value
        return item
    }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    private inner class MutableEntry(
        private val delegate: MutableMap.MutableEntry<K, V>,
    ) : MutableMap.MutableEntry<K, V> by delegate {

        override fun setValue(newValue: V): V {
            val oldValue = delegate.setValue(newValue)
            put(key, value)
            return oldValue
        }
    }
}