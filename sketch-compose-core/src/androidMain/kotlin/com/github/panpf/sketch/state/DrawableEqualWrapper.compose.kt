package com.github.panpf.sketch.state

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.state.DrawableEqualWrapper
import com.github.panpf.sketch.state.PainterEqualWrapper


fun Drawable.asPainterEqualWrapper(equalKey: Any): PainterEqualWrapper =
    PainterEqualWrapper(painter = this.asPainter(), equalKey = equalKey)

fun DrawableEqualWrapper.asPainterEqualWrapper(): PainterEqualWrapper {
    return PainterEqualWrapper(this.drawable.asPainter(), this.equalKey)
}