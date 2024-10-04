package com.github.panpf.sketch.test.utils

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.AnimatablePainter
import com.github.panpf.sketch.painter.EquitableAnimatablePainter
import com.github.panpf.sketch.painter.EquitablePainter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.painter.toLogString

fun Painter.asAnimatablePainter(): Painter {
    return if (this is AnimatablePainter) {
        this
    } else {
        TestAnimatablePainter(this)
    }
}

fun Painter.asEquitableWithThis(): EquitablePainter {
    return if (this is AnimatablePainter) {
        EquitableAnimatablePainter(painter = this, equalityKey = this)
    } else {
        EquitablePainter(painter = this, equalityKey = this)
    }
}

class TestAnimatablePainter(painter: Painter) : PainterWrapper(painter), AnimatablePainter {

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAnimatablePainter
        if (painter != other.painter) return false
        return true
    }

    override fun hashCode(): Int {
        return painter.hashCode()
    }

    override fun toString(): String {
        return "TestAnimatablePainter(drawable=${painter.toLogString()})"
    }
}