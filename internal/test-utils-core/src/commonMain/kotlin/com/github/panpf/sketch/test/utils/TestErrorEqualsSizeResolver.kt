package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.resize.SizeResolver
import com.github.panpf.sketch.util.Size

class TestErrorEqualsSizeResolver(val size1: Size) : SizeResolver {

    override suspend fun size(): Size {
        return size1
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return super.toString()
    }

    override val key: String = "Fixed($size1)"
}