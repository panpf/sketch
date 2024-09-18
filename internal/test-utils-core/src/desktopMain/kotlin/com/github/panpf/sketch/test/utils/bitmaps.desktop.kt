package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Bitmap
import org.jetbrains.skiko.toBufferedImage

actual fun Bitmap.toPreviewBitmap(): Any = this.toBufferedImage()