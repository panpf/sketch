package com.github.panpf.sketch.images

import com.github.panpf.sketch.ComponentRegistry
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.fetch.isHttpUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext

fun ResourceImageFile.toResourceHttpUri(): String {
    return "http://resource/${resourceName}"
}

/**
 * Adds KtorHttpUriFetcher support
 */
fun ComponentRegistry.Builder.supportResourcesHttpUri(
    context: PlatformContext
): ComponentRegistry.Builder = apply {
    addFetcher(ResourcesHttpUriFetcher.Factory(ResourcesHttpStack(context)))
}

/**
 * HttpUriFetcher implementation using Ktor as http request engine
 */
class ResourcesHttpUriFetcher(
    sketch: Sketch,
    httpStack: ResourcesHttpStack,
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
        return "KtorHttpUriFetcher(sketch=$sketch, httpStack=$httpStack, request=$request)"
    }

    class Factory(val httpStack: ResourcesHttpStack) : Fetcher.Factory {

        override fun create(requestContext: RequestContext): HttpUriFetcher? {
            val request = requestContext.request
            val uri = request.uri
            if (!isHttpUri(uri) || uri.authority != "resource") return null
            return ResourcesHttpUriFetcher(
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

        override fun toString(): String = "ResourcesHttpUriFetcher"
    }
}