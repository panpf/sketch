package com.github.panpf.sketch.decode

import org.jetbrains.skia.ColorType

/**
 * Decode configuration
 *
 * @see com.github.panpf.sketch.core.nonandroid.test.decode.DecodeConfigTest
 */
// TODO Use it
class DecodeConfig {

    /**
     * If set to a value > 1, requests the decoder to subsample the original
     * image, returning a smaller image to save memory. The sample size is
     * the number of pixels in either dimension that correspond to a single
     * pixel in the decoded bitmap. For example, inSampleSize == 4 returns
     * an image that is 1/4 the width/height of the original, and 1/16 the
     * number of pixels. Any value <= 1 is treated the same as 1. Note: the
     * decoder uses a final value based on powers of 2, any other value will
     * be rounded down to the nearest power of 2.
     */
    var inSampleSize: Int? = null

    /**
     * Color configuration
     */
    var colorType: ColorType? = null
}