package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat

class TestAnimatable2CompatDrawable(
    drawable: Drawable? = null
) : DrawableWrapperCompat(drawable), Animatable2Compat {

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
}