package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.resize.NewSize

internal object ViewBoundsSize : NewSize {

    override val width: Int
        get() = throw Exception("Cannot be used directly, normally Sketch will convert the ViewBounds to RealSize")
    override val height: Int
        get() = throw Exception("Cannot be used directly, normally Sketch will convert the ViewBounds to RealSize")

    override fun toString(): String = "ViewBoundsSize"
}