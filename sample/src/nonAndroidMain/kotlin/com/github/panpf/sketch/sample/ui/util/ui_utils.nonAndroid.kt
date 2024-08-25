package com.github.panpf.sketch.sample.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun windowSize(): IntSize {
    val windowInfo = LocalWindowInfo.current
    return windowInfo.containerSize
}