package com.github.panpf.sketch.drawable.internal

import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.resize.Resize

open class ResizeAnimatableDrawable(val drawable: SketchAnimatableDrawable, resize: Resize) :
    ResizeDrawable(drawable, resize), Animatable2Compat {

    override fun start() {
        drawable.start()
    }

    override fun stop() {
        drawable.start()
    }

    override fun isRunning(): Boolean {
        return drawable.isRunning
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        drawable.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return drawable.unregisterAnimationCallback(callback)
    }

    override fun clearAnimationCallbacks() {
        drawable.clearAnimationCallbacks()
    }
}