package com.github.panpf.sketch.drawable.internal

import android.content.Context
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.resize.Resize

open class ResizeAnimatableDrawable(context: Context, val drawable: SketchAnimatableDrawable, resize: Resize) :
    ResizeDrawable(context, drawable, resize), Animatable2Compat {

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