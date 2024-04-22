package com.github.panpf.sketch.painter.internal

import androidx.compose.runtime.MutableIntState

interface DrawInvalidate {

    var drawInvalidateTick: MutableIntState

    fun invalidateDraw() {
        if (drawInvalidateTick.value == Int.MAX_VALUE) {
            drawInvalidateTick.value = 0
        } else {
            drawInvalidateTick.value++
        }
    }
}