package com.github.panpf.sketch.util.pool

/**
 * Similar to [java.util.LinkedHashMap] when access ordered except that it is access ordered on groups
 * of bitmaps rather than individual objects. The idea is to be able to find the LRU bitmap size, rather than the
 * LRU bitmap object. We can then remove bitmaps from the least recently used size of bitmap when we need to
 * reduce our cache size.
 *
 *
 * For the purposes of the LRU, we count gets for a particular size of bitmap as an access, even if no bitmaps
 * of that size are present. We do not count addition or removal of bitmaps as an access.
 */
class GroupedLinkedMap<K : Poolable?, V> {

    private val head = LinkedEntry<K, V>()
    private val keyToEntry: MutableMap<K, LinkedEntry<K, V>> = HashMap()

    fun put(key: K, value: V) {
        var entry = keyToEntry[key]
        if (entry == null) {
            entry = LinkedEntry(key)
            makeTail(entry)
            keyToEntry[key] = entry
        } else {
            key!!.offer()
        }
        entry.add(value)
    }

    operator fun get(key: K): V? {
        var entry = keyToEntry[key]
        if (entry == null) {
            entry = LinkedEntry(key)
            keyToEntry[key] = entry
        } else {
            key!!.offer()
        }
        makeHead(entry)
        return entry.removeLast()
    }

    fun exist(key: K): Boolean {
        val entry = keyToEntry[key]
        return entry != null && entry.size() > 0
    }

    fun exist(key: K, value: V): Boolean {
        val entry = keyToEntry[key]
        return entry != null && entry.contains(value)
    }

    fun removeLast(): V? {
        var last = head.prev
        while (last != head) {
            val removed = last.removeLast()
            if (removed != null) {
                return removed
            } else {
                // We will clean up empty lru entries since they are likely to have been one off or
                // unusual sizes and
                // are not likely to be requested again so the gc thrash should be minimal. Doing so will
                // speed up our
                // removeLast operation in the future and prevent our linked list from growing to
                // arbitrarily large
                // sizes.
                removeEntry(last)
                keyToEntry.remove(last.key)
                last.key!!.offer()
            }
            last = last.prev
        }
        return null
    }

    override fun toString(): String {
        val sb = StringBuilder("GroupedLinkedMap(")
        var current = head.next
        var hadAtLeastOneItem = false
        while (current != head) {
            hadAtLeastOneItem = true
            sb.append('{').append(current.key).append(':').append(current.size()).append("}, ")
            current = current.next
        }
        if (hadAtLeastOneItem) {
            sb.delete(sb.length - 2, sb.length)
        }
        return sb.append(")").toString()
    }

    // Make the entry the most recently used item.
    private fun makeHead(entry: LinkedEntry<K, V>) {
        removeEntry(entry)
        entry.prev = head
        entry.next = head.next
        updateEntry(entry)
    }

    // Make the entry the least recently used item.
    private fun makeTail(entry: LinkedEntry<K, V>) {
        removeEntry(entry)
        entry.prev = head.prev
        entry.next = head
        updateEntry(entry)
    }

    private class LinkedEntry<K, V> @JvmOverloads constructor(val key: K? = null) {
        var prev: LinkedEntry<K, V> = this
        var next: LinkedEntry<K, V> = prev
        private var values: MutableList<V>? = null

        fun removeLast(): V? {
            val valueSize = size()
            return if (valueSize > 0) values!!.removeAt(valueSize - 1) else null
        }

        fun size(): Int {
            return if (values != null) values!!.size else 0
        }

        operator fun contains(value: V): Boolean {
            return values != null && values!!.contains(value)
        }

        fun add(value: V) {
            if (values == null) {
                values = ArrayList()
            }
            values!!.add(value)
        }
    }

    companion object {
        private fun <K, V> updateEntry(entry: LinkedEntry<K, V>) {
            entry.next.prev = entry
            entry.prev.next = entry
        }

        private fun <K, V> removeEntry(entry: LinkedEntry<K, V>) {
            entry.prev.next = entry.next
            entry.next.prev = entry.prev
        }
    }
}
