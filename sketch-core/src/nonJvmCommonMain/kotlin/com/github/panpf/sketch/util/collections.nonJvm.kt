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

/**
 * Create a [MutableMap] that orders its entries by most recently used to least recently used.
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testLruMutableMap
 */
internal actual fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int,
    loadFactor: Float,
): MutableMap<K, V> = LruMutableMap(LinkedHashMap(initialCapacity, loadFactor))

/**
 * Convert this [Map] to an immutable [Map].
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testToImmutableMap
 */
internal actual fun <K, V> Map<K, V>.toImmutableMap() = when {
    isEmpty() -> emptyMap()
    this is ImmutableMap -> this
    else -> ImmutableMap(LinkedHashMap(this))
}

/**
 * Convert this [List] to an immutable [List].
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testToImmutableList
 */
internal actual fun <T> List<T>.toImmutableList() = when {
    isEmpty() -> emptyList()
    this is ImmutableList -> this
    else -> ImmutableList(ArrayList(this))
}

/**
 * [MutableMap] whose entries are ordered by least recently used
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testLruMutableMap
 */
internal class LruMutableMap<K : Any, V : Any>(
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

/**
 * Immutable [Map]
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testImmutableMap
 */
private class ImmutableMap<K, V>(
    private val delegate: Map<K, V>,
) : Map<K, V> by delegate {

    override val entries: Set<Map.Entry<K, V>>
        get() = delegate.entries.mapTo(mutableSetOf(), ::ImmutableEntry)

    override fun equals(other: Any?) = delegate == other
    override fun hashCode() = delegate.hashCode()
    override fun toString() = delegate.toString()

    private class ImmutableEntry<K, V>(
        private val delegate: Map.Entry<K, V>,
    ) : Map.Entry<K, V> by delegate {
        override fun equals(other: Any?) = delegate == other
        override fun hashCode() = delegate.hashCode()
        override fun toString() = delegate.toString()
    }
}

/**
 * Immutable [List]
 *
 * @see com.github.panpf.sketch.core.nonjvmcommon.test.util.CollectionsNonJvmTest.testImmutableList
 */
private class ImmutableList<T>(
    private val delegate: List<T>,
) : List<T> by delegate {

    override fun iterator(): Iterator<T> {
        return ImmutableIterator(delegate.iterator())
    }

    override fun listIterator(): ListIterator<T> {
        return ImmutableListIterator(delegate.listIterator())
    }

    override fun listIterator(index: Int): ListIterator<T> {
        return ImmutableListIterator(delegate.listIterator(index))
    }

    override fun equals(other: Any?) = delegate == other
    override fun hashCode() = delegate.hashCode()
    override fun toString() = delegate.toString()

    private class ImmutableIterator<T>(
        private val delegate: Iterator<T>,
    ) : Iterator<T> by delegate {
        override fun equals(other: Any?) = delegate == other
        override fun hashCode() = delegate.hashCode()
        override fun toString() = delegate.toString()
    }

    private class ImmutableListIterator<T>(
        private val delegate: ListIterator<T>,
    ) : ListIterator<T> by delegate {
        override fun equals(other: Any?) = delegate == other
        override fun hashCode() = delegate.hashCode()
        override fun toString() = delegate.toString()
    }
}