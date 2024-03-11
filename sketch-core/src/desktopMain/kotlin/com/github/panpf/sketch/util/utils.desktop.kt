package com.github.panpf.sketch.util

import java.awt.color.ColorSpace
import java.awt.image.BufferedImage


internal actual fun isMainThread(): Boolean {
    // TODO implement
    return true
}

internal actual fun requiredMainThread() {
    // TODO implement
}

internal actual fun requiredWorkThread() {
    // TODO implement
}

internal actual fun getMimeTypeFromExtension(extension: String): String? {
    // TODO implement
    return null
}

internal fun BufferedImage.toLogString(): String {
    return "BufferedImage@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorModel.colorSpace.typeName})"
}

internal val ColorSpace.typeName: String
    get() = when (type) {
        ColorSpace.TYPE_CMYK -> "CMYK"
        ColorSpace.TYPE_GRAY -> "GRAY"
        ColorSpace.TYPE_RGB -> "RGB"
        ColorSpace.TYPE_HLS -> "HLS"
        else -> "Unknown"
    }