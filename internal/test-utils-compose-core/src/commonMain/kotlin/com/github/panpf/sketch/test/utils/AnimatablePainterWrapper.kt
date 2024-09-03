package com.github.panpf.sketch.test.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.AnimatablePainter

fun Painter.asAnimatable(): Painter {
    return AnimatablePainterWrapper(this)
}

class AnimatablePainterWrapper(val painter: Painter) : Painter(), AnimatablePainter {

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    override val intrinsicSize: Size
        get() = painter.intrinsicSize

    override fun DrawScope.onDraw() {
        with(painter) {
            draw(size, alpha, colorFilter)
        }
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun isRunning(): Boolean {
        return false
    }
}