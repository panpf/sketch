package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.BitmapImage
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.asImage

actual fun createBitmapImage(width: Int, height: Int): BitmapImage =
    SkiaBitmap(width, height).asImage()