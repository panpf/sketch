package com.github.panpf.sketch.resize

fun fixedPrecision(precision: Precision): FixedPrecisionDecider = FixedPrecisionDecider(precision)

/**
 * Always return specified precision
 */
data class FixedPrecisionDecider(private val precision: Precision) : PrecisionDecider {

    override val key: String by lazy { "Fixed($precision)" }

    override fun get(
        imageWidth: Int, imageHeight: Int, resizeWidth: Int, resizeHeight: Int
    ): Precision {
        return precision
    }

    override fun toString(): String {
        return "FixedPrecisionDecider(precision=$precision)"
    }
}