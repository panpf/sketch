package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.ImageTransformer
import com.github.panpf.sketch.cache.MemoryCache.Value
import com.github.panpf.sketch.util.SketchSize

class FakeImage(val size: SketchSize) : Image {

    override val width: Int
        get() = size.width

    override val height: Int
        get() = size.height

    override val byteCount: Long
        get() = size.width * size.height * 4L

    override val allocationByteCount: Long
        get() = byteCount

    override val shareable: Boolean
        get() = true

    override fun cacheValue(extras: Map<String, Any?>?): Value? = null

    override fun checkValid(): Boolean {
        return true
    }

    override fun transformer(): ImageTransformer? {
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FakeImage
        return size == other.size
    }

    override fun hashCode(): Int {
        return size.hashCode()
    }

    override fun toString(): String = "FakeImage(size=$size)"
}