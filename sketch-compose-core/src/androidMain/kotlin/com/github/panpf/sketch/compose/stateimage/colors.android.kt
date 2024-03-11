package com.github.panpf.sketch.compose.stateimage

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.sketch.stateimage.StateImage

@Composable
fun rememberColorPainterStateImage(@ColorRes color: Int): StateImage = remember(color) {
    ColorFetcherPainterStateImage(ResColor(color))
}