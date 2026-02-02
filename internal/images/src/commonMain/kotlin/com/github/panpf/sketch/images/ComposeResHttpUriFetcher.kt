package com.github.panpf.sketch.images

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.isHttpUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext

fun ComposeResImageFile.toComposeResHttpUri(): String {
    return "http://resource/${name}"
}

/**
 * Adds ComposeResHttpUriFetcher support
 */
fun ComponentRegistry.Builder.supportComposeResHttpUri(
    context: PlatformContext
): ComponentRegistry.Builder = apply {
    add(ComposeResHttpUriFetcher.Factory(ComposeResHttpStack(context)))
}

/**
 * HttpUriFetcher implementation using compose resource images
 */
class ComposeResHttpUriFetcher(
    sketch: Sketch,
    httpStack: ComposeResHttpStack,
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
        return "ComposeResHttpUriFetcher(httpStack=$httpStack, request=$request, downloadCacheKey='$downloadCacheKey')"
    }

    class Factory(val httpStack: ComposeResHttpStack) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri) || uri.authority != "resource") return null
            return ComposeResHttpUriFetcher(
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

        override fun toString(): String = "ComposeResHttpUriFetcher"
    }
}