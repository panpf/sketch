package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.SketchException

/**
 * Image decoding related exception
 */
class DecodeBitmapException(
    thenRequest: ImageRequest,
    message: String,
    cause: Throwable? = null
) : SketchException(thenRequest, message, cause)