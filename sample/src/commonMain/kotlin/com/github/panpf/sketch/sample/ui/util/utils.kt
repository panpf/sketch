package com.github.panpf.sketch.sample.ui.util

import kotlin.math.pow
import kotlin.math.round


internal fun Double.format(newScale: Int): Double {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier)
    }
}

/**
 * Returns the this size in human-readable format.
 */
internal fun Long.formatFileSize(decimals: Int = 1): String {
    val doubleString: (Double) -> String = { number ->
        if (number % 1 == 0.0) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }
    val finalFileSize: Double = this.coerceAtLeast(0L).toDouble()
    if (finalFileSize < 1000.0) return "${doubleString(finalFileSize)}B"
    val units = listOf("KB", "MB", "GB", "TB", "PB")
    units.forEachIndexed { index, suffix ->
        val powValue: Double = 1024.0.pow(index + 1)
        val powMaxValue: Double = powValue * 1000
        if (finalFileSize < powMaxValue || index == units.size - 1) {
            val value: Double = finalFileSize / powValue
            val formattedValue = value.format(decimals)
            return "${doubleString(formattedValue)}${suffix}"
        }
    }
    throw IllegalStateException("Can't format file size: $this")
}