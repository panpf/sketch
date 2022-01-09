package com.github.panpf.sketch.util

import com.github.panpf.sketch.request.internal.ImageRequest

abstract class SketchException(
    val thenRequest: ImageRequest,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)