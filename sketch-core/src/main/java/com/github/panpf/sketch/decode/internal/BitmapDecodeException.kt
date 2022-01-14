package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.request.internal.ImageRequest
import com.github.panpf.sketch.util.SketchException

/**
 * Image decoding related exception
 */
// todo 异常 message 中不再包含 uri 信息
class BitmapDecodeException(
    thenRequest: ImageRequest,
    message: String,
    cause: Throwable? = null
) : SketchException(thenRequest, message, cause)