package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange

abstract class ProgressDrawable : Drawable() {

    @get:FloatRange(from = 0.0, to = 1.0)
    abstract var progress: Float

    abstract fun animUpdateProgress(
        @FloatRange(from = 0.0, to = 1.0) newProgress: Float,
        onAnimationEnd: (() -> Unit)? = null
    )

    fun isActive(): Boolean = callback != null && isVisible
}