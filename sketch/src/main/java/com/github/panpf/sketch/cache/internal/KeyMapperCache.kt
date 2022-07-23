package com.github.panpf.sketch.cache.internal

import androidx.collection.LruCache

class KeyMapperCache(val maxSize: Int = 100, val mapper: (key: String) -> String) {

    private val cache = LruCache<String, String>(maxSize)

    fun mapKey(key: String): String =
        cache.get(key) ?: mapper(key).apply {
            cache.put(key, this)
        }
}