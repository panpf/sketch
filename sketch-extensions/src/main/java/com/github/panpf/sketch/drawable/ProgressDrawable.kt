package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable
import androidx.annotation.FloatRange

abstract class ProgressDrawable : Drawable() {
    abstract fun updateProgress(@FloatRange(from = 0.0, to = 1.0) newProgress: Float, onAnimationEnd: (() -> Unit)? = null)
}