package com.github.panpf.sketch.test.utils

import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.sketch.painter.PainterWrapper
import com.github.panpf.sketch.painter.toLogString
import com.github.panpf.sketch.util.Key

class TestKeyPainter(painter: Painter, override val key: String) :
    PainterWrapper(painter), Key {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestKeyPainter
        if (painter != other.painter) return false
        return true
    }

    override fun hashCode(): Int {
        return painter.hashCode()
    }

    override fun toString(): String {
        return "TestKeyPainter(painter=${painter.toLogString()})"
    }
}