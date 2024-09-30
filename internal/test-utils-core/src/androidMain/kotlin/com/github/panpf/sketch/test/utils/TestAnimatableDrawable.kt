package com.github.panpf.sketch.test.utils

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.util.toLogString

class TestAnimatableDrawable constructor(
    drawable: Drawable
) : DrawableWrapperCompat(drawable), Animatable, SketchDrawable {

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

    override fun mutate(): TestAnimatableDrawable {
        val mutateDrawable = drawable?.mutate()
        return if (mutateDrawable != null && mutateDrawable !== drawable) {
            TestAnimatableDrawable(drawable = mutateDrawable)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAnimatableDrawable
        if (drawable != other.drawable) return false
        return true
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "TestAnimatableDrawable(drawable=${drawable?.toLogString()})"
    }
}