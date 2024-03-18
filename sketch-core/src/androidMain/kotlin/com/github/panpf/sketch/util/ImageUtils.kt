package com.github.panpf.sketch.util

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.AndroidDrawableImage
import com.github.panpf.sketch.Image

fun Image.toBitmap(): Bitmap = when (this) {
    is AndroidBitmapImage -> bitmap.copy(bitmap.config, false)
    is AndroidDrawableImage -> drawable.toBitmap()
    else -> throw IllegalArgumentException("Unknown image type: $this")
}