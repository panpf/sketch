package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.util.SketchException

/**
 * Bitmap decode related exception
 */
class BitmapDecodeException constructor(
    thenRequest: LoadRequest,
    message: String,
    cause: Throwable? = null
) : SketchException(thenRequest, message, cause)