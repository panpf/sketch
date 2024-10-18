package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.internal.calculateSampleSize
import com.github.panpf.sketch.decode.internal.calculateSampledBitmapSize
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow
import kotlin.math.round
import kotlin.time.TimeSource


fun Long.pow(n: Int): Long = this.toDouble().pow(n).toLong()

suspend fun <T> runBlock(block: suspend () -> T): T {
    return block()
}

suspend fun <T> runBlock(context: CoroutineContext, block: suspend () -> T): T {
    return withContext(context) {
        block()
    }
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

/**
 * Replacement for delay as delay does not work in runTest
 *
 * Note: Because block will really block the current thread, so please do not use it in the UI thread.
 */
fun block(millis: Long) {
    if (millis > 0) {
        val startTime = TimeSource.Monotonic.markNow()
        while (startTime.elapsedNow().inWholeMilliseconds < millis) {
            // Do nothing
        }
    }
}