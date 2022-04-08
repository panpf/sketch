package com.github.panpf.sketch.util

import com.github.panpf.sketch.request.ImageRequest

abstract class SketchException constructor(
    val thenRequest: ImageRequest,
    message: String?,
    cause: Throwable? = null,
) : Exception(message, cause)

class OtherException constructor(
    thenRequest: ImageRequest,
    message: String?,
    cause: Throwable? = null,
) : SketchException(thenRequest, message, cause)