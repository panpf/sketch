package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Size

data class DefaultSizeResolver(val wrapped: SizeResolver) : SizeResolver {

    override suspend fun size(): Size? {
        return wrapped.size()
    }

    override fun toString(): String {
        return "DefaultSizeResolver(wrapped=$wrapped)"
    }
}