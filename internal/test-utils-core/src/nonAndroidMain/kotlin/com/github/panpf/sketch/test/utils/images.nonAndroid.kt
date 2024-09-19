package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.SkiaImage
import com.github.panpf.sketch.asImage
import com.github.panpf.sketch.util.Size

actual fun createBitmapImage(width: Int, height: Int): BitmapImage =
    SkiaBitmap(width, height).asImage()

val SkiaImage.size: Size
    get() = Size(width, height)