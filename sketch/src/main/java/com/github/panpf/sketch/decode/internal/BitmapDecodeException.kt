package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.util.SketchException

/**
 * Bitmap decode related exception
 */
class BitmapDecodeException constructor(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)