package com.github.panpf.sketch.sample.util

import com.github.panpf.sketch.decode.Decoder
import okio.ByteString.Companion.encodeUtf8
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
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

fun Double.format(newScale: Int): Double {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier)
    }
}

fun String.formatLength(targetLength: Int, padChar: Char = ' '): String {
    return if (this.length >= targetLength) {
        this.substring(0, targetLength)
    } else {
        this.padEnd(targetLength, padChar)
    }
}

fun String.sha256String() = encodeUtf8().sha256().hex()

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

@OptIn(ExperimentalContracts::class)
inline fun <T> T.ifLet(predicate: Boolean, block: (T) -> T): T {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return if (predicate) block(this) else this
}

expect fun platformGifDecoders(): List<Decoder.Factory>