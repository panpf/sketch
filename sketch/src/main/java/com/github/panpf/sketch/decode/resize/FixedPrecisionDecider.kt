package com.github.panpf.sketch.decode.resize

/**
 * Always return specified precision
 */
data class FixedPrecisionDecider(private val precision: Precision) : PrecisionDecider {

    override fun precision(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        return precision
    }

    override fun toString(): String = "FixedPrecisionDecider($precision)"
}

fun fixedPrecision(precision: Precision): FixedPrecisionDecider = FixedPrecisionDecider(precision)