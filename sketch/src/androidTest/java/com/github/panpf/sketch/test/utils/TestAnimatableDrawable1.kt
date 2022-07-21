package com.github.panpf.sketch.test.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapper

class TestAnimatableDrawable1(drawable: Drawable) : DrawableWrapper(drawable), Animatable {
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

    @SuppressLint("RestrictedApi")
    override fun mutate(): TestAnimatableDrawable1 {
        val mutateDrawable = wrappedDrawable.mutate()
        return if (mutateDrawable !== wrappedDrawable) {
            TestAnimatableDrawable1(drawable = mutateDrawable)
        } else {
            this
        }
    }
}