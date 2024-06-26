package com.github.panpf.sketch.state

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.painter.asPainter
import com.github.panpf.sketch.drawable.DrawableEqualizer
import com.github.panpf.sketch.painter.PainterEqualizer


fun Drawable.asPainterEqualizer(equalKey: Any): PainterEqualizer =
    PainterEqualizer(wrapped = this.asPainter(), equalityKey = equalKey)

fun DrawableEqualizer.asPainterEqualizer(): PainterEqualizer {
    return PainterEqualizer(this.wrapped.asPainter(), this.equalityKey)
}