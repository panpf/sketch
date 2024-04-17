package com.github.panpf.sketch.compose.painter

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.internal.toLogString

class DrawableAnimatablePainter(
    drawable: Drawable
) : DrawablePainter(drawable), AnimatablePainter {

    private val animatable: Animatable

    init {
        require(drawable is Animatable) {
            "drawable must be Animatable"
        }
        animatable = drawable
    }

    override fun start() {
        animatable.start()
    }

    override fun stop() {
        animatable.stop()
    }

    override fun isRunning(): Boolean {
        return animatable.isRunning
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DrawableAnimatablePainter) return false
        return drawable == other.drawable
    }

    override fun hashCode(): Int {
        return drawable.hashCode()
    }

    override fun toString(): String {
        return "DrawableAnimatablePainter(drawable=${drawable.toLogString()})"
    }
}