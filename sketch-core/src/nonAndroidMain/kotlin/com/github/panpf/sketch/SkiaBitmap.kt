package com.github.panpf.sketch

import org.jetbrains.skia.ImageInfo

typealias SkiaBitmap = org.jetbrains.skia.Bitmap

fun SkiaBitmap(imageInfo: ImageInfo): SkiaBitmap = SkiaBitmap()
    .apply { allocPixels(imageInfo) }