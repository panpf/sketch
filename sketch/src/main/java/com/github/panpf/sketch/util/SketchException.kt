package com.github.panpf.sketch.util

import com.github.panpf.sketch.request.internal.ImageRequest

open class SketchException constructor(
    val thenRequest: ImageRequest,
    message: String?,
    cause: Throwable? = null,
) : Exception(message, cause)