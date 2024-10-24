package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.isHttpUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext

class TestHttpUriFetcher(
    sketch: Sketch,
    httpStack: TestHttpStack,
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
        return "TestHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request)"
    }

    class Factory(
        private val context: PlatformContext,
        val readDelayMillis: Long? = null,
        val connectionDelayMillis: Long? = null,
    ) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri)) return null
            return TestHttpUriFetcher(
                sketch = requestContext.sketch,
                httpStack = TestHttpStack(context, readDelayMillis, connectionDelayMillis),
                request = request,
            )
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as Factory
            if (readDelayMillis != other.readDelayMillis) return false
            if (connectionDelayMillis != other.connectionDelayMillis) return false
            return true
        }

        override fun hashCode(): Int {
            var result = readDelayMillis?.hashCode() ?: 0
            result = 31 * result + (connectionDelayMillis?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String =
            "TestHttpUriFetcher(readDelayMillis=$readDelayMillis, connectionDelayMillis=$connectionDelayMillis)"
    }
}