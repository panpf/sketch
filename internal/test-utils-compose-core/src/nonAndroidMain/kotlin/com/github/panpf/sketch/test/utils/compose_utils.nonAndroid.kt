package com.github.panpf.sketch.test.utils

import androidx.compose.ui.graphics.asComposeImageBitmap
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.ComposeBitmap

actual fun Bitmap.toComposeBitmap(): ComposeBitmap = this.asComposeImageBitmap()