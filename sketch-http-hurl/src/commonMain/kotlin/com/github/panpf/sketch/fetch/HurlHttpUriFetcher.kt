package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.http.HurlStack
import com.github.panpf.sketch.http.HurlStack.Builder
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext

/**
 * Adds HurlHttpUriFetcher support
 *
 * @see com.github.panpf.sketch.http.hurl.common.test.fetch.HurlHttpUriFetcherTest.testSupportHurlHttpUri
 */
fun ComponentRegistry.Builder.supportHurlHttpUri(
    httpStack: HurlStack = Builder().build()
): ComponentRegistry.Builder = apply {
    addFetcher(HurlHttpUriFetcher.Factory(httpStack))
}

/**
 * HttpUriFetcher implementation using HttpUrlConnection as http request engine
 *
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpg' uri
 *
 * @see com.github.panpf.sketch.http.hurl.common.test.fetch.HurlHttpUriFetcherTest
 */
class HurlHttpUriFetcher(
    sketch: Sketch,
    httpStack: HurlStack,
    request: ImageRequest
) : HttpUriFetcher(sketch, httpStack, request) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HttpUriFetcher
        if (sketch != other.sketch) return false
        if (httpStack != other.httpStack) return false
        if (request != other.request) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + httpStack.hashCode()
        result = 31 * result + request.hashCode()
        return result
    }

    override fun toString(): String {
        return "HurlHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request)"
    }

    class Factory(val httpStack: HurlStack = Builder().build()) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri)) return null
            return HurlHttpUriFetcher(
                sketch = requestContext.sketch,
                httpStack = httpStack,
                request = request,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Factory
            if (httpStack != other.httpStack) return false
            return true
        }

        override fun hashCode(): Int {
            return httpStack.hashCode()
        }

        override fun toString(): String {
            return if (httpStack.interceptors.isNotEmpty()) {
                "HurlHttpUriFetcher(interceptors=${httpStack.interceptors})"
            } else {
                "HurlHttpUriFetcher"
            }
        }
    }
}