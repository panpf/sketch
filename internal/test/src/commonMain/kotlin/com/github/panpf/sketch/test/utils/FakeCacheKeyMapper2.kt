package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.cache.CacheKeyMapper

class FakeCacheKeyMapper2 : CacheKeyMapper {

    override fun map(key: String): String = key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "FakeCacheKeyMapper2"
}