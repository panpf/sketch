package com.github.panpf.sketch.util

abstract class SketchException constructor(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

class UnknownException constructor(
    message: String,
    cause: Throwable? = null,
) : SketchException(message, cause)