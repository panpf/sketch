package com.github.panpf.sketch.sample.ui.test

import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity
import com.github.panpf.sketch.util.Rect

expect object TestAnimatedTransformation : AnimatedTransformation {

    override fun transform(
        canvas: Any,
        bounds: Rect
    ): PixelOpacity

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
    override fun toString(): String
    override val key: String
}