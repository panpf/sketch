package com.github.panpf.sketch.compose.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.compose.painter.internal.toLogString
import com.github.panpf.sketch.resize.Scale

class ResizeAnimatablePainter(
    painter: Painter,
    size: Size,
    scale: Scale
) : ResizePainter(painter, size, scale), Animatable {

    private val animatable: Animatable

    init {
        require(painter is Animatable) {
            "painter must be AnimatablePainter"
        }
        animatable = painter
    }

    override fun start() {
        animatable.start()
    }

    override fun stop() {
        animatable.stop()
    }

    override fun isRunning(): Boolean {
        return animatable.isRunning()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResizeAnimatablePainter) return false
        if (painter != other.painter) return false
        if (size != other.size) return false
        return scale == other.scale
    }

    override fun hashCode(): Int {
        var result = painter.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + scale.hashCode()
        return result
    }

    override fun toString(): String {
        return "ResizeAnimatablePainter(painter=${painter.toLogString()}, size=${size.width}x${size.height}, scale=$scale)"
    }
}