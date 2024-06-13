package com.github.panpf.sketch.painter

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.resize.Scale

@Stable
class ResizeAnimatablePainter(
    painter: Painter,
    size: Size,
    scale: Scale
) : ResizePainter(painter, size, scale), AnimatablePainter {

    private val animatablePainter: AnimatablePainter

    init {
        require(painter is AnimatablePainter) {
            "painter must be AnimatablePainter"
        }
        animatablePainter = painter
    }

    override fun start() {
        animatablePainter.start()
    }

    override fun stop() {
        animatablePainter.stop()
    }

    override fun isRunning(): Boolean {
        return animatablePainter.isRunning()
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