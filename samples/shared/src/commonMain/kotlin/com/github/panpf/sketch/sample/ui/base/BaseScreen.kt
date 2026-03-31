package com.github.panpf.sketch.sample.ui.base

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun BaseScreen(content: @Composable () -> Unit) {
    Surface {
        content()
    }
}