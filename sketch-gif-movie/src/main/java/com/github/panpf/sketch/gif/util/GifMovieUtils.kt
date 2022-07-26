package com.github.panpf.sketch.gif.util

import android.graphics.Bitmap
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