package com.github.panpf.sketch.compose.stateimage

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.state.ResColor
import com.github.panpf.sketch.state.StateImage

@Composable
fun rememberColorPainterStateImage(@ColorRes color: Int): StateImage = remember(color) {
    ColorFetcherPainterStateImage(ResColor(color))
}