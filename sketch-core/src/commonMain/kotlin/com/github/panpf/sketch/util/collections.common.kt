package com.github.panpf.sketch.util

/**
 * Create a [MutableMap] that orders its entries by most recently used to least recently used.
 *
 * https://youtrack.jetbrains.com/issue/KT-52183
 */
internal expect fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int = 0,
    loadFactor: Float = 0.75F,
): MutableMap<K, V>

/** @see forEach */
@PublishedApi // Used by extension modules.
internal inline fun <T> List<T>.forEachIndices(action: (index: Int, T) -> Unit) {
    for (i in indices) {
        action(i, get(i))
    }
}