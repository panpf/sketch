package com.github.panpf.sketch.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize

/**
 * Get window container size
 *
 * @see com.github.panpf.sketch.compose.core.android.test.util.AsyncImageStateAndroidTest.testWindowContainerSize
 */
@Composable
actual fun windowContainerSize(): IntSize {
    val displayMetrics = LocalContext.current.resources.displayMetrics
    return remember(displayMetrics) {
        IntSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}