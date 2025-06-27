package com.github.panpf.sketch.fetch

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.http.OkHttpStack
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext

/**
 * Adds OkHttpHttpUriFetcher support
 *
 * @see com.github.panpf.sketch.http.okhttp.common.test.fetch.OkHttpHttpUriFetcherTest.testSupportOkHttpHttpUri
 */
fun ComponentRegistry.Builder.supportOkHttpHttpUri(
    httpStack: OkHttpStack = OkHttpStack.Builder().build()
): ComponentRegistry.Builder = apply {
    addFetcher(OkHttpHttpUriFetcher.Factory(httpStack))
}

/**
 * HttpUriFetcher implementation using OkHttp as http request engine
 *
 * Support 'http://pexels.com/sample.jpg', 'https://pexels.com/sample.jpg' uri
 *
 * @see com.github.panpf.sketch.http.okhttp.common.test.fetch.OkHttpHttpUriFetcherTest
 */
class OkHttpHttpUriFetcher(
    sketch: Sketch,
    httpStack: OkHttpStack,
    request: ImageRequest,
    downloadCacheKey: String,
) : HttpUriFetcher(sketch, httpStack, request, downloadCacheKey) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as HttpUriFetcher
        if (sketch != other.sketch) return false
        if (httpStack != other.httpStack) return false
        if (request != other.request) return false
        if (downloadCacheKey != other.downloadCacheKey) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sketch.hashCode()
        result = 31 * result + httpStack.hashCode()
        result = 31 * result + request.hashCode()
        result = 31 * result + downloadCacheKey.hashCode()
        return result
    }

    override fun toString(): String {
        return "OkHttpHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request, downloadCacheKey='$downloadCacheKey')"
    }

    class Factory(val httpStack: OkHttpStack = OkHttpStack.Builder().build()) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri)) return null
            return OkHttpHttpUriFetcher(
                sketch = requestContext.sketch,
                httpStack = httpStack,
                request = request,
                downloadCacheKey = requestContext.downloadCacheKey,
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

        override fun toString(): String = buildString {
            append("OkHttpHttpUriFetcher(")
            val beginLength = length

            httpStack.okHttpClient.connectTimeoutMillis.takeIf { it != 10_000 }?.let {
                if (length > beginLength) append(",")
                append("connectTimeout=$it")
            }

            httpStack.okHttpClient.readTimeoutMillis.takeIf { it != 10_000 }?.let {
                if (length > beginLength) append(",")
                append("readTimeout=$it")
            }

            httpStack.okHttpClient.interceptors.takeIf { it.isNotEmpty() }?.let {
                if (length > beginLength) append(",")
                append("interceptors=$it")
            }

            httpStack.okHttpClient.networkInterceptors.takeIf { it.isNotEmpty() }?.let {
                if (length > beginLength) append(",")
                append("interceptors=$it")
            }

            append(")")
        }.let {
            if (it.endsWith("()")) {
                it.replace("()", "")
            } else {
                it
            }
        }
    }
}