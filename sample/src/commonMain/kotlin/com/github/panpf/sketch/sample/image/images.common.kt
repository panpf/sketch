package com.github.panpf.sketch.sample.image

import com.github.panpf.sketch.Image

/**
 * Get the pixels of the image
 */
expect fun Image.readIntPixels(
    x: Int = 0,
    y: Int = 0,
    width: Int = this.width,
    height: Int = this.height
): IntArray