package com.github.panpf.sketch.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap as skikoAsComposeImageBitmap
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.Bitmap

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

/**
 * Create an [ImageBitmap] from the given [Bitmap]. Note this does not create a copy of the original
 * [Bitmap] and changes to it will modify the returned [ImageBitmap]
 */
actual fun Bitmap.asComposeImageBitmap(): ImageBitmap {
    return this.skikoAsComposeImageBitmap()
}