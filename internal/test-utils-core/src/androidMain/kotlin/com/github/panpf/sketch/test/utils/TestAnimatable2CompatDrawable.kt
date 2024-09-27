package com.github.panpf.sketch.test.utils

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat

class TestAnimatable2CompatDrawable : Drawable(), Animatable2Compat {

    private var running = false

    override fun draw(canvas: Canvas) {

    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun start() {
        running = true
    }

    override fun stop() {
        running = false
    }

    override fun isRunning(): Boolean = running

    var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null
    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        val callbacks = callbacks
            ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
                this@TestAnimatable2CompatDrawable.callbacks = this
            }
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks?.remove(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbacks?.clear()
    }
}