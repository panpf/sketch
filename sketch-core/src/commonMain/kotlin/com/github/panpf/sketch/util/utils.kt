/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.util

import androidx.annotation.WorkerThread
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.datasource.DataSource
import com.github.panpf.sketch.request.ImageRequest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okio.IOException
import okio.Path
import okio.buffer
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round


internal inline fun <R> ifOrNull(value: Boolean, block: () -> R?): R? = if (value) block() else null

/**
 * Convert to the type specified by the generic, if this is null or cannot be converted return null
 */
internal inline fun <reified R> Any?.asOrNull(): R? {
    return if (this != null && this is R) this else null
}

/**
 * Convert to the type specified by the generic
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun <R> Any.asOrThrow(): R {
    @Suppress("UNCHECKED_CAST")
    return this as R
}

internal expect fun isMainThread(): Boolean

internal expect fun requiredMainThread()

internal expect fun requiredWorkThread()

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Deferred<T>.getCompletedOrNull(): T? {
    return try {
        getCompleted()
    } catch (_: Throwable) {
        null
    }
}

internal fun Any.toHexString(): String = Integer.toHexString(this.hashCode())

internal fun Float.format(newScale: Int): Float {
    return if (this.isNaN()) {
        this
    } else {
        val multiplier = 10.0.pow(newScale)
        (round(this * multiplier) / multiplier).toFloat()
    }
}

/**
 * Returns the formatted file length that can be displayed, up to EB
 *
 * @receiver              File size
 * @param decimalPlacesLength   Keep a few decimal places
 * @param decimalPlacesFillZero Use 0 instead when the number of decimal places is insufficient
 * @param compact               If true, returns 150KB, otherwise returns 150 KB
 * @return For example: 300 B, 150.25 KB, 500.46 MB, 300 GB
 */
internal fun Long.formatFileSize(
    decimalPlacesLength: Int = 2,
    decimalPlacesFillZero: Boolean = false,
    compact: Boolean = true,
): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")
    if (this <= 0) {
        return if (compact) "0B" else "0 B"
    }
    val formatString = buildString {
        append("%.")
        append(decimalPlacesLength)
        append(if (decimalPlacesFillZero) "0f" else "f")
        if (compact) {
            append("%s")
        } else {
            append(" %s")
        }
    }
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    val value = this / 1024.0.pow(digitGroups.toDouble())
    return formatString.format(value, units[digitGroups])

//    // Multiplied by 999 to avoid 1000 KB, 1000 MB
//    // Why is appendSuffix required to be true when calling the format method, because DecimalFormat encounters '#.##EB' and throws an exception 'IllegalArgumentException: Malformed exponential pattern "#.##EB"'
//    @Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
//    val finalFileSize = Math.max(this, 0)
//    return if (finalFileSize <= 999) {
//        finalFileSize.toString() + if (compact) "B" else " B"
//    } else {
//        val value: Double
//        val suffix: String
//        if (finalFileSize <= 1024L * 999) {
//            value = (finalFileSize / 1024f).toDouble()
//            suffix = if (compact) "KB" else " KB"
//        } else if (finalFileSize <= 1024L * 1024 * 999) {
//            value = (finalFileSize / 1024f / 1024f).toDouble()
//            suffix = if (compact) "MB" else " MB"
//        } else if (finalFileSize <= 1024L * 1024 * 1024 * 999) {
//            value = (finalFileSize / 1024f / 1024f / 1024f).toDouble()
//            suffix = if (compact) "GB" else " GB"
//        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 999) {
//            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f).toDouble()
//            suffix = if (compact) "TB" else " TB"
//        } else if (finalFileSize <= 1024L * 1024 * 1024 * 1024 * 1024 * 999) {
//            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
//            suffix = if (compact) "PB" else " PB"
//        } else {
//            value = (finalFileSize / 1024f / 1024f / 1024f / 1024f / 1024f / 1024f).toDouble()
//            suffix = if (compact) "EB" else " EB"
//        }
//        val buffString = StringBuilder()
//        buffString.append("#")
//        if (decimalPlacesLength > 0) {
//            buffString.append(".")
//            for (w in 0 until decimalPlacesLength) {
//                buffString.append(if (decimalPlacesFillZero) "0" else "#")
//            }
//        }
//        val format = DecimalFormat(buffString.toString())
//        format.roundingMode = RoundingMode.HALF_UP
//        format.format(value).toString() + suffix
//    }
}

internal fun Int.formatFileSize(): String = toLong().formatFileSize()

internal fun intMerged(highInt: Int, lowInt: Int): Int {
    require(highInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'highInt' is 0 to ${Short.MAX_VALUE}"
    }
    require(lowInt in 0.rangeTo(Short.MAX_VALUE)) {
        "The value range for 'lowInt' is 0 to ${Short.MAX_VALUE}"
    }
    val high2 = highInt shl 16
    val low2 = (lowInt shl 16) shr 16
    return high2 or low2
}

internal fun intSplit(value: Int): Pair<Int, Int> {
    return (value shr 16) to ((value shl 16) shr 16)
}

@WorkerThread
@Throws(IOException::class)
internal fun getDataSourceCacheFile(
    sketch: Sketch,
    request: ImageRequest,
    dataSource: DataSource,
): Path = runBlocking {
    val resultCache = sketch.resultCache
    val resultCacheKey = request.uriString + "_data_source"
    val snapshot = resultCache.withLock(resultCacheKey) {
        val snapshot = resultCache.openSnapshot(resultCacheKey)
        if (snapshot != null) {
            snapshot
        } else {
            val editor = resultCache.openEditor(resultCacheKey)
                ?: throw IOException("Disk cache cannot be used")
            try {
                dataSource.openSource().use { source ->
                    resultCache.fileSystem.sink(editor.data).buffer().use { sink ->
                        sink.writeAll(source)
                    }
                }
                editor.commitAndOpenSnapshot()
            } catch (e: Throwable) {
                editor.abort()
                throw e
            }
        }
    } ?: throw IOException("Disk cache cannot be used after edit")
    snapshot.use { it.data }
}


/**
 * Gets a power of 2 that is less than or equal to the given integer
 *
 * Examples: -1->1，0->1，1->1，2->2，3->2，4->4，5->4，6->4，7->4，8->8，9->8
 */
internal fun floorRoundPow2(number: Int): Int {
    return number.takeHighestOneBit().coerceAtLeast(1)
}

/**
 * Gets a power of 2 that is greater than or equal to the given integer
 *
 * Examples: -1->1，0->1，1->1，2->2，3->4，4->4，5->8，6->8，7->8，8->8，9->16
 *
 * Copy from Java 17 'HashMap.tableSizeFor()' method
 */
internal fun ceilRoundPow2(number: Int): Int {
    val n = -1 ushr (number - 1).countLeadingZeroBits()
    return if (n < 0) 1 else if (n >= 1073741824) 1073741824 else n + 1
}

internal expect fun getMimeTypeFromExtension(extension: String): String?