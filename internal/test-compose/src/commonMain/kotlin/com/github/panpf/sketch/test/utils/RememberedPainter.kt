package com.github.panpf.sketch.test.utils

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.util.RememberedCounter

class RememberedPainter(painter: Painter) : PainterWrapper(painter), RememberObserver {

    val rememberedCounter: RememberedCounter = RememberedCounter()

    override fun onRemembered() {
        if (!rememberedCounter.remember()) return
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        if (!rememberedCounter.forget()) return
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as RememberedPainter
        if (painter != other.painter) return false
        return true
    }

    override fun hashCode(): Int {
        return painter.hashCode()
    }
}