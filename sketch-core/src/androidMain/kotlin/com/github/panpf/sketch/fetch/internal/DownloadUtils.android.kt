package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.util.getMimeTypeFromUrl


/**
 * Parse the response's `content-type` header.
 *
 * "text/plain" is often used as a default/fallback MIME type.
 * Attempt to guess a better MIME type from the file extension.
 */
internal actual fun getMimeType(url: String, contentType: String?): String? {
    if (contentType.isNullOrEmpty() || contentType.isBlank()) {
        return getMimeTypeFromUrl(url)
    }

    return if (contentType.startsWith(HttpUriFetcher.MIME_TYPE_TEXT_PLAIN)) {
        getMimeTypeFromUrl(url)
    } else {
        null
    } ?: contentType.substringBefore(';')
}