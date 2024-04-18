package com.github.panpf.sketch.compose.state

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.compose.painter.asPainter
import com.github.panpf.sketch.state.DrawableEqualWrapper


fun Drawable.asPainterEqualWrapper(equalKey: Any): PainterEqualWrapper =
    PainterEqualWrapper(painter = this.asPainter(), equalKey = equalKey)

fun DrawableEqualWrapper.asPainterEqualWrapper(): PainterEqualWrapper {
    return PainterEqualWrapper(this.drawable.asPainter(), this.equalKey)
}