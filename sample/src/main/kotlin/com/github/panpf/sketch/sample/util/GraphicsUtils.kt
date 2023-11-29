package com.github.panpf.sketch.sample.util

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.util.Size


val Drawable.intrinsicSize: Size
    get() = Size(intrinsicWidth, intrinsicHeight)