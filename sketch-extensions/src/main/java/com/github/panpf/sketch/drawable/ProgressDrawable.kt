package com.github.panpf.sketch.drawable

import android.graphics.drawable.Drawable

/**
 * Abstract definition of progress Drawable
 */
abstract class ProgressDrawable : Drawable() {

    abstract var progress: Float

    abstract var onProgressEnd: (() -> Unit)?

    fun isActive(): Boolean = callback != null && isVisible
}