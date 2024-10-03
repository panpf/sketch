package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback
import com.github.panpf.sketch.drawable.internal.AnimatableCallbackHelper
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toLogString

class SizeAnimatableDrawable(
    drawable: Drawable,
    size: Size
) : SizeDrawable(drawable, size), Animatable2Compat {

    private val callbackHelper = AnimatableCallbackHelper(drawable)

    override fun setDrawable(drawable: Drawable?) {
        callbackHelper.setDrawable(drawable)
        super.setDrawable(drawable)
    }

    override fun start() {
        callbackHelper.start()
    }

    override fun stop() {
        callbackHelper.stop()
    }

    override fun isRunning(): Boolean {
        return callbackHelper.isRunning
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        callbackHelper.registerAnimationCallback(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return callbackHelper.unregisterAnimationCallback(callback)
    }

    override fun clearAnimationCallbacks() {
        callbackHelper.clearAnimationCallbacks()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SizeAnimatableDrawable
        if (drawable != other.drawable) return false
        if (size != other.size) return false
        return true
    }

    override fun hashCode(): Int {
        var result = drawable.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }

    override fun toString(): String {
        return "SizeAnimatableDrawable(drawable=${drawable?.toLogString()}, size=$size)"
    }
}