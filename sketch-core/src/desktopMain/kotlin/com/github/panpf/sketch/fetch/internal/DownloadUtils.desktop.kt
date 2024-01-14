package com.github.panpf.sketch.fetch.internal

/**
 * Parse the response's `content-type` header.
 *
 * "text/plain" is often used as a default/fallback MIME type.
 * Attempt to guess a better MIME type from the file extension.
 */
internal actual fun getMimeType(url: String, contentType: String?): String? {
    // TODO use url
    return contentType?.substringBefore(';')
}