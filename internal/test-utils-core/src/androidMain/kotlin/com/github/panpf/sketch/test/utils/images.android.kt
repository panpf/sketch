package com.github.panpf.sketch.test.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.DrawableImage
import com.github.panpf.sketch.Image
import com.github.panpf.sketch.asImage

actual fun createBitmapImage(width: Int, height: Int): BitmapImage =
    Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).asImage()

fun Image.getDrawableOrThrow(): Drawable =
    if (this is DrawableImage)
        drawable else throw IllegalArgumentException("Unable to get Drawable from Image '$this'")