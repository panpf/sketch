package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.util.Size
import kotlin.math.pow
import kotlin.math.round


fun Long.pow(n: Int): Long = this.toDouble().pow(n).toLong()

suspend fun <T> runBlock(block: suspend () -> T): T {
    return block()
}

fun samplingByTarget(imageSize: Size, targetSize: Size, mimeType: String? = null): Size {
    val sampleSize = calculateSampleSize(imageSize, targetSize, false)
    return calculateSampledBitmapSize(imageSize, sampleSize, mimeType)
}

val Size.ratio: Float
    get() = (width / height.toFloat()).format(1)

/**
 * Convert to the type specified by the generic
 */
inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}