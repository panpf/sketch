package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.toLogString

class TestAnimatable2CompatDrawable(
    drawable: Drawable? = null
) : DrawableWrapperCompat(drawable), Animatable2Compat, SketchDrawable {

    private var running = false
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    var callbacks: MutableList<Animatable2Compat.AnimationCallback>? = null

    override fun start() {
        running = true
        handler.post {
            val callbacks = callbacks
            if (callbacks != null) {
                for (callback in callbacks) {
                    callback.onAnimationStart(this)
                }
            }
        }
    }

    override fun stop() {
        running = false
        handler.post {
            val callbacks = callbacks
            if (callbacks != null) {
                for (callback in callbacks) {
                    callback.onAnimationEnd(this)
                }
            }
        }
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun registerAnimationCallback(callback: Animatable2Compat.AnimationCallback) {
        val callbacks = callbacks ?: mutableListOf<Animatable2Compat.AnimationCallback>().apply {
            this@TestAnimatable2CompatDrawable.callbacks = this
        }
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: Animatable2Compat.AnimationCallback): Boolean {
        return callbacks?.remove(callback) == true
    }

    override fun clearAnimationCallbacks() {
        callbacks?.clear()
    }

    override fun mutate(): TestAnimatable2CompatDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            TestAnimatable2CompatDrawable(drawable = mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAnimatable2CompatDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {

        return "TestAnimatable2CompatDrawable(drawable=${drawable?.toLogString()})"
    }
}