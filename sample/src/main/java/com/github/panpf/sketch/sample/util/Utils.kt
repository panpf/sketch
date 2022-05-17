package com.github.panpf.sketch.sample.util

import java.math.BigDecimal

internal fun Float.format(newScale: Int): Float =
    BigDecimal(toDouble()).setScale(newScale, BigDecimal.ROUND_HALF_UP).toFloat()

fun <T> safeRun(block: () -> T): T? {
    return try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}