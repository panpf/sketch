package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.source.toByteArray
import com.github.panpf.sketch.util.Size

suspend fun ImageRequest.fetch(sketch: Sketch, factory: Fetcher.Factory? = null): FetchResult {
    val requestContext = toRequestContext(sketch, Size.Empty)
    val fetcher =
        factory?.create(requestContext) ?: sketch.components.newFetcherOrThrow(requestContext)
    return fetcher.fetch().getOrThrow()
}

suspend fun readBytes(sketch: Sketch, imageUri: String): ByteArray {
    val request = ImageRequest(sketch.context, imageUri)
    val requestContext = RequestContext(sketch, request, Size.Empty)
    val fetcher = sketch.components.newFetcherOrThrow(requestContext)
    val fetchResult = fetcher.fetch().getOrThrow()
    return fetchResult.dataSource.toByteArray()
}