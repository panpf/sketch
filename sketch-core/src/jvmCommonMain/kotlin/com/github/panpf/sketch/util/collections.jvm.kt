package com.github.panpf.sketch.util

internal actual fun <K : Any, V : Any> LruMutableMap(
    initialCapacity: Int,
    loadFactor: Float,
): MutableMap<K, V> = LinkedHashMap(initialCapacity, loadFactor, true)
