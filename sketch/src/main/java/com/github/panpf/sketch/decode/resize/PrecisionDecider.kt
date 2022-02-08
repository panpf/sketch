package com.github.panpf.sketch.decode.resize

/**
 * Decide which precision to use
 */
fun interface PrecisionDecider {
    fun precision(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision
}