package com.github.panpf.sketch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize

@Composable
actual fun getWindowContainerSize(): IntSize {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    return remember(displayMetrics) {
        IntSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}