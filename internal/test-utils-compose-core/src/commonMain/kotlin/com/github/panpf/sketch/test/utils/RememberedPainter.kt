package com.github.panpf.sketch.test.utils

import androidx.compose.runtime.RememberObserver
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.PainterWrapper

class RememberedPainter(painter: Painter) : PainterWrapper(painter), RememberObserver {

    var rememberedCount: Int = 0
        private set

    override fun onRemembered() {
        rememberedCount++
    }

    override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount = (rememberedCount - 1).coerceAtLeast(0)
    }

    override fun onAbandoned() {
        onForgotten()
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