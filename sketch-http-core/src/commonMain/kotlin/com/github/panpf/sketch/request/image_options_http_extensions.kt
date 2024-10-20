package com.github.panpf.sketch.request

import com.github.panpf.sketch.http.HttpHeaders

internal const val HTTP_HEADERS_KEY = "sketch#http_headers"

/**
 * Set headers for http requests
 *
 *
 * @see com.github.panpf.sketch.fetch.HttpUriFetcher
 * @see com.github.panpf.sketch.http.core.common.test.request.ImageOptionsHttpExtensionsTest.testHttpHeaders
 */
val ImageOptions.httpHeaders: HttpHeaders?
    get() = extras?.value<HttpHeaders>(HTTP_HEADERS_KEY)

/**
 * Bulk set headers for any network request for this request
 *
 * @see com.github.panpf.sketch.http.core.common.test.request.ImageOptionsHttpExtensionsTest.testHttpHeaders
 */
fun ImageOptions.Builder.httpHeaders(httpHeaders: HttpHeaders?): ImageOptions.Builder = apply {
    setExtra(key = HTTP_HEADERS_KEY, value = httpHeaders, cacheKey = null)
}