package com.github.panpf.sketch

sealed class SketchException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Download process-related exceptions
 */
class DownloadException(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)

/**
 * Load process-related exceptions
 */
class LoadException(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)

/**
 * Display process-related exceptions
 */
class DisplayException(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)

/**
 * Image decoding related exception
 */
class DecodeException(
    message: String,
    cause: Throwable? = null
) : SketchException(message, cause)