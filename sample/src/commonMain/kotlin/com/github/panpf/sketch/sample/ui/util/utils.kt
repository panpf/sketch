package com.github.panpf.sketch.sample.ui.util

import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Returns the formatted file length that can be displayed, up to EB
 *
 * @receiver              File size
 * @param decimalPlacesLength   Keep a few decimal places
 * @param decimalPlacesFillZero Use 0 instead when the number of decimal places is insufficient
 * @param compact               If true, returns 150KB, otherwise returns 150 KB
 * @return For example: 300 B, 150.25 KB, 500.46 MB, 300 GB
 */
internal fun Long.formatFileSize(decimals: Int = 1): String {
    val bytes = this
    return when {
        bytes < 1024 -> {
            "$bytes B"
        }

        bytes < 1_048_576 -> {
            "${(bytes / 1_024f).formatWithDecimals(decimals)} KB"
        }

        bytes < 1.07374182E9f -> {
            "${(bytes / 1_048_576f).formatWithDecimals(decimals)} MB"
        }

        bytes < 1.09951163E12f -> {
            "${(bytes / 1.07374182E9f).formatWithDecimals(decimals)} GB"
        }

        else -> {
            "${(bytes / 1.09951163E12f).formatWithDecimals(decimals)} TB"
        }
    }
}

private fun Float.formatWithDecimals(decimals: Int): String {
    val multiplier = 10.0.pow(decimals)
    val numberAsString = (this * multiplier).roundToLong().toString()
    val decimalIndex = numberAsString.length - decimals - 1
    val mainRes = numberAsString.substring(0..decimalIndex)
    val fractionRes = numberAsString.substring(decimalIndex + 1)
    return if (fractionRes.isEmpty()) {
        mainRes
    } else {
        "$mainRes.$fractionRes"
    }
}