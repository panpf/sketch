package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.cache.CacheKeyMapper

class MyCacheKeyMapper(val key: String) : CacheKeyMapper {

    override fun map(key: String): String = this@MyCacheKeyMapper.key

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MyCacheKeyMapper
        if (key != other.key) return false
        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return "MyCacheKeyMapper(key='$key')"
    }
}