package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.cache.CacheKeyMapper

class AppendCacheKeyMapper(val append: String) : CacheKeyMapper {

    override fun map(key: String): String = "${key}${append}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AppendCacheKeyMapper
        if (append != other.append) return false
        return true
    }

    override fun hashCode(): Int {
        return append.hashCode()
    }

    override fun toString(): String = "AppendCacheKeyMapper(append=$append)"
}