package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.SkiaBitmapImage
import com.github.panpf.sketch.util.readIntPixels

/**
 * Get the pixels of the image
 */
actual fun Image.readIntPixels(x: Int, y: Int, width: Int, height: Int): IntArray {
    return (this as SkiaBitmapImage).bitmap.readIntPixels(x, y, width, height)
}