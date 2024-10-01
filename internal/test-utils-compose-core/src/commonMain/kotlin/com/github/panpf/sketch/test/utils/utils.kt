package com.github.panpf.sketch.test.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Dp.dp2Px(): Float {
    return with(LocalDensity.current) { toPx() }
}

@Composable
fun Float.dp2Px(): Float {
    return with(LocalDensity.current) { this@dp2Px.dp.toPx() }
}