package com.github.panpf.sketch.sample.ui.util

import kotlin.math.log10
import kotlin.math.pow

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
