package com.github.panpf.sketch.sample.ui.util

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

class WrapperPainter(val wrapped: Painter, val bgColor: Color? = null) : Painter(),
    RememberObserver {

    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null

    /**
     * Drawing a color does not have an intrinsic size, return [Size.Unspecified] here
     */
    override val intrinsicSize: Size = wrapped.intrinsicSize

    override fun DrawScope.onDraw() {
        if (bgColor != null) {
            drawRect(color = bgColor, alpha = alpha, colorFilter = colorFilter)
        }

        with(wrapped) {
            draw(this@onDraw.size, alpha, colorFilter)
        }
    }

    override fun onRemembered() {
        (wrapped as? RememberObserver)?.onRemembered()
    }

    override fun onAbandoned() {
        (wrapped as? RememberObserver)?.onAbandoned()
    }

    override fun onForgotten() {
        (wrapped as? RememberObserver)?.onForgotten()
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as WrapperPainter
        if (wrapped != other.wrapped) return false
        if (bgColor != other.bgColor) return false
        return true
    }

    override fun hashCode(): Int {
        var result = wrapped.hashCode()
        result = 31 * result + bgColor.hashCode()
        return result
    }

    override fun toString(): String {
        return "WrapperPainter(wrapped=${wrapped}, bgColor=${bgColor})"
    }
}