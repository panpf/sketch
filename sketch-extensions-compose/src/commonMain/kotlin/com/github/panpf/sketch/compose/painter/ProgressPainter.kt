package com.github.panpf.sketch.compose.painter

import androidx.compose.ui.graphics.painter.Painter

abstract class ProgressPainter : Painter(), DrawInvalidate {

    /**
     * Progress, range is -1f to 1f. Less than 0f means hidden, 0f means indeterminate progress, 1f means completed
     */
    abstract var progress: Float

}