package com.github.panpf.sketch.sample.util

import java.math.BigDecimal
import java.math.RoundingMode

fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        BigDecimal(toDouble()).setScale(newScale, RoundingMode.HALF_UP).toFloat()
    }
}

fun String.formatLength(targetLength: Int, padChar: Char = ' '): String {
    return if (this.length >= targetLength) {
        this.substring(0, targetLength)
    } else {
        this.padEnd(targetLength, padChar)
    }
}