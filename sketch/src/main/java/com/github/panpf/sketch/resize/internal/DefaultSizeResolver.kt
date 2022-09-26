package com.github.panpf.sketch.resize.internal

import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size

/**
 * Used to identify the default SizeResolver provided by Sketch
 */
data class DefaultSizeResolver(val wrapped: SizeResolver) : SizeResolver {

    override suspend fun size(): Size? {
        return wrapped.size()
    }

    override fun toString(): String {
        return "DefaultSizeResolver($wrapped)"
    }
}