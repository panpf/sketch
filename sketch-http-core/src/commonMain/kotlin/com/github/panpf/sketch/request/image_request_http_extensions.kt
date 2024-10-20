package com.github.panpf.sketch.request

import com.github.panpf.sketch.http.HttpHeaders

/**
 * Set headers for http requests
 *
 *
 * @see com.github.panpf.sketch.fetch.HttpUriFetcher
 * @see com.github.panpf.sketch.http.core.common.test.request.ImageRequestHttpExtensionsTest.testHttpHeaders
 */
val ImageRequest.httpHeaders: HttpHeaders?
    get() = extras?.value<HttpHeaders>(HTTP_HEADERS_KEY)

/**
 * Bulk set headers for any network request for this request
 *
 * @see com.github.panpf.sketch.http.core.common.test.request.ImageRequestHttpExtensionsTest.testHttpHeaders
 */
fun ImageRequest.Builder.httpHeaders(httpHeaders: HttpHeaders?): ImageRequest.Builder = apply {
    setExtra(key = HTTP_HEADERS_KEY, value = httpHeaders, cacheKey = null)
}