package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Size

/**
 * Returns the fixed size
 */
data class FixedSizeResolver constructor(private val size: Size) : SizeResolver {

    constructor(width: Int, height: Int) : this(Size(width, height))

    override suspend fun size(): Size {
        return size
    }

    override fun toString(): String {
        return "FixedSizeResolver($size)"
    }
}