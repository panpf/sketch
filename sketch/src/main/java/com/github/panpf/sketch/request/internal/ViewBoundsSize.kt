package com.github.panpf.sketch.request.internal

import com.github.panpf.sketch.decode.resize.NewSize
import com.github.panpf.sketch.util.Size

internal object ViewBoundsSize : NewSize {
    override val size: Size
        get() = throw Exception("Cannot be used directly, normally Sketch will convert the ViewBounds to RealSize")

    override fun toString(): String = "ViewBoundsSize"
}