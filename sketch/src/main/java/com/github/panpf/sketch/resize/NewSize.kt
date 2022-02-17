package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Size

fun NewSize(size: Size): NewSize = RealNewSize(size.width, size.height)

fun NewSize(width: Int, height: Int): NewSize = RealNewSize(width, height)

interface NewSize {
    val width: Int
    val height: Int
}

data class RealNewSize(override val width: Int, override val height: Int) : NewSize {

    override fun toString(): String = "RealNewSize(${width}x${height})"
}