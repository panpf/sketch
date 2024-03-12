package com.github.panpf.sketch.decode.internal

import android.graphics.Bitmap


internal fun Bitmap.toLogString(): String =
    "Bitmap@${hashCode().toString(16)}(${width}x${height},$config)"