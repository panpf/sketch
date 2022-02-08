package com.github.panpf.sketch.decode.resize

import com.github.panpf.sketch.util.Size

fun NewSize(size: Size): NewSize = RealNewSize(size)

fun NewSize(width: Int, height: Int): NewSize = RealNewSize(width, height)

interface NewSize {
    val size: Size
}

data class RealNewSize(override val size: Size) : NewSize {
    constructor(width: Int, height: Int) : this(Size(width, height))

    override fun toString(): String = "RealNewSize(${size.width}x${size.height})"
}