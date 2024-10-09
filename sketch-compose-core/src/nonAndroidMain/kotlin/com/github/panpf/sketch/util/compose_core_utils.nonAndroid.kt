package com.github.panpf.sketch.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

/**
 * Get window container size
 *
 * @see com.github.panpf.sketch.compose.core.nonandroid.test.util.AsyncImageStateNonAndroidTest.testWindowContainerSize
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun windowContainerSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}