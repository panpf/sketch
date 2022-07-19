package com.github.panpf.sketch.resize

/**
 * Decide which precision to use
 */
interface PrecisionDecider {

    val key: String

    fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision
}