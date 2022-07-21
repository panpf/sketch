package com.github.panpf.sketch.test.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Animatable2.AnimationCallback
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawableWrapper

@RequiresApi(23)
class TestAnimatableDrawable2(drawable: Drawable) : DrawableWrapper(drawable), Animatable2 {
    private var running = false
    private var callbacks: MutableList<AnimationCallback> = mutableListOf()
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun start() {
        running = true
        handler.post {
            for (callback in callbacks) {
                callback.onAnimationStart(this)
            }
        }
    }

    override fun stop() {
        running = false
        handler.post {
            for (callback in callbacks) {
                callback.onAnimationEnd(this)
            }
        }
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun registerAnimationCallback(callback: AnimationCallback) {
        callbacks.add(callback)
    }

    override fun unregisterAnimationCallback(callback: AnimationCallback): Boolean {
        return callbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() {
        callbacks.clear()
    }

    @SuppressLint("RestrictedApi")
    override fun mutate(): TestAnimatableDrawable2 {
        val mutateDrawable = wrappedDrawable.mutate()
        return if (mutateDrawable !== wrappedDrawable) {
            TestAnimatableDrawable2(drawable = mutateDrawable)
        } else {
            this
        }
    }
}