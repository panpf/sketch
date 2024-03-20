package com.github.panpf.sketch.util

import org.jetbrains.skia.Codec

internal fun Codec.toLogString(): String {
    return "Codec@${hashCode().toString(16)}(${width.toFloat()}x${height.toFloat()},${colorType})"
}