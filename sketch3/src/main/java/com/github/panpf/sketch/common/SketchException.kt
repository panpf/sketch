package com.github.panpf.sketch.common

abstract class SketchException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

class DecodeException(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)