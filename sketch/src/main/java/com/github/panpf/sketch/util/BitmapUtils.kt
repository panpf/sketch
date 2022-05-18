package com.github.panpf.sketch.util

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

internal val Bitmap.allocationByteCountCompat: Int
    get() {
        return when {
            this.isRecycled -> 0
            VERSION.SDK_INT >= VERSION_CODES.KITKAT -> this.allocationByteCount
            else -> this.byteCount
        }
    }

internal fun Bitmap.toInfoString(): String =
    "Bitmap(width=${width}, height=${height}, config=$config)"

internal fun Bitmap.toShortInfoString(): String = "Bitmap(${width}x${height},$config)"

/**
 * Gets the number of bytes occupied by a single pixel in a specified configuration
 */
internal fun Bitmap.Config?.getBytesPerPixel(): Int {
    // A bitmap by decoding a gif has null "config" in certain environments.
    val config = this ?: Bitmap.Config.ARGB_8888
    @Suppress("DEPRECATION")
    return when {
        config == Bitmap.Config.ALPHA_8 -> 1
        config == Bitmap.Config.RGB_565 || config == Bitmap.Config.ARGB_4444 -> 2
        config == Bitmap.Config.ARGB_8888 -> 4
        VERSION.SDK_INT >= VERSION_CODES.O && config == Bitmap.Config.RGBA_F16 -> 8
        else -> 4
    }
}

/**
 * The number of bytes required for calculation based on width, height, and configuration
 */
internal fun computeByteCount(width: Int, height: Int, config: Bitmap.Config?): Int {
    return width * height * config.getBytesPerPixel()
}

/**
 * Get the appropriate compression format according to the [Bitmap] configuration
 */
@Suppress("unused")
internal val Bitmap.Config?.getCompressFormat: CompressFormat
    get() = if (this == Bitmap.Config.RGB_565) CompressFormat.JPEG else CompressFormat.PNG

internal fun Bitmap.Config.isAndSupportHardware(): Boolean =
    VERSION.SDK_INT >= VERSION_CODES.O && this == Bitmap.Config.HARDWARE