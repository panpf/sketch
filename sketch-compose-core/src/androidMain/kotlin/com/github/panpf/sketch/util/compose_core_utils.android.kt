package com.github.panpf.sketch.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.github.panpf.sketch.Bitmap

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

/**
 * Create an [ImageBitmap] from the given [Bitmap]. Note this does not create a copy of the original
 * [Bitmap] and changes to it will modify the returned [ImageBitmap]
 */
actual fun Bitmap.asComposeImageBitmap(): ImageBitmap {
    return this.asImageBitmap()
}