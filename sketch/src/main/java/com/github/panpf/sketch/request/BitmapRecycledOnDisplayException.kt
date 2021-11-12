package com.github.panpf.sketch.request

import com.github.panpf.sketch.SketchException
import com.github.panpf.sketch.drawable.SketchDrawable

class BitmapRecycledOnDisplayException(
    val request: DisplayRequest,
    val sketchDrawable: SketchDrawable
) : SketchException()