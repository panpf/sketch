package com.github.panpf.sketch.sample.util

import kotlin.math.pow
import kotlin.math.round

fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

fun String.formatLength(targetLength: Int, padChar: Char = ' '): String {
    return if (this.length >= targetLength) {
        this.substring(0, targetLength)
    } else {
        this.padEnd(targetLength, padChar)
    }
}