package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.util.SketchSize

data class FakeImage constructor(val size: SketchSize, val valid: Boolean = true) : Image {

    constructor(width: Int, height: Int, valid: Boolean = true)
            : this(SketchSize(width, height), valid)

    override val width: Int
        get() = size.width

    override val height: Int
        get() = size.height

    override val byteCount: Long
        get() = size.width * size.height * 4L

    override val shareable: Boolean = true

    override fun checkValid(): Boolean = valid

    override fun toString(): String = "FakeImage(size=$size)"
}