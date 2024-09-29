package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat

class TestAnimatableDrawable(
    drawable: Drawable? = null
) : DrawableWrapperCompat(drawable), Animatable {

    private var running = false

    override fun start() {
        running = true
    }

    override fun stop() {
        running = false
    }

    override fun isRunning(): Boolean {
        return running
    }
}