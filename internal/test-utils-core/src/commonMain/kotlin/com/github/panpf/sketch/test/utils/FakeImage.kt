package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.util.SketchSize

class FakeImage(val size: SketchSize) : Image {

    constructor(width: Int, height: Int) : this(SketchSize(width, height))

    override val width: Int
        get() = size.width

    override val height: Int
        get() = size.height

    override val byteCount: Long
        get() = size.width * size.height * 4L

    override val shareable: Boolean
        get() = true

    override val cacheInMemory: Boolean = false

    override fun checkValid(): Boolean {
        return true
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