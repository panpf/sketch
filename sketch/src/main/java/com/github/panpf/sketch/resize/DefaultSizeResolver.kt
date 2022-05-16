package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Size

class DefaultSizeResolver(val wrapped: SizeResolver) : SizeResolver {

    override suspend fun size(): Size? {
        return wrapped.size()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultSizeResolver

        if (wrapped != other.wrapped) return false

        return true
    }

    override fun hashCode(): Int {
        return wrapped.hashCode()
    }

    override fun toString(): String {
        return "DefaultSizeResolver(wrapped=$wrapped)"
    }
}