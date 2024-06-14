package com.github.panpf.sketch.animated.android.test.internal

import android.graphics.Canvas
import com.github.panpf.sketch.transform.AnimatedTransformation
import com.github.panpf.sketch.transform.PixelOpacity

object TranslucentAnimatedTransformation : AnimatedTransformation {
    override val key: String = "TranslucentAnimatedTransformation"

    override fun transform(canvas: Canvas): PixelOpacity {
        return PixelOpacity.TRANSLUCENT
    }
}