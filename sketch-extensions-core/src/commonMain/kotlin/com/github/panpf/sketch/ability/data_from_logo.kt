package com.github.panpf.sketch.ability

import androidx.annotation.ColorInt
import com.github.panpf.sketch.datasource.DataFrom

@ColorInt
private const val DATA_FROM_COLOR_MEMORY = 0x77008800   // dark green

@ColorInt
private const val DATA_FROM_COLOR_MEMORY_CACHE = 0x7700FF00   // green

@ColorInt
private const val DATA_FROM_COLOR_RESULT_CACHE = 0x77FFFF00 // yellow

@ColorInt
private const val DATA_FROM_COLOR_LOCAL = 0x771E90FF   // dodger blue

@ColorInt
private const val DATA_FROM_COLOR_DOWNLOAD_CACHE = 0x77FF8800 // dark yellow

@ColorInt
private const val DATA_FROM_COLOR_NETWORK = 0x77FF0000  // red

fun dataFromColor(dataFrom: DataFrom): Int = when (dataFrom) {
    DataFrom.MEMORY_CACHE -> DATA_FROM_COLOR_MEMORY_CACHE
    DataFrom.MEMORY -> DATA_FROM_COLOR_MEMORY
    DataFrom.RESULT_CACHE -> DATA_FROM_COLOR_RESULT_CACHE
    DataFrom.DOWNLOAD_CACHE -> DATA_FROM_COLOR_DOWNLOAD_CACHE
    DataFrom.LOCAL -> DATA_FROM_COLOR_LOCAL
    DataFrom.NETWORK -> DATA_FROM_COLOR_NETWORK
}

val dataFromDefaultSize = 20f