package com.github.panpf.sketch.test.utils

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.github.panpf.sketch.target.ComposeTarget

class TestComposeTarget : ComposeTarget {

    override var painter: Painter? = null
    override val contentScale: ContentScale
        get() = ContentScale.Fit
    override val alignment: Alignment
        get() = Alignment.Center

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestComposeTarget
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return "TestComposeTarget"
    }
}