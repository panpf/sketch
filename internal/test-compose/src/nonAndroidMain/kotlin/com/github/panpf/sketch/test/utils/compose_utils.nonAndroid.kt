package com.github.panpf.sketch.test.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.github.panpf.sketch.Bitmap

actual fun Bitmap.toComposeBitmap(): ImageBitmap = this.asComposeImageBitmap()