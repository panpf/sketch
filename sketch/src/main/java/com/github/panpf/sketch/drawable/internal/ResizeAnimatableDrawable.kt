package com.github.panpf.sketch.drawable.internal

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.SketchAnimatableDrawable
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.util.asOrThrow
import com.github.panpf.sketch.util.requiredMainThread

open class ResizeAnimatableDrawable(
    drawable: SketchAnimatableDrawable,
    resize: Resize
) : ResizeDrawable(drawable, resize), Animatable2Compat {

    override fun start() {
        wrappedDrawable.start()
    }

    override fun stop() {
        wrappedDrawable.stop()
    }

    override fun isRunning(): Boolean {
        return wrappedDrawable.isRunning
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        requiredMainThread()    // Consistent with AnimatedImageDrawable
        wrappedDrawable.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return wrappedDrawable.unregisterAnimationCallback(callback)
    }

    override fun clearAnimationCallbacks() {
        wrappedDrawable.clearAnimationCallbacks()
    }

    override fun toString(): String {
        return "ResizeAnimatableDrawable($wrappedDrawable)"
    }

    @SuppressLint("RestrictedApi")
    override fun getWrappedDrawable(): SketchAnimatableDrawable {
        return super.getWrappedDrawable().asOrThrow()
    }

    @SuppressLint("RestrictedApi")
    override fun setWrappedDrawable(drawable: Drawable) {
        super.setWrappedDrawable(drawable as SketchAnimatableDrawable)
    }

    @SuppressLint("RestrictedApi")
    override fun mutate(): ResizeAnimatableDrawable {
        val mutateDrawable = wrappedDrawable.mutate()
        return if (mutateDrawable !== wrappedDrawable) {
            ResizeAnimatableDrawable(mutateDrawable, resize)
        } else {
            this
        }
    }
}