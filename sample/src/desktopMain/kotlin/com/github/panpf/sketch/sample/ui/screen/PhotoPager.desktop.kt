package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.PlatformContext

@Composable
actual fun PagerBackground(
    imageUri: String,
    buttonBgColorState: MutableState<Color>,
    screenSize: IntSize,
) {

}

actual fun getTopMargin(context: PlatformContext): Int {
    return 0
}