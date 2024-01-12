package com.github.panpf.sketch.util

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.Image

fun Image.toBitmap(): Bitmap = when (this) {
    is BitmapImage -> bitmap.copy(bitmap.config, false)
    is DrawableImage -> drawable.toBitmap()
    else -> throw IllegalArgumentException("Unknown image type: $this")
}